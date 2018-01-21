package mem.sirius.example.java.database;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;

@Deprecated //and unused
public class MemeView extends Documentable {
    private String id;
    private String memeId;
    private String userId;
    private Long time;

    public ObjectId getId() {
        return toObjectId(id);
    }

    public ObjectId getMemeId() {
        return toObjectId(memeId);
    }

    public ObjectId getUserId() {
        return toObjectId(userId);
    }

    public BsonTimestamp getTime() {
        return toBsonTimestamp(time);
    }

    public MemeView setId(ObjectId id) {
        this.id = fromObjectId(id);
        return this;
    }

    public MemeView setMemeId(ObjectId memeId) {
        this.memeId = fromObjectId(memeId);
        return this;
    }

    public MemeView setUserId(ObjectId userId) {
        this.userId = fromObjectId(userId);
        return this;
    }

    public MemeView setTime(BsonTimestamp time) {
        this.time = fromBsonTimestamp(time);
        return this;
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        if (getId() != null)
            document.put("_id", getId());

        if (getMemeId() != null)
            document.put("meme_id", getMemeId());

        if (getUserId() != null)
            document.put("user_id", getUserId());

        if (getTime() != null)
            document.put("time", getTime());

        return null;
    }

    @Override
    public void parseFromDocument(Document document) {
        if (document.containsKey("_id"))
            setId(document.getObjectId("_id"));

        if (document.containsKey("meme_id"))
            setMemeId(document.getObjectId("meme_id"));

        if (document.containsKey("user_id"))
            setUserId(document.getObjectId("user_id"));

        if (document.containsKey("time"))
            setTime(document.get("time", BsonTimestamp.class));
    }
}
