package mem.sirius.example.java.database;

import org.bson.Document;
import org.bson.types.ObjectId;

public class User extends Documentable {

    private ObjectId id;
    private Integer vkUserId;
    private String username;


    public ObjectId getId() {
        return id;
    }

    public User setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public Integer getVkUserId() {
        return vkUserId;
    }

    public User setVkUserId(Integer vkUserId) {
        this.vkUserId = vkUserId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public User(Document document) {
        super(document);
    }

    public User() {
    }

    public Document toDocument() {
        Document document = new Document();
        if (id != null)
            document.append("_id", id);

        if (vkUserId != null)
            document.append("vk_user_id", vkUserId);

        if (username != null)
            document.append("username", username);

        return document;
    }

    public void parseFromDocument(Document document) {
        if (document.containsKey("_id"))
            id = document.getObjectId("_id");

        if (document.containsKey("vk_user_id"))
            vkUserId = document.getInteger("vk_user_id");

        if (document.containsKey("username"))
            username = document.getString("username");

    }


}
