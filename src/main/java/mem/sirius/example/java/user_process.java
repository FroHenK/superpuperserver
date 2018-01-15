package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import mem.sirius.example.java.database.Session;
import mem.sirius.example.java.database.User;
import org.bson.Document;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class user_process {
    private static final Integer APP_ID = 6327207;
    private static final String CLIENT_SECRET = "LACdimcvRWOqsQJUrsOv";

    //TODO create user login and registration methods

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
