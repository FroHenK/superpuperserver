package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import mem.sirius.example.java.database.Comment;
import mem.sirius.example.java.database.User;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class comment_process {
    private TreeMap<String, String> links = new TreeMap<String, String>();

    public comment_process(Request a) {
        links = a.links;
    }


    //accepts meme_id
    public Response getComments() {
        String memeId = links.get("meme_id");
        TreeMap<String, Object> a = new TreeMap<String, Object>();
        ArrayList<Comment> comments = App.memeAppDatabase.commentsList(new ObjectId(memeId));
        HashMap<ObjectId, String> usernamesMap = new HashMap<ObjectId, String>();

        for (Comment comment :
                comments) {
            usernamesMap.put(comment.getAuthorId(), null);
        }

        App.memeAppDatabase.assignUsernamesToIds(usernamesMap);

        a.put("comments", comments);
        a.put("usernames", usernamesMap);

        return new Response(a);
    }

    //accepts auth_token, meme_id, text, parent_id(optional)
    public Response postComment() {
        TreeMap<String, Object> a = new TreeMap<String, Object>();

        String authToken = links.get("auth_token");

        User user = App.memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "invalid_auth_token");
            return new Response(a);
        }

        MongoCollection<Document> commentsCollection = App.memeAppDatabase.getCommentsCollection();

        ObjectId memeId = new ObjectId(links.get("meme_id"));
        ObjectId parentId = null;
        String text = links.get("text");

        if (text.length() > 300) {
            a.put("status", "fail");
            a.put("message", "lol_tractor_driver");
        }

        if (links.containsKey("parent_id")) {
            parentId = new ObjectId(links.get("parent_id"));
            Document commentQuery = new Comment().setId(parentId).setMemeId(memeId).toDocument();
            if (commentsCollection.count(commentQuery) == 0) {
                a.put("status", "fail");
                a.put("message", "parent_comment_does_not_exist");
                return new Response(a);
            }
        }

        Comment comment = new Comment().setAuthorId(user.getId()).setText(text).setTime(new BsonTimestamp()).setMemeId(memeId);
        comment.setParentCommentId(parentId);
        commentsCollection.insertOne(comment.toDocument());

        a.put("status", "success");

        return new Response(a);
    }

}
