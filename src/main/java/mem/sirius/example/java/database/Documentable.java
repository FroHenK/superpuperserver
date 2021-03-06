package mem.sirius.example.java.database;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class Documentable {
    public abstract Document toDocument();

    public abstract void parseFromDocument(Document document);

    public Documentable(Document document) {
        this();
        this.parseFromDocument(document);
    }

    public Documentable() {
    }

    public static BsonTimestamp toBsonTimestamp(Long dateTime) {
        if (dateTime == null)
            return null;
        return new BsonTimestamp(dateTime);
    }

    public static Long fromBsonTimestamp(BsonTimestamp timestamp) {
        if (timestamp == null)
            return null;
        return timestamp.getValue();
    }

    public static ObjectId toObjectId(String id) {
        if (id == null)
            return null;
        return new ObjectId(id);
    }

    public static String fromObjectId(ObjectId objectId) {
        if (objectId == null)
            return null;
        return objectId.toHexString();
    }

}
