package mem.sirius.example.java.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;

public class MemeAppDatabase {
    private final String server;
    private final String user;
    private final String password;
    private final String salt;
    private final MongoCollection<Document> memesCollection;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> sessionsCollection;
    private final MongoCollection<Document> visitsCollection;

    public MongoCollection<Document> getUsersCollection() {
        return usersCollection;
    }

    public MongoCollection<Document> getMemesCollection() {
        return memesCollection;
    }

    public MongoCollection<Document> getSessionsCollection() {
        return sessionsCollection;
    }

    public MongoCollection<Document> getVisitsCollection() {
        return visitsCollection;
    }

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MemeAppDatabase(String server, String user, String password, String salt, String database) {
        this.server = server;
        this.user = user;
        this.password = password;
        this.salt = salt;

        mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb+srv://%s:%s@%s/test", user, password, server)));
        mongoDatabase = mongoClient.getDatabase(database);
        memesCollection = mongoDatabase.getCollection("memes");
        usersCollection = mongoDatabase.getCollection("users");
        sessionsCollection = mongoDatabase.getCollection("sessions");
        visitsCollection = mongoDatabase.getCollection("visits");
    }

    public ArrayList<Meme> memesList(Integer last, Integer num) {
        ArrayList<Meme> memesList = new ArrayList<Meme>();
        MongoCursor<Document> cursor = memesCollection.find().skip(last).limit(num).iterator();
        while (cursor.hasNext()) {
            Meme meme = new Meme(cursor.next());
            memesList.add(meme);
        }
        cursor.close();
        return memesList;
    }
}
