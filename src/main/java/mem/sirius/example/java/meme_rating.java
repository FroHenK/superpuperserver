package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.User;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.TreeMap;

public class meme_rating {
    private TreeMap<String, String> links = new TreeMap<String, String>();

    public meme_rating(Request a) {
        links = a.links;
    }

    public Response getResponse() {
        TreeMap<String, String> a = new TreeMap<String, String>();
        ObjectId memeId = new ObjectId(links.get("meme_id"));
        Meme memeQuery = new Meme().setId(memeId);
        Meme meme = new Meme(App.memeAppDatabase.getMemesCollection().find(memeQuery.toDocument()).first());

        if (links.get("action").equals("change")) {
            String authToken = links.get("auth_token");
            Integer newValue = Integer.valueOf(links.get("new_value"));
            MongoCollection<Document> memesCollection = App.memeAppDatabase.getMemesCollection();
            MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();

            User user = App.memeAppDatabase.getUserByAuthToken(authToken);
            if (user == null || Math.abs(newValue) > 1) {
                a.put("status", "fail");
                return new Response(a);
            }


            meme.setRating(meme.getRating() - user.getIsPostLiked(memeId));
            user.setIsPostLiked(memeId, newValue);
            meme.setRating(meme.getRating() + user.getIsPostLiked(memeId));

            Document memeIdQuery = new Meme().setId(meme.getId()).toDocument();
            memesCollection.updateOne(memeIdQuery, new Document("$set", new Document("rating", meme.getRating())));

            Document userIdQuery = new User().setId(user.getId()).toDocument();
            usersCollection.updateOne(userIdQuery, new Document("$set", user.toDocument()));
        }
        if (links.get("action").equals("get")) {
            a.put("rating", meme.getRating().toString());
        }
        a.put("status", "success");
        return new Response(a);
    }

}
