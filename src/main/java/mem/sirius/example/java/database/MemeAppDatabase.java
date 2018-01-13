package mem.sirius.example.java.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Map;

public class MemeAppDatabase {
    private final String server;
    private final String user;
    private final String password;
    private final String salt;
    private final MongoCollection<Document> memesCollection;
    private final MongoCollection<Document> usersCollection;
    private final MongoCollection<Document> sessionsCollection;
    private final MongoCollection<Document> visitsCollection;
    private final MongoCollection<Document> commentsCollection;

    public MongoCollection<Document> getCommentsCollection() {
        return commentsCollection;
    }

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

    public User getUserByAuthToken(String authToken) {
        if (sessionsCollection.count(new Document("auth_token", authToken)) != 0) {
            MongoCursor<Document> cursor = sessionsCollection.find(new Document("auth_token", authToken)).iterator();
            Document session = cursor.next();
            cursor.close();
            cursor = usersCollection.find(new Document("_id", session.getObjectId("user_id"))).iterator();
            Document user = cursor.next();
            cursor.close();
            return new User(user);
        }
        return null;
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
        commentsCollection = mongoDatabase.getCollection("comments");
    }

    public ArrayList<Meme> memesList(Integer last, Integer num) {
        ArrayList<Meme> memesList = new ArrayList<Meme>();
        MongoCursor<Document> cursor = memesCollection.find().sort(new Document("time", -1)).skip(last).limit(num).iterator();
        while (cursor.hasNext()) {
            Meme meme = new Meme(cursor.next());
            memesList.add(meme);
        }
        cursor.close();
        return memesList;
    }

    public ArrayList<Comment> commentsList(ObjectId memeId) {
        ArrayList<Comment> commentsList = new ArrayList<Comment>();
        for (Document document : commentsCollection.find(new Comment().setMemeId(memeId).toDocument()).sort(new Document("time", -1))) {
            commentsList.add(new Comment(document));
        }
        return commentsList;
    }

    public Map<ObjectId, String> assignUsernamesToIds(Map<ObjectId, String> map) {
        Bson fieldsProjection = Projections.fields(Projections.include("username"), Projections.excludeId());
        for (ObjectId objectId :
                map.keySet()) {
            Document userQuery = new User().setId(objectId).toDocument();
            Document userDocument = usersCollection.find(userQuery).projection(fieldsProjection).first();
            map.put(objectId, new User(userDocument).getUsername());
        }
        return map;
    }
}
