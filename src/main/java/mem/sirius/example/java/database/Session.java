package mem.sirius.example.java.database;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;

public class Session extends Documentable implements Serializable {

    private String id;
    private String userId;
    private String authToken;

    public ObjectId getId() {
        return toObjectId(id);
    }

    public ObjectId getUserId() {
        return toObjectId(userId);
    }

    public String getAuthToken() {
        return authToken;
    }

    public Session setUserId(ObjectId userId) {
        this.userId = fromObjectId(userId);
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
        this.userId = fromObjectId(userId);
        this.authToken = authToken;
    }

    public Session() {
    }


    public Document toDocument() {
        Document document = new Document();
        if (authToken != null)
            document.append("auth_token", authToken);

        if (userId != null)
            document.append("user_id", toObjectId(userId));

        if (id != null)
            document.append("_id", toObjectId(id));

        return document;
    }

    public void parseFromDocument(Document document) {
        if (document.containsKey("auth_token"))
            authToken = document.getString("auth_token");

        if (document.containsKey("_id"))
            id = fromObjectId(document.getObjectId("_id"));

        if (document.containsKey("user_id"))
            userId = fromObjectId(document.getObjectId("user_id"));

    }
}
