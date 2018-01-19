package mem.sirius.example.java;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import mem.sirius.example.java.database.Session;
import mem.sirius.example.java.database.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class user_process {
    private static final Integer APP_ID = 6327207;
    private static final String CLIENT_SECRET = "LACdimcvRWOqsQJUrsOv";

    //TODO create user login and registration methods

    @RequestMapping(value = "/get_auth_token")
    public HashMap<String, Object> getAuthToken(@RequestParam(value = "token") String token) {
        HashMap<String, Object> a = new HashMap<>();

        System.out.println("New login/registration attempt");

        FirebaseToken decodedToken = null;
        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(token).get();
            if (decodedToken == null)
                throw new Exception("Bad token");
        } catch (Exception e) {
            e.printStackTrace();
            a.put("status", "fail");
            a.put("message", "invalid_token");
            return a;
        }
        String googleUID = decodedToken.getUid();


        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();
        MongoCollection<Document> sessionsCollection = App.memeAppDatabase.getSessionsCollection();


        Document googleUIDQuery = new User().setGoogleUID(googleUID).toDocument();
        if (usersCollection.count(googleUIDQuery) != 0) {
            MongoCursor<Document> cursor = usersCollection.find(googleUIDQuery).iterator();
            User user = new User(cursor.next());
            cursor.close();
            String authToken = new RandomString().nextString();
            sessionsCollection.insertOne(new Session(user.getId(), authToken).toDocument());//new Document("user_id", user.getId()).append("auth_token", authToken));

            a.put("status", "success");
            a.put("auth_token", authToken);
            return a;
        }

        String authToken = new RandomString().nextString();

        ObjectId userId = new ObjectId();
        usersCollection.insertOne(new User().setId(userId).setGoogleUID(googleUID).toDocument());
        sessionsCollection.insertOne(new Session(userId, authToken).toDocument());


        a.put("status", "success");
        a.put("auth_token", authToken);
        return a;
    }


    @RequestMapping(value = "/validate_session")
    public HashMap<String, Object> validateSession(@RequestParam(value = "auth_token") String authToken) {
        HashMap<String, Object> a = new HashMap<>();
        System.out.println("New session validation attempt");

        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();
        MongoCollection<Document> sessionsCollection = App.memeAppDatabase.getSessionsCollection();


        Document authTokenQuery = new Session().setAuthToken(authToken).toDocument();
        if (sessionsCollection.count(authTokenQuery) != 0) {
            MongoCursor<Document> cursor = sessionsCollection.find(authTokenQuery).iterator();
            Session session = new Session(cursor.next());
            cursor.close();
            User userIdQuery = new User().setId(session.getUserId());
            cursor = usersCollection.find(userIdQuery.toDocument()).iterator();

            User user = new User(cursor.next());
            cursor.close();

            if (user.getUsername() == null) {
                a.put("status", "void_username");
            }

            a.put("status", "success");
            a.put("user_id", user.getId().toHexString());
            a.put("auth_token", authToken);
            a.put("username", user.getUsername());

            return a;
        }

        a.put("status", "fail");
        a.put("message", "auth_token_is_invalid");
        return a;
    }

    @RequestMapping(value = "/kill_session")
    public HashMap<String, Object> killSession(@RequestParam(value = "auth_token") String authToken) {
        HashMap<String, Object> a = new HashMap<>();
        System.out.println("New session murder attempt");

        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();
        MongoCollection<Document> sessionsCollection = App.memeAppDatabase.getSessionsCollection();

        if (sessionsCollection.deleteMany(new Session().setAuthToken(authToken).toDocument()).getDeletedCount() != 0) {
            a.put("status", "success");
            a.put("message", "auth_token_is_invalid");
            return (a);
        }

        a.put("status", "fail");
        return (a);
    }


}
