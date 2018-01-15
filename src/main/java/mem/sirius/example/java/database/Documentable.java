package mem.sirius.example.java.database;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class Documentable {
    public abstract Document toDocument();

    public abstract void parseFromDocument(Document document);

    public Documentable(Document document) {
        this();
        this.parseFromDocument(document);
    }

    public Documentable() {
    }

    public static BsonTimestamp toBsonTimestamp(LocalDateTime dateTime) {
        if (dateTime == null)
            return null;
        return new BsonTimestamp(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public static LocalDateTime fromBsonTimestamp(BsonTimestamp timestamp) {
        if (timestamp == null)
            return null;
        return Instant.ofEpochMilli(timestamp.getValue()).atZone(ZoneId.systemDefault()).toLocalDateTime();
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
