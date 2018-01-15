package mem.sirius.example.java;

import com.mongodb.client.MongoCollection;
import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.User;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.bson.BsonTimestamp;
import org.bson.Document;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.HashMap;


@RestController
public class meme_upload {

    private static final int MAX_FILE_SIZE = 2 * 1024 * 1024;//don't forget that we define it in application.properties too
    private static final String URL_PREFIX = "http://azatismagilov00.siteme.org/kek/";
    private static final int FILE_PART_LENGTH = 1024 * 20;


    @RequestMapping(value = "/upload_meme", method = RequestMethod.POST)
    public HashMap<String, Object> getResponse(@RequestParam(value = "auth_token") String authToken,
                                               @RequestParam(value = "file") MultipartFile file) {
        HashMap<String, Object> a = new HashMap<>();

        if (file.getSize() > MAX_FILE_SIZE) {
            a.put("status", "fail");
            a.put("message", "file_too_big");
            return a;
        }

        Integer contentLength = Math.toIntExact(file.getSize());
        MongoCollection<Document> memesCollection = App.memeAppDatabase.getMemesCollection();

        User user = App.memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "invalid_auth_token");
            return a;
        }


        System.out.printf("Accepting image with content length: %d from %s%n", contentLength, user.getUsername());

        FTPClient ftpClient = new FTPClient();
        ///Name for file
        String filename = new RandomString().nextString();
        String extension = null;
        try {
            InputStream inputStream = file.getInputStream();
            ftpClient.connect(App.ftp_host, App.ftp_port);
            ftpClient.enterLocalPassiveMode();
            boolean login = ftpClient.login(App.ftp_user, App.ftp_password);
            System.out.println(login ? "Successful login!" : "Login failed!");

            byte image[] = new byte[contentLength];
            int current = 0;
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            System.out.println("Uploading file to: " + filename);
            OutputStream storeFileStream = ftpClient.storeFileStream(filename);

            System.out.println("FTP Reply: " + ftpClient.getReplyString().trim() + ":" + ftpClient.getReplyCode());
            while (current < contentLength) {
                // I have no idea why I implemented this this particular way
                // ¯\_(ツ)_/¯

                int read = inputStream.read(image, current, Math.min(FILE_PART_LENGTH, contentLength - current));
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
            a.put("status", "fail");
            a.put("message", "io_exception");
            return a;
        } catch (MimeTypeException e) {
            e.printStackTrace();
        }

        if (extension == null) {
            a.put("status", "fail");
            a.put("message", "unknown_extension");
            return a;
        }


        a.put("status", "success");
        String memeUrl = URL_PREFIX + filename + extension;
        a.put("link", memeUrl);

        memesCollection.insertOne(new Meme(memeUrl, new BsonTimestamp()).setRating(0).setAuthorId(user.getId()).toDocument());

        //status(success,fail), link
        return a;
    }
}
