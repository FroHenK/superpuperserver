package mem.sirius.example.java.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

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

    public HashMap<ObjectId, Meme> getMemesCache() {
        return memesCache;
    }

    private final HashMap<ObjectId, Meme> memesCache;
    private final Thread memesCacheDaemon;

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
        return getUserByAuthToken(authToken, true);
    }

    public User getUserByAuthToken(String authToken, Boolean nullIfVoidUsername) {
        if (sessionsCollection.count(new Document("auth_token", authToken)) != 0) {
            MongoCursor<Document> cursor = sessionsCollection.find(new Document("auth_token", authToken)).iterator();
            Document session = cursor.next();
            cursor.close();
            cursor = usersCollection.find(new Document("_id", session.getObjectId("user_id"))).iterator();
            Document user = cursor.next();
            cursor.close();
            User userObject = new User(user);

            if (nullIfVoidUsername && userObject.getUsername() == null)
                return null;

            return userObject;
        }
        return null;
    }

    public Meme getMemeById(ObjectId id) {
        if (memesCache.containsKey(id)) //todo don't forget about cache if you will add ability to edit memes
            return memesCache.get(id);
        MongoCursor<Document> cursor = memesCollection.find(new Document("_id", id)).iterator();
        if (!cursor.hasNext())
            return null;
        Document meme = cursor.next();
        cursor.close();
        Meme memeObject = new Meme(meme);

        memesCache.put(id, memeObject);
        return memeObject;
    }

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MemeAppDatabase(String server, String user, String password, String salt, String database) {
        this.server = server;
        this.user = user;
        this.password = password;
        this.salt = salt;


        System.out.println("Establishing connection to the database");
        mongoClient = new MongoClient(new MongoClientURI(String.format("mongodb+srv://%s:%s@%s/test", user, password, server)));
        mongoDatabase = mongoClient.getDatabase(database);
        memesCollection = mongoDatabase.getCollection("memes");
        usersCollection = mongoDatabase.getCollection("users");
        sessionsCollection = mongoDatabase.getCollection("sessions");
        visitsCollection = mongoDatabase.getCollection("visits");
        commentsCollection = mongoDatabase.getCollection("comments");


        System.out.println("Initializing memes cache");
        memesCache = new HashMap<>();
        for (Document document : memesCollection.find()) {
            Meme meme = new Meme(document);
            memesCache.put(meme.getId(), meme);
        }

        System.out.println("Starting memes cache updater daemon");
        memesCacheDaemon = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(200);
                    for (Document document : memesCollection.find()) {
                        Meme meme = new Meme(document);
                        memesCache.put(meme.getId(), meme);
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        memesCacheDaemon.setDaemon(true);
        memesCacheDaemon.setName("Memes cache daemon");
        memesCacheDaemon.start();
    }

    public ArrayList<Meme> memesList(User user, Integer num, String sortBy, ObjectId objectId, Boolean amoral) {
        ArrayList<Meme> memesList = new ArrayList<>();
        ArrayList<Bson> filters = new ArrayList<>();

        if (objectId != null)
            filters.add(Filters.lt("_id", objectId));
        if (!amoral)
            filters.add(Filters.eq("is_amoral", false));


        Bson bson[] = new Bson[filters.size()];
        filters.toArray(bson);


        FindIterable<Document> sort = memesCollection.find().sort(new Document(sortBy, -1));


        if (filters.size() != 0)
            sort = sort.filter(Filters.and(bson));


        MongoCursor<Document> cursor = sort.iterator();
        Set<String> viewed = user.getIsViewed();
        while (cursor.hasNext() && memesList.size() < num) {
            Meme meme = new Meme(cursor.next());
            if (!viewed.contains(meme.getId().toHexString())) {
                memesList.add(meme);
            }
        }
        cursor.close();
        return memesList;
    }

    public ArrayList<Meme> topMemesList(Integer num, ObjectId objectId, User user, Boolean amoral) {
        ArrayList<Meme> memesList = new ArrayList<>();
        FindIterable<Document> sort = memesCollection.find().sort(new Document("rating", -1));
        MongoCursor<Document> cursor = sort.iterator();
        while (objectId != null && cursor.hasNext()) {
            Meme meme = new Meme(cursor.next());
            if (meme.getId().equals(objectId))
                break;
        }
        while (cursor.hasNext() && num > 0) {
            Meme meme = new Meme(cursor.next());
            if (user.getIsViewed().contains(meme.getId().toHexString()) || !(amoral || !meme.isAmoral))
                continue;
            memesList.add(meme);
            num--;
        }
        cursor.close();
        return memesList;
    }


    public ArrayList<Meme> oldMemesList(User user, Integer num, ObjectId objectId, Boolean amoral) {
        ArrayList<Meme> memesList = new ArrayList<>();
        ArrayList<String> listOfViewed = user.getListOfViewed();
        Collections.reverse(listOfViewed);
        Iterator<String> iterator = listOfViewed.iterator();
        if (objectId != null)
            while (iterator.hasNext()) {
                Meme meme = getMemeById(new ObjectId(iterator.next()));
                if (meme.getId().equals(objectId))
                    break;
            }
        while (num > 0 && iterator.hasNext()) {
            num--;
            Meme meme = getMemeById(new ObjectId(iterator.next()));
            if (meme.isAmoral && !amoral)
                continue;
            memesList.add(meme);
        }
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

    public Map<String, String> assignUsernamesToIds(HashMap<String, String> map) {
        Bson fieldsProjection = Projections.fields(Projections.include("username"), Projections.excludeId());
        for (String objectId :
                map.keySet()) {
            Document userQuery = new User().setId(new ObjectId(objectId)).toDocument();
            Document userDocument = usersCollection.find(userQuery).projection(fieldsProjection).first();
            map.put(objectId, new User(userDocument).getUsername());
        }
        return map;
    }


    public Map<String, String> assignAvatarUrlToIds(HashMap<String, String> map) {
        Bson fieldsProjection = Projections.fields(Projections.include("avatar_url"), Projections.excludeId());
        for (String objectId :
                map.keySet()) {
            Document userQuery = new User().setId(new ObjectId(objectId)).toDocument();
            Document userDocument = usersCollection.find(userQuery).projection(fieldsProjection).first();
            map.put(objectId, new User(userDocument).avatarUrl);
        }
        return map;
    }

    public Map<String, Boolean> assignIsSubscribedToIds(HashMap<String, Boolean> map, User user) {
        for (String userId :
                map.keySet()) {
            map.put(userId, user.subscriptions.contains(userId));
        }
        return map;
    }


    public ArrayList<Meme> userMemesList(ObjectId userId, Integer count, ObjectId objectId, Boolean amoral) {
        ArrayList<Meme> memesList = new ArrayList<>();
        ArrayList<Bson> filters = new ArrayList<>();

        filters.add(Filters.eq("author_id", userId));
        if (objectId != null)
            filters.add(Filters.lt("_id", objectId));
        if (!amoral)
            filters.add(Filters.eq("is_amoral", false));


        Bson bson[] = new Bson[filters.size()];
        filters.toArray(bson);

        FindIterable<Document> sort = memesCollection.find().filter(Filters.and(bson)).sort(new Document("_id", -1)).limit(count);


        for (Document doc :
                sort) {
            Meme meme = new Meme(doc);
            memesList.add(meme);
        }
        return memesList;
    }
}
