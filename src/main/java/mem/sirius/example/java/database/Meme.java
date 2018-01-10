package mem.sirius.example.java.database;

import org.bson.Document;

public class Meme extends Documentable {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Meme(String url) {
        this.url = url;
    }

    public Meme(Document document) {
        super(document);
    }

    public Document toDocument() {
        return new Document().append("url", url);
    }

    public void parseFromDocument(Document document) {
        url = document.getString("url");
    }
}