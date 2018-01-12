package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import mem.sirius.example.java.database.Meme;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.bson.Document;
import org.bson.types.BSONTimestamp;

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

        System.out.printf("Accepting image with content length: %d from %s%n", contentLength, user.getString("username"));

        FTPClient ftpClient = new FTPClient();
        String filename = new RandomString().nextString();
        String extension = null;
        try {

            System.out.println(App.ftp_port);
            System.out.println(App.ftp_user);
            System.out.println(App.ftp_password);
            ftpClient.connect(App.ftp_host, App.ftp_port);
            boolean login = ftpClient.login(App.ftp_user, App.ftp_password);
            System.out.println(login ? "Successful login!" : "Login failed!");

            byte image[] = new byte[contentLength];
            int current = 0;
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            OutputStream storeFileStream = ftpClient.storeFileStream(filename);
            System.out.println(storeFileStream);
            while (current < contentLength) {
                // I have no idea why I implemented this this particular way
                // ¯\_(ツ)_/¯

                System.out.println(image + ", " + current + ", " + contentLength);
                int read = inputStream.read(image, current, Math.min(FILE_PART_LENGTH, contentLength - current));
                System.out.println("read " + read);
                storeFileStream.write(image, current, read);
                storeFileStream.flush();
                current += read;
            }
            storeFileStream.close();
            ftpClient.completePendingCommand();

            String mime = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(image));
            System.out.println("Filetype: " + mime);
            MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
            if (mime.contains("image")) {
                extension = mimeTypes.forName(mime).getExtension();
                ftpClient.rename(filename, filename + extension);
            }
        } catch (IOException e) {
            e.printStackTrace();
            a.put("status", new OneElementArrayList<String>("fail"));
            a.put("message", new OneElementArrayList<String>("io_exception"));
            return (new Response(a));
        } catch (MimeTypeException e) {
            e.printStackTrace();
        }

        if (extension == null) {
            a.put("status", new OneElementArrayList<String>("fail"));
            a.put("message", new OneElementArrayList<String>("unknown_extension"));
            return (new Response(a));
        }


        a.put("status", new OneElementArrayList<String>("success"));
        String memeUrl = URL_PREFIX + filename + extension;
        a.put("link", new OneElementArrayList<String>(memeUrl));

        memesCollection.insertOne(new Meme(memeUrl, new BSONTimestamp()).toDocument());

        //status(success,fail), link
        return (new Response(a));
    }
}
