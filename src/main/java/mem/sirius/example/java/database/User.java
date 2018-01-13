package mem.sirius.example.java.database;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.Map;

public class User extends Documentable {

    private ObjectId id;
    private Integer vkUserId;
    private String username;
    private Map<String, Integer> isLiked;

    public ObjectId getId() {
        return id;
    }

    public Integer getVkUserId() {
        return vkUserId;
    }

    public String getUsername() {
        return username;
    }

    public Map<String, Integer> getIsLiked() {
        return isLiked;
    }

    public Integer getIsPostLiked(ObjectId post) {
        return this.isLiked.getOrDefault(post.toHexString(), 0);
    }

    public User setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public User setVkUserId(Integer vkUserId) {
        this.vkUserId = vkUserId;
        return this;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User setIsLiked(HashMap<String, Integer> isLiked) {
        this.isLiked = isLiked;
        return this;
    }

    public void setIsPostLiked(ObjectId post, Integer new_value) {
        this.isLiked.put(post.toHexString(), new_value);
    }

    public User(Document document) {
        super(document);
        if (isLiked == null)
            isLiked = new HashMap<>();
    }

    public User() {
        super();
        if (isLiked == null)
            isLiked = new HashMap<>();
    }

    public Document toDocument() {
        Document document = new Document();
        if (id != null)
            document.append("_id", id);

        if (vkUserId != null)
            document.append("vk_user_id", vkUserId);

        if (username != null)
            document.append("username", username);

        if (username != null)
            document.append("username", username);

        if (isLiked != null && !isLiked.isEmpty())
            document.append("is_liked", isLiked);

        return document;
    }

    public void parseFromDocument(Document document) {
        if (document.containsKey("_id"))
            id = document.getObjectId("_id");

        if (document.containsKey("vk_user_id"))
            vkUserId = document.getInteger("vk_user_id");

        if (document.containsKey("username"))
            username = document.getString("username");

        if (document.containsKey("is_liked"))
            isLiked = document.get("is_liked", Map.class);

    }


}
