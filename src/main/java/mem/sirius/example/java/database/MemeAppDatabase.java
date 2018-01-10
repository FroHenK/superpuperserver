package mem.sirius.example.java.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MemeAppDatabase {
    private final String mServer;
    private final String mUser;
    private final String mPassword;
    private final String mSalt;
    private MongoClient mMongoClient;

    public MemeAppDatabase(String server, String user, String password, String salt) {
        mServer = server;
        mUser = user;
        mPassword = password;
        mSalt = salt;

        mMongoClient = new MongoClient(new MongoClientURI(String.format("mongodb+srv://%s:%s@%s/test", user, password, server)));
    }
}
