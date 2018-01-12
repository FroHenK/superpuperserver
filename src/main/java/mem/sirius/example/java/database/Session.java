package mem.sirius.example.java.database;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Session extends Documentable {
    private ObjectId id;
    private ObjectId userId;
    private String authToken;

    public ObjectId getId() {
        return id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Session setUserId(ObjectId userId) {
        this.userId = userId;
        return this;
    }

    public Session setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }

    public Session(Document document) {
        super(document);
    }

    public Session(ObjectId userId, String authToken) {
        this.userId = userId;
        this.authToken = authToken;
    }

    public Session() {
    }


    public Document toDocument() {
        Document document = new Document();
        if (authToken != null)
            document.append("auth_token", authToken);

        if (userId != null)
            document.append("user_id", userId);

        if (id != null)
            document.append("_id", id);

        return document;
    }

    public void parseFromDocument(Document document) {
        if (document.containsKey("auth_token"))
            authToken = document.getString("auth_token");

        if (document.containsKey("_id"))
            id = document.getObjectId("_id");

        if (document.containsKey("user_id"))
            userId = document.getObjectId("user_id");

    }
}
