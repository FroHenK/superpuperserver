package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import mem.sirius.example.java.database.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class set_viewed {

    @RequestMapping(value = "/set_viewed")
    public HashMap<String, Object> getResponse(@RequestParam(value = "auth_token") String authToken,
                                               @RequestParam(value = "meme_id") String memeId) {
        HashMap<String, Object> a = new HashMap<>();

        MongoCollection<Document> usersCollection = App.memeAppDatabase.getUsersCollection();

        User user = App.memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "auth_token_is_invalid");
            return (a);
        }

        ObjectId meme_id = new ObjectId(memeId);
        user.setIsPostViewed(meme_id, true);
        user.pushListOfViewed(meme_id);

        Document userIdQuery = new User().setId(user.getId()).toDocument();
        usersCollection.updateOne(userIdQuery, new Document("$set", user.toDocument()));

        a.put("status", "success");
        return (a);
    }
}
