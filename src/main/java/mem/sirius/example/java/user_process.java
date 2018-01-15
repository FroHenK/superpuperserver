package mem.sirius.example.java;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import mem.sirius.example.java.database.Session;
import mem.sirius.example.java.database.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.TreeMap;

@RestController
public class user_process {
    private static final Integer APP_ID = 6327207;
    private static final String CLIENT_SECRET = "LACdimcvRWOqsQJUrsOv";
    private TreeMap<String, String> links = new TreeMap<String, String>();

    public Response vkLoginOrCreate() {
        TreeMap<String, Object> a = new TreeMap<>();
        //links contains username, vk_token
        System.out.println("New vk login/registration attempt");
        String vkCode = links.get("vk_code");
        String username = links.get("username");


        TransportClient transportClient = HttpTransportClient.getInstance();
        VkApiClient vk = new VkApiClient(transportClient, new Gson(), 666);//666!
        UserAuthResponse authResponse = null;
        try {

            authResponse = vk.oauth()
                    .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, "https://oauth.vk.com/blank.html", vkCode).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (authResponse == null) {
            a.put("status", "fail");
            return new Response(a);
        }
        Integer vkUserId = authResponse.getUserId();


        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();
        MongoCollection<Document> sessionsCollection = App.memeAppDatabase.getSessionsCollection();


        if (usersCollection.count(new User().setVkUserId(vkUserId).toDocument()) != 0) {
            MongoCursor<Document> cursor = usersCollection.find(new User().setVkUserId(vkUserId).toDocument()).iterator();
            User user = new User(cursor.next());
            cursor.close();
            username = user.getUsername();
            String authToken = new RandomString().nextString();
            sessionsCollection.insertOne(new Session(user.getId(), authToken).toDocument());//new Document("user_id", user.getId()).append("auth_token", authToken));

            a.put("status", "login");
            a.put("user_id", user.getId().toHexString());
            a.put("auth_token", authToken);
            a.put("username", username);
            return new Response(a);
        }
        if (usersCollection.count(new User().setUsername(username).toDocument()) != 0) {
            a.put("status", "fail");
            a.put("message", "nickname_occupied");
            return new Response(a);
        }

        String authToken = new RandomString().nextString();

        ObjectId userId = new ObjectId();
        usersCollection.insertOne(new User().setId(userId).setUsername(username).setVkUserId(vkUserId).toDocument());
        sessionsCollection.insertOne(new Session(userId, authToken).toDocument());


        a.put("status", "success");
        a.put("user_id", userId.toHexString());
        a.put("auth_token", authToken);
        a.put("username", username);

        //user_id; status "success","fail"; auth_token;username
        return new Response(a);
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
