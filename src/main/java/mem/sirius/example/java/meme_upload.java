package mem.sirius.example.java;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeMap;

public class meme_upload {

    private TreeMap<String, String> links = new TreeMap<String, String>();
    private InputStream inputStream;

    public meme_upload(Request a, InputStream inputStream) {
        links = a.links;
        this.inputStream = inputStream;
    }

    public Response getResponse() {
        TreeMap<String, ArrayList<String>> a = new TreeMap<String, ArrayList<String>>();

        return (new Response(a));
    }
}
