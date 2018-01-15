package mem.sirius.example.java.database;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class Comment extends Documentable {
    public Comment(Document document) {
        super(document);
    }

    public Comment() {
    }

    private String id;
    private String memeId;
    private String parentCommentId;
    private String authorId;
    private String text;
    private LocalDateTime time;

    public ObjectId getId() {
        return toObjectId(id);
    }

    public ObjectId getMemeId() {
        return toObjectId(memeId);
    }

    public ObjectId getParentCommentId() {
        return toObjectId(parentCommentId);
    }

    public ObjectId getAuthorId() {
        return toObjectId(authorId);
    }

    public String getText() {
        return text;
    }

    public BsonTimestamp getTime() {
        return toBsonTimestamp(time);
    }


    public Comment setId(ObjectId id) {
        this.id = fromObjectId(id);
        return this;
    }

    public Comment setParentCommentId(ObjectId parentCommentId) {
        this.parentCommentId = fromObjectId(parentCommentId);
        return this;
    }

    public Comment setMemeId(ObjectId memeId) {
        this.memeId = fromObjectId(memeId);
        return this;
    }

    public Comment setAuthorId(ObjectId authorId) {
        this.authorId = fromObjectId(authorId);
        return this;
    }

    public Comment setText(String text) {
        this.text = text;
        return this;
    }

    public Comment setTime(BsonTimestamp time) {
        this.time = fromBsonTimestamp(time);
        return this;
    }

    public Document toDocument() {
        Document document = new Document();
        if (id != null)
            document.append("_id", toObjectId(id));

        if (parentCommentId != null)
            document.append("parent_comment", toObjectId(parentCommentId));

        if (authorId != null)
            document.append("author_id", toObjectId(authorId));

        if (memeId != null)
            document.append("meme_id", toObjectId(memeId));

        if (text != null)
            document.append("text", text);

        if (time != null)
            document.append("time", toBsonTimestamp(time));

        return document;
    }

    public void parseFromDocument(Document document) {
        if (document.containsKey("_id"))
            id = fromObjectId(document.getObjectId("_id"));

        if (document.containsKey("parent_comment"))
            parentCommentId = fromObjectId(document.getObjectId("parent_comment"));

        if (document.containsKey("author_id"))
            authorId = fromObjectId(document.getObjectId("author_id"));

        if (document.containsKey("meme_id"))
            memeId = fromObjectId(document.getObjectId("meme_id"));

        if (document.containsKey("text"))
            text = document.getString("text");

        if (document.containsKey("time"))
            time = fromBsonTimestamp(document.get("time", BsonTimestamp.class));
    }
}
