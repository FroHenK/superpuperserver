package mem.sirius.example.java;


import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.MemeAppDatabase;
import org.bson.BsonTimestamp;
import org.bson.types.ObjectId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.ServerSocket;
import java.net.Socket;

@RestController
@SpringBootApplication
public class App {
    public static final String PORT = "PORT";

    private static final String MONGO_SERVER = "MONGO_SERVER";
    private static final String MONGO_USER = "MONGO_USER";
    private static final String MONGO_PASSWORD = "MONGO_PASSWORD";
    private static final String MONGO_SALT = "MONGO_SALT";
    public static final String DATABASE = "memes";

    private static final String FTP_HOST = "MEME_FTP_HOST";
    private static final String FTP_PORT = "MEME_FTP_PORT";
    private static final String FTP_USER = "MEME_FTP_USER";
    private static final String FTP_PASSWORD = "MEME_FTP_PASSWORD";


    public static int port = 0;
    public static final String myip = "http://azatismagilov00.siteme.org/ac/";
    public static final String mem_dir = "memes";
    private static String mongo_server;
    private static String mongo_password;
    private static String mongo_salt;
    private static String mongo_user;
    public static MemeAppDatabase memeAppDatabase;

    public static String ftp_host;
    public static Integer ftp_port;
    public static String ftp_user;
    public static String ftp_password;


    public static void main(String[] args) {
        System.out.println("Version: 2 WebSocket Try");
        port = Integer.parseInt(System.getenv(PORT) != null ? System.getenv(PORT) : "5000");

        mongo_server = (System.getenv(MONGO_SERVER) != null ? System.getenv(MONGO_SERVER) : "localhost");
        mongo_user = (System.getenv(MONGO_USER) != null ? System.getenv(MONGO_USER) : "user");
        mongo_password = (System.getenv(MONGO_PASSWORD) != null ? System.getenv(MONGO_PASSWORD) : "password");
        mongo_salt = (System.getenv(MONGO_SALT) != null ? System.getenv(MONGO_SALT) : "salt_salt_salt");

        memeAppDatabase = new MemeAppDatabase(mongo_server, mongo_user, mongo_password, mongo_salt, DATABASE);

        ftp_host = (System.getenv(FTP_HOST) != null ? System.getenv(FTP_HOST) : "localhost");
        ftp_port = Integer.valueOf((System.getenv(FTP_PORT) != null ? System.getenv(FTP_PORT) : "21"));
        ftp_user = (System.getenv(FTP_USER) != null ? System.getenv(FTP_USER) : "user");
        ftp_password = (System.getenv(FTP_PASSWORD) != null ? System.getenv(FTP_PASSWORD) : "password");


        SpringApplication.run(App.class, args);

        System.out.println("Port is :" + port);
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch (Exception x) {
            x.printStackTrace();
        }
        while (true)
            try {
                System.out.println("Waiting for a client...");
                Socket socket = ss.accept();
                new Thread(new socket_connection(socket)).start();
            } catch (Exception x) {
                x.printStackTrace();
            }

    }

    @RequestMapping(value = "/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

        return new Meme().setTitle(name).setAuthorId(new ObjectId("5a5ba201182b3e0004c2ed09")).setTime(new BsonTimestamp()).toDocument().toJson();
    }
}
