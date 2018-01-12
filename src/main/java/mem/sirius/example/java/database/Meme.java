package mem.sirius.example.java.database;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Meme extends Documentable {
    private String url;
    private BsonTimestamp time;
    private ObjectId authorId;
    private ObjectId id;
    private String title;


    public ObjectId getId() {
        return id;
    }

    public BsonTimestamp getTime() {
        return time;
    }

    public ObjectId getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Meme setTime(BsonTimestamp time) {
        this.time = time;
        return this;
    }

    public Meme setAuthorId(ObjectId authorId) {
        this.authorId = authorId;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Meme setUrl(String url) {
        this.url = url;
        return this;
    }

    public Meme(String url) {
        this.url = url;
    }

    public Meme(String url, BsonTimestamp time) {
        this.url = url;
        this.time = time;
    }


    public Meme(Document document) {
        super(document);
    }

    public Document toDocument() {
        Document document = new Document();
        if (url != null)
            document.append("url", url);

        if (time != null)
            document.append("time", time);

        if (authorId != null)
            document.append("author_id", authorId);

        if (id != null)
            document.append("_id", id);

        if (title != null)
            document.append("title", title);

        return document;
    }

    public void parseFromDocument(Document document) {
        if (document.containsKey("url"))
            url = document.getString("url");

        if (document.containsKey("time"))
            time = document.get("time", BsonTimestamp.class);

        if (document.containsKey("author_id"))
            authorId = document.get("author_id", ObjectId.class);

        if (document.containsKey("_id"))
            id = document.get("_id", ObjectId.class);

        if (document.containsKey("title"))
            title = document.getString("title");
    }


}