package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import mem.sirius.example.java.database.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.TreeMap;

public class set_viewed {
    private TreeMap<String, String> links = new TreeMap<String, String>();

    public set_viewed(Request a) {
        links = a.links;
    }

    public Response getResponse() {
        TreeMap<String, Object> a = new TreeMap<>();

        String authToken = links.get("auth_token");
        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();

        User user = App.memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            return new Response(a);
        }

        ObjectId memeId = new ObjectId(links.get("meme_id"));
        user.setIsPostViewed(memeId, true);
        user.pushListOfViewed(memeId);

        Document userIdQuery = new User().setId(user.getId()).toDocument();
        usersCollection.updateOne(userIdQuery, new Document("$set", user.toDocument()));

        a.put("status", "success");
        return new Response(a);
    }
}
