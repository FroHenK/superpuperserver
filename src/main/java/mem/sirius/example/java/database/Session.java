package mem.sirius.example.java.database;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Session extends Documentable {
    private ObjectId id;

    public ObjectId getId() {
        return id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public Session setUserId(ObjectId userId) {
        this.userId = userId;
        return this;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Session(Document document) {
        super(document);
    }

    public Session() {
    }

    public Session setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }

    private ObjectId userId;
    private String authToken;


    public Document toDocument() {
        Document document = new Document();
        document.append("auth_token", authToken);
        document.append("user_id", userId);
        if (id != null)
            document.append("_id", id);
        return document;
    }

    public void parseFromDocument(Document document) {
        authToken = document.getString("auth_token");
        id = document.getObjectId("_id");
        userId = document.getObjectId("user_id");

    }
}
