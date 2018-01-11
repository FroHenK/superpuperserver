package mem.sirius.example.java.database;

import org.bson.Document;
import org.bson.types.BSONTimestamp;

public class Meme extends Documentable {
    private String url;
    private BSONTimestamp time;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        return document;
    }

    public void parseFromDocument(Document document) {
        url = document.getString("url");
        if (document.containsKey("datetime")) {
            time = document.get("time", BSONTimestamp.class);
        }

    }
}