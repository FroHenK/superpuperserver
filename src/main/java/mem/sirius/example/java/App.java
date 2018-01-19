package mem.sirius.example.java;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import mem.sirius.example.java.database.MemeAppDatabase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    private static final String GOOGLE_SERVICE_ACCOUNT = "GOOGLE_SERVICE_ACCOUNT";

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


    public static void main(String[] args) throws IOException {
        System.out.println("Version: 4 No jar");

        InputStream firebaseServiceJsonStream = null;

        if (System.getenv().containsKey(GOOGLE_SERVICE_ACCOUNT)) {
            firebaseServiceJsonStream = new ByteArrayInputStream(System.getenv(GOOGLE_SERVICE_ACCOUNT).getBytes(StandardCharsets.UTF_8.name()));
        } else {
            firebaseServiceJsonStream = new FileInputStream(System.getProperty("user.home") + "/memeService.json");
        }
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(firebaseServiceJsonStream))
                .build();

        FirebaseApp.initializeApp(options);


        port = Integer.parseInt(System.getenv(PORT) != null ? System.getenv(PORT) : "8080");

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

        System.out.println("Started on port: " + port);
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

    @Configuration
    public class ServletConfig {
        @Bean
        public EmbeddedServletContainerCustomizer containerCustomizer() {
            return (container -> {
                container.setPort(port);
            });
        }
    }

    @RequestMapping(value = "/")
    public String greeting() {
        return "Server is working";
    }
}
