package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import mem.sirius.example.java.database.Comment;
import mem.sirius.example.java.database.User;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class comment_process {
    public static final int MAX_COMMENT_LENGTH = 300;


    //accepts meme_id
    @RequestMapping(value = "/get_comments")
    public HashMap getComments(@RequestParam(value = "meme_id") String memeId) {
        System.out.println(memeId);
        HashMap map = new HashMap();
        ArrayList<Comment> comments = App.memeAppDatabase.commentsList(new ObjectId(memeId));

        HashMap<ObjectId, String> usernamesMap = new HashMap<ObjectId, String>();

        for (Comment comment :
                comments) {
            usernamesMap.put(comment.getAuthorId(), null);
        }

        App.memeAppDatabase.assignUsernamesToIds(usernamesMap);

        map.put("status", "success");
        map.put("comments", comments);
        map.put("usernames", usernamesMap);
        //document.put("comment",new Comment().setTe
        return map;
    }

    //accepts auth_token, meme_id, text, parent_id(optional)
    @RequestMapping(value = "/post_comments")
    public HashMap postComment(
            @RequestParam(value = "auth_token") String authToken,
            @RequestParam(value = "meme_id") String meme_id,
            @RequestParam(value = "text") String text,
            @RequestParam(value = "parent_id", defaultValue = "null") String parent_id) {

        if (parent_id.equals("null"))
            parent_id = null;

        HashMap a = new HashMap();

        User user = App.memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "invalid_auth_token");
            return a;
        }

        if (text.length() > MAX_COMMENT_LENGTH) {
            a.put("status", "fail");
            a.put("message", "lol_tractor_driver");
        }

        MongoCollection<Document> commentsCollection = App.memeAppDatabase.getCommentsCollection();

        ObjectId memeId = new ObjectId(meme_id);


        ObjectId parentId = null;

        if (parent_id != null) {
            parentId = new ObjectId(parent_id);
            Document commentQuery = new Comment().setId(parentId).setMemeId(memeId).toDocument();
            if (commentsCollection.count(commentQuery) == 0) {
                a.put("status", "fail");
                a.put("message", "parent_comment_does_not_exist");
                return a;
            }
        }

        Comment comment = new Comment().setAuthorId(user.getId()).setText(text).setTime(new BsonTimestamp()).setMemeId(memeId);
        comment.setParentCommentId(parentId);
        commentsCollection.insertOne(comment.toDocument());

        a.put("status", "success");

        return a;
    }

}
