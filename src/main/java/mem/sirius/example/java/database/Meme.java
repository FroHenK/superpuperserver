package mem.sirius.example.java.database;

import org.bson.Document;
import org.bson.types.BSONTimestamp;
import org.bson.types.ObjectId;

public class Meme extends Documentable {
    private String url;
    private BSONTimestamp time;
    private ObjectId authorId;
    private ObjectId id;

    public BSONTimestamp getTime() {
        return time;
    }

    public ObjectId getAuthorId() {
        return authorId;
    }

    public String getUrl() {
        return url;
    }

    public Meme setTime(BSONTimestamp time) {
        this.time = time;
        return this;
    }

    public Meme setAuthorId(ObjectId authorId) {
        this.authorId = authorId;
        return this;
    }

    public Meme setUrl(String url) {
        this.url = url;
        return this;
    }

    public Meme(String url) {
        this.url = url;
    }

    public Meme(String url, BSONTimestamp time) {
        this.url = url;
        this.time = time;
    }


    public Meme(Document document) {
        super(document);
    }

    public Document toDocument() {
        Document document = new Document().append("url", this.url);
        if (time != null)
            document.append("time", time);
        if (authorId != null)
            document.append("author_id", authorId);
        if (id != null)
            document.append("_id", id);

        return document;
    }

    public void parseFromDocument(Document document) {
        url = document.getString("url");
        if (document.containsKey("datetime")) {
            time = document.get("time", BSONTimestamp.class);
        }
        if (document.containsKey("author_id")) {
            authorId = document.get("author_id", ObjectId.class);
        }
        if (document.containsKey("_id")) {
            id = document.get("_id", ObjectId.class);
        }


    }

    public ObjectId getId() {
        return id;
    }

}