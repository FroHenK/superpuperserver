package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class meme_rating {

    @RequestMapping(value = "/rate_meme")
    public HashMap<String, Object> getResponse(@RequestParam(value = "auth_token") String authToken,
                                               @RequestParam(value = "meme_id") String memeIdString,
                                               @RequestParam(value = "new_value") Integer newValue) {
        HashMap<String, Object> a = new HashMap<>();

        ObjectId memeId = new ObjectId(memeIdString);

        Meme memeQuery = new Meme().setId(memeId);
        Meme meme = new Meme(App.memeAppDatabase.getMemesCollection().find(memeQuery.toDocument()).first());

        MongoCollection<Document> memesCollection = App.memeAppDatabase.getMemesCollection();
        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();

        User user = App.memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "auth_token_is_invalid");
            return a;
        }
        if (Math.abs(newValue) > 1) {
            a.put("status", "fail");
            a.put("message", "incorrect_new_value");
            return a;
        }

        meme.setRating(meme.getRating() - user.getIsPostLiked(memeId));
        user.setIsPostLiked(memeId, newValue);
        meme.setRating(meme.getRating() + user.getIsPostLiked(memeId));

        Document memeIdQuery = new Meme().setId(meme.getId()).toDocument();
        memesCollection.updateOne(memeIdQuery, new Document("$set", new Document("rating", meme.getRating())));
        HashMap<ObjectId, Meme> memesCache = App.memeAppDatabase.getMemesCache();
        if (memesCache.containsKey(meme.getId())) {
            memesCache.get(meme.getId()).setRating(meme.getRating());
        }

        Document userIdQuery = new User().setId(user.getId()).toDocument();
        usersCollection.updateOne(userIdQuery, new Document("$set", user.toDocument()));
        a.put("status", "success");
        return a;
    }

}
