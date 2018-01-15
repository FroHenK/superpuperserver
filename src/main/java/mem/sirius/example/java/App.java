package mem.sirius.example.java;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import mem.sirius.example.java.database.MemeAppDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        System.out.println("Version: 3 RestApi");

//        Document parse = Document.parse("{\"comments\":[{\"id\":{\"timestamp\":1516006157,\"machineIdentifier\":3246456,\"processIdentifier\":15670,\"counter\":15831739,\"time\":1516006157000,\"date\":1516006157000,\"timeSecond\":1516006157},\"memeId\":{\"timestamp\":1515750978,\"machineIdentifier\":15575715,\"processIdentifier\":4,\"counter\":7238845,\"time\":1515750978000,\"date\":1515750978000,\"timeSecond\":1515750978},\"parentCommentId\":null,\"authorId\":{\"timestamp\":1515962055,\"machineIdentifier\":4093892,\"processIdentifier\":4,\"counter\":13808805,\"time\":1515962055000,\"date\":1515962055000,\"timeSecond\":1515962055},\"text\":\"kek\",\"time\":{\"value\":6511196864849641474,\"time\":1516006157,\"bsonType\":\"TIMESTAMP\",\"inc\":2,\"document\":false,\"string\":false,\"number\":false,\"int32\":false,\"int64\":false,\"decimal128\":false,\"double\":false,\"boolean\":false,\"objectId\":false,\"dbpointer\":false,\"timestamp\":true,\"binary\":false,\"dateTime\":false,\"symbol\":false,\"regularExpression\":false,\"javaScript\":false,\"javaScriptWithScope\":false,\"array\":false,\"null\":false}}],\"usernames\":{\"5a5bbec73e77c40004d2b4a5\":\"ggbet\"}}");
//        Document comment = (Document)parse.get("comments", ArrayList.class).get(0);
//        Comment comment1 = new Comment(comment);

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

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);


        SpringApplication.run(App.class, args);

        System.out.println("Port is :" + port);
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder() {

            @Override
            public void configure(ObjectMapper objectMapper) {
                super.configure(objectMapper);
                objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            }

        };

    }

    @RequestMapping(value = "/")
    public String greeting() {
        return "Server is working";
    }
}
