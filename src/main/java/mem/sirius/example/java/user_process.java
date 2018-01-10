package mem.sirius.example.java;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
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


        if (usersCollection.count(new Document("vk_user_id", vkUserId)) != 0) {
            MongoCursor<Document> cursor = usersCollection.find(new Document("vk_user_id", vkUserId)).iterator();
            Document user = cursor.next();
            username = user.getString("username");
            String authToken = new RandomString().nextString();
            sessionsCollection.insertOne(new Document("user_id", user.getObjectId("_id")).append("auth_token", authToken));

            {
                ArrayList<String> value = new ArrayList<String>();
                value.add("login");
                a.put("status", value);
            }
            {
                ArrayList<String> value = new ArrayList<String>();
                value.add(user.getObjectId("_id").toString());
                a.put("user_id", value);
            }
            {
                ArrayList<String> value = new ArrayList<String>();
                value.add(authToken);
                a.put("auth_token", value);
            }
            {
                ArrayList<String> value = new ArrayList<String>();
                value.add(username);
                a.put("username", value);
            }

            return new Response(a);
        }


        String authToken = new RandomString().nextString();

        ObjectId objectId = new ObjectId();
        usersCollection.insertOne(new Document().append("vk_user_id", vkUserId).append("username", username).append("_id", objectId));
        sessionsCollection.insertOne(new Document("user_id", objectId).append("auth_token", authToken));

        {
            ArrayList<String> value = new ArrayList<String>();
            value.add("success");
            a.put("status", value);
        }
        {
            ArrayList<String> value = new ArrayList<String>();
            value.add(objectId.toString());
            a.put("user_id", value);
        }
        {
            ArrayList<String> value = new ArrayList<String>();
            value.add(authToken);
            a.put("auth_token", value);
        }
        {
            ArrayList<String> value = new ArrayList<String>();
            value.add(username);
            a.put("username", value);
        }

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


        if (sessionsCollection.count(new Document("auth_token", authToken)) != 0) {
            MongoCursor<Document> cursor = usersCollection.find(new Document("auth_token", authToken)).iterator();
            Document user = cursor.next();
            String username = user.getString("username");
            sessionsCollection.insertOne(new Document("user_id", user.getObjectId("_id")).append("auth_token", authToken));

            {
                ArrayList<String> value = new ArrayList<String>();
                value.add("success");
                a.put("status", value);
            }
            {
                ArrayList<String> value = new ArrayList<String>();
                value.add(user.getObjectId("_id").toString());
                a.put("user_id", value);
            }
            {
                ArrayList<String> value = new ArrayList<String>();
                value.add(authToken);
                a.put("auth_token", value);
            }

            {
                ArrayList<String> value = new ArrayList<String>();
                value.add(username);
                a.put("username", value);
            }

            return new Response(a);
        }

        {
            ArrayList<String> value = new ArrayList<String>();
            value.add("fail");
            a.put("status", value);
        }
        return new Response(a);
    }

    public Response killSession() {
        TreeMap<String, ArrayList<String>> a = new TreeMap<String, ArrayList<String>>();
        System.out.println("New session murder attempt");
        System.out.println(links);
        String authToken = links.get("auth_token");

        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();
        MongoCollection<Document> sessionsCollection = App.memeAppDatabase.getSessionsCollection();


        if (sessionsCollection.deleteMany(new Document("auth_token", authToken)).getDeletedCount() != 0) {
            {
                ArrayList<String> value = new ArrayList<String>();
                value.add("success");
                a.put("status", value);
            }
            return new Response(a);
        }
        {
            ArrayList<String> value = new ArrayList<String>();
            value.add("fail");
            a.put("status", value);
        }
        return new Response(a);
    }


}
