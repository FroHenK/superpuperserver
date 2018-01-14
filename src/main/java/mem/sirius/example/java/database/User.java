package mem.sirius.example.java.database;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class User extends Documentable {

    private ObjectId id;
    private Integer vkUserId;
    private String username;
    private Map<String, Integer> isLiked;
    private Set<String> isViewed;
    private ArrayList<String> listOfViewed;

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

    public Set<String> getIsViewed() {
        return isViewed;
    }

    public Boolean getIsPostViewed(ObjectId post) {
        if (this.isViewed.contains(post.toHexString())) return true;
        else return false;
    }

    public ArrayList<String> getListOfViewed() {
        return listOfViewed;
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

    public User setIsLiked(Map<String, Integer> isLiked) {
        this.isLiked = isLiked;
        return this;
    }

    public User setIsPostLiked(ObjectId post, Integer new_value) {
        this.isLiked.put(post.toHexString(), new_value);
        return this;
    }

    public User setIsViewed(Set<String> isViewed) {
        this.isViewed = isViewed;
        return this;
    }

    public User setIsPostViewed(ObjectId post, boolean add) {
        if (add) {
            this.isViewed.add(post.toHexString());
        } else {
            this.isViewed.remove(post.toHexString());
        }
        return this;
    }

    public User setListOfViewed(ArrayList<String> listOfViewed) {
        this.listOfViewed = listOfViewed;
        return this;
    }

    public User pushListOfViewed(ObjectId post) {
        this.listOfViewed.add(post.toHexString());
        return this;
    }

    public User(Document document) {
        super(document);
        if (isLiked == null)
            isLiked = new HashMap<>();
        if (isViewed == null)
            isViewed = new TreeSet<>();
        if (listOfViewed == null)
            listOfViewed = new ArrayList<>();
    }

    public User() {
        super();
        if (isLiked == null)
            isLiked = new HashMap<>();
        if (isViewed == null)
            isViewed = new TreeSet<>();
        if (listOfViewed == null)
            listOfViewed = new ArrayList<>();
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

        if (isViewed != null && !isViewed.isEmpty())
            document.append("is_viewed", isViewed);

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

        if (document.containsKey("is_viewed"))
            isViewed = document.get("is_viewed", Set.class);

        if (document.containsKey("list_of_viewed"))
            listOfViewed = document.get("list_of_viewed", ArrayList.class);

    }


}
