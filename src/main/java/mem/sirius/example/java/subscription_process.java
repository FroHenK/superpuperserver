package mem.sirius.example.java;


import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import static mem.sirius.example.java.App.memeAppDatabase;

@RestController
public class subscription_process {
    @RequestMapping(value = "/subscribe")
    public HashMap<String, Object> subscribe(@RequestParam(value = "auth_token") String authToken,
                                             @RequestParam(value = "user_id") String user_id) {
        HashMap<String, Object> a = new HashMap<>();

        ObjectId userId = new ObjectId(user_id);

        User user = memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "auth_token_is_invalid");

            return a;
        }
        if (memeAppDatabase.getUsersCollection().count(new User().setId(userId).toDocument()) == 0) {
            a.put("status", "fail");
            a.put("message", "no_such_user");
            return a;
        }
        user.subscriptions.add(user_id);

        Document userIdQuery = new User().setId(user.getId()).toDocument();
        memeAppDatabase.getUsersCollection().updateOne(userIdQuery, new Document("$set", user.toDocument()));

        a.put("status", "success");
        a.put("message", "successful_subscription");
        return a;
    }

    @RequestMapping(value = "/unsubscribe")
    public HashMap<String, Object> unsubscribe(@RequestParam(value = "auth_token") String authToken,
                                               @RequestParam(value = "user_id") String user_id) {
        HashMap<String, Object> a = new HashMap<>();

        ObjectId userId = new ObjectId(user_id);

        User user = memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "auth_token_is_invalid");

            return a;
        }
        if (memeAppDatabase.getUsersCollection().count(new User().setId(userId).toDocument()) == 0) {
            a.put("status", "fail");
            a.put("message", "no_such_user");
            return a;
        }
        user.subscriptions.remove(user_id);

        Document userIdQuery = new User().setId(user.getId()).toDocument();
        memeAppDatabase.getUsersCollection().updateOne(userIdQuery, new Document("$set", user.toDocument()));

        a.put("status", "success");
        a.put("message", "successful_subscription");
        return a;
    }

    @RequestMapping(value = "/get_subscriptions_list")
    public HashMap<String, Object> get_subscriptions_list(@RequestParam(value = "auth_token") String authToken,
                                                          @RequestParam(value = "count") Integer count,
                                                          @RequestParam(value = "last") String last,
                                                          @RequestParam(value = "amoral", defaultValue = "false") Boolean amoral) {
        if (Objects.equals(last, "null"))
            last = null;

        HashMap<String, Object> a = new HashMap<>();

        ArrayList<Meme> memes = new ArrayList<>();

        User user = memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "auth_token_is_invalid");

            return a;
        }


        MongoCollection<Document> memesCollection = memeAppDatabase.getMemesCollection();

        FindIterable<Document> sort = memesCollection.find().sort(new Document("_id", -1));
        if (last != null)
            sort = sort.filter(Filters.lt("_id", new ObjectId(last)));
        MongoCursor<Document> cursor = sort.iterator();
        Set<String> viewed = user.getIsViewed();
        while (cursor.hasNext() && memes.size() < count) {
            Meme meme = new Meme(cursor.next());
            if ((amoral || !meme.isAmoral) && (!viewed.contains(meme.getId().toHexString())) && user.subscriptions.contains(meme.getAuthorId().toHexString())) {
                memes.add(meme);
            }
        }


        HashMap<String, String> usernamesMap = new HashMap<>();
        //assigning our rating to posts, so we could display that properly
        HashMap<String, Integer> isRatedByUserMap = new HashMap<>();

        for (Meme meme :
                memes) {
            usernamesMap.put(meme.getAuthorId().toHexString(), null);
            isRatedByUserMap.put(meme.getId().toHexString(), user.getIsPostLiked(meme.getId()));
        }

        memeAppDatabase.assignUsernamesToIds(usernamesMap);


        a.put("status", "success");
        a.put("links", memes);
        a.put("usernames", usernamesMap);
        a.put("likes", isRatedByUserMap);
        return a;
    }

}
