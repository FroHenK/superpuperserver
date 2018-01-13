package mem.sirius.example.java.database;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Comment extends Documentable {
    public Comment(Document document) {
        super(document);
    }

    public Comment() {
    }

    private ObjectId id;
    private ObjectId memeId;
    private ObjectId parentCommentId;
    private ObjectId authorId;
    private String text;
    private BsonTimestamp time;

    public ObjectId getId() {
        return id;
    }

    public ObjectId getMemeId() {
        return memeId;
    }

    public ObjectId getParentCommentId() {
        return parentCommentId;
    }

    public ObjectId getAuthorId() {
        return authorId;
    }

    public String getText() {
        return text;
    }

    public BsonTimestamp getTime() {
        return time;
    }


    public Comment setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public Comment setParentCommentId(ObjectId parentCommentId) {
        this.parentCommentId = parentCommentId;
        return this;
    }

    public Comment setMemeId(ObjectId memeId) {
        this.memeId = memeId;
        return this;
    }

    public Comment setAuthorId(ObjectId authorId) {
        this.authorId = authorId;
        return this;
    }

    public Comment setText(String text) {
        this.text = text;
        return this;
    }

    public Comment setTime(BsonTimestamp time) {
        this.time = time;
        return this;
    }

    public Document toDocument() {
        Document document = new Document();
        if (id != null)
            document.append("_id", id);

        if (parentCommentId != null)
            document.append("parent_comment", parentCommentId);

        if (authorId != null)
            document.append("author_id", authorId);

        if (memeId != null)
            document.append("meme_id", memeId);

        if (text != null)
            document.append("text", text);

        if (time != null)
            document.append("time", time);

        return document;
    }

    public void parseFromDocument(Document document) {
        if (document.containsKey("_id"))
            id = document.getObjectId("_id");

        if (document.containsKey("parent_comment"))
            parentCommentId = document.getObjectId("parent_comment");

        if (document.containsKey("author_id"))
            authorId = document.getObjectId("author_id");

        if (document.containsKey("meme_id"))
            memeId = document.getObjectId("meme_id");

        if (document.containsKey("text"))
            text = document.getString("text");

        if (document.containsKey("time"))
            time = document.get("time", BsonTimestamp.class);
    }
}
