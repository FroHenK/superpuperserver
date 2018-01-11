package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import org.apache.commons.net.ftp.FTPClient;
import org.bson.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.TreeMap;

public class meme_upload {

    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String URL_PREFIX = "http://azatismagilov00.siteme.org/kek/";
    private static final int FILE_PART_LENGTH = 1024 * 20;
    private TreeMap<String, String> links = new TreeMap<String, String>();
    private InputStream inputStream;

    public meme_upload(Request a, InputStream inputStream) {
        links = a.links;
        this.inputStream = inputStream;
    }

    public Response getResponse() {
        TreeMap<String, ArrayList<String>> a = new TreeMap<String, ArrayList<String>>();
        //auth_token, content_length
        String authToken = links.get("auth_token");
        Integer contentLength = Integer.valueOf(links.get("content_length"));
        MongoCollection<Document> memesCollection = App.memeAppDatabase.getMemesCollection();

        Document user = App.memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", new OneElementArrayList<String>("fail"));
            a.put("message", new OneElementArrayList<String>("invalid_auth_token"));
            return (new Response(a));
        }

        if (contentLength > MAX_FILE_SIZE) {
            a.put("status", new OneElementArrayList<String>("fail"));
            a.put("message", new OneElementArrayList<String>("file_too_big"));
            return (new Response(a));
        }

        FTPClient ftpClient = new FTPClient();
        String filename = new RandomString().nextString();

        try {
            ftpClient.connect(App.ftp_host, App.ftp_port);
            ftpClient.login(App.ftp_user, App.ftp_password);
            OutputStream storeFileStream = ftpClient.storeFileStream(filename);
            byte image[] = new byte[contentLength];
            int current = 0;
            while (current < contentLength) {
                int read = inputStream.read(image, current, FILE_PART_LENGTH);
                storeFileStream.write(image, current, read);
                current += read;
            }

            ftpClient.completePendingCommand();

            System.out.println("Filetype: " + URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(image)));


        } catch (IOException e) {
            e.printStackTrace();

            a.put("status", new OneElementArrayList<String>("fail"));
            a.put("message", new OneElementArrayList<String>("io_exception"));
            return (new Response(a));

        }

        a.put("status", new OneElementArrayList<String>("success"));
        a.put("link", new OneElementArrayList<String>(URL_PREFIX + filename));

        //status(success,fail), link
        return (new Response(a));
    }
}
