package mem.sirius.example.java;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import mem.sirius.example.java.database.MemeAppDatabase;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        mongo_server = (System.getenv(MONGO_SERVER) != null ? System.getenv(MONGO_SERVER) : "localhost");
        mongo_user = (System.getenv(MONGO_USER) != null ? System.getenv(MONGO_USER) : "user");
        mongo_password = (System.getenv(MONGO_PASSWORD) != null ? System.getenv(MONGO_PASSWORD) : "password");
        mongo_salt = (System.getenv(MONGO_SALT) != null ? System.getenv(MONGO_SALT) : "salt_salt_salt");

        App.memeAppDatabase = new MemeAppDatabase(mongo_server, mongo_user, mongo_password, mongo_salt, DATABASE);
        memeAppDatabase = App.memeAppDatabase;
//        {
//            Request a = new Request("{}");
//
//            a.links.put("auth_token", "eY7XifPvqpTosI6HMANeT");
//            a.links.put("meme_id", "5a588642edaaa300046e74bd");
//            a.links.put("text", "I am very interested");
//            a.links.put("parent_id", "5a59c09cb9bb8d363f8c775d");
//
//            comment_process comment_process = new comment_process(a);
//            //System.out.println(comment_process.postComment());
//            System.out.println(comment_process.getComments());
//        }

        {
            Request a = new Request("{}");

            a.links.put("auth_token", "8j4Y0rr7Mvb8pSo4voLXk");
            a.links.put("last", "0");
            a.links.put("count", "10");
            a.links.put("sort_by", "time");
            a.links.put("meme_id", "5a58ae090156ce754e858bc9");

            get_new_list comment_process = new get_new_list(a);
            //System.out.println(comment_process.postComment());
            System.out.println(comment_process.getResponse());
        }



    }

    private static String mongo_server;
    private static String mongo_password;
    private static String mongo_salt;
    private static String mongo_user;
    public static MemeAppDatabase memeAppDatabase;
    private static final String MONGO_SERVER = "MONGO_SERVER";
    private static final String MONGO_USER = "MONGO_USER";
    private static final String MONGO_PASSWORD = "MONGO_PASSWORD";
    private static final String MONGO_SALT = "MONGO_SALT";
    public static final String DATABASE = "memes";


}
