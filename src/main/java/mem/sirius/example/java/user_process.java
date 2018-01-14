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

import java.util.ArrayList;
import java.util.TreeMap;

public class user_process {
    private static final Integer APP_ID = 6327207;
    private static final String CLIENT_SECRET = "LACdimcvRWOqsQJUrsOv";
    private TreeMap<String, String> links = new TreeMap<String, String>();

    public user_process(Request a) {
        links = a.links;
    }

    public Response vkLoginOrCreate() {
        TreeMap<String, ArrayList<String>> a = new TreeMap<String, ArrayList<String>>();
        //links contains username, vk_token
        System.out.println("New vk login/registration attempt");
        System.out.println(links);
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
            ArrayList<String> status_value = new ArrayList<String>();
            status_value.add("fail");// Thank you
            a.put("status", status_value);
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

            a.put("status", new OneElementArrayList<String>("login"));
            a.put("user_id", new OneElementArrayList<String>(user.getId().toString()));
            a.put("auth_token", new OneElementArrayList<String>(authToken));
            a.put("username", new OneElementArrayList<String>(username));
            return new Response(a);
        }
        if (usersCollection.count(new User().setUsername(username).toDocument()) != 0) {
            a.put("status", new OneElementArrayList<String>("fail"));
            a.put("message", new OneElementArrayList<String>("nickname_occupied"));
            return new Response(a);
        }

        String authToken = new RandomString().nextString();

        ObjectId userId = new ObjectId();
        usersCollection.insertOne(new User().setId(userId).setUsername(username).setVkUserId(vkUserId).toDocument());
        sessionsCollection.insertOne(new Session(userId, authToken).toDocument());


        a.put("status", new OneElementArrayList<String>("success"));
        a.put("user_id", new OneElementArrayList<String>(userId.toString()));
        a.put("auth_token", new OneElementArrayList<String>(authToken));
        a.put("username", new OneElementArrayList<String>(username));

        //user_id; status "success","fail"; auth_token;username
        return new Response(a);
    }

    public Response validateSession() {
        TreeMap<String, ArrayList<String>> a = new TreeMap<String, ArrayList<String>>();
        System.out.println("New session validation attempt");
        System.out.println(links);
        String authToken = links.get("auth_token");

        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();
        MongoCollection<Document> sessionsCollection = App.memeAppDatabase.getSessionsCollection();


        if (sessionsCollection.count(new Session().setAuthToken(authToken).toDocument()) != 0) {
            MongoCursor<Document> cursor = sessionsCollection.find(new Session().setAuthToken(authToken).toDocument()).iterator();
            Session session = new Session(cursor.next());
            cursor.close();
            cursor = usersCollection.find(new User().setId(session.getUserId()).toDocument()).iterator();

            User user = new User(cursor.next());
            cursor.close();

            a.put("status", new OneElementArrayList<String>("success"));
            a.put("user_id", new OneElementArrayList<String>(user.getId().toString()));
            a.put("auth_token", new OneElementArrayList<String>(authToken));

            return new Response(a);
        }

        a.put("status", new OneElementArrayList<String>("fail"));
        return new Response(a);
    }

    public Response killSession() {
        TreeMap<String, ArrayList<String>> a = new TreeMap<String, ArrayList<String>>();
        System.out.println("New session murder attempt");
        System.out.println(links);
        String authToken = links.get("auth_token");

        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();
        MongoCollection<Document> sessionsCollection = App.memeAppDatabase.getSessionsCollection();


        if (sessionsCollection.deleteMany(new Session().setAuthToken(authToken).toDocument()).getDeletedCount() != 0) {

            a.put("status", new OneElementArrayList<String>("success"));
            return new Response(a);
        }

        a.put("status", new OneElementArrayList<String>("fail"));
        return new Response(a);
    }


}
