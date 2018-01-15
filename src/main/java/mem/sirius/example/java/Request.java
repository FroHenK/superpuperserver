package mem.sirius.example.java;

import com.google.gson.Gson;

import java.util.TreeMap;

@Deprecated
public class Request {
    public TreeMap<String, String> links = new TreeMap<String, String>();

    public Request(String s) {
        links = new Gson().fromJson(s, links.getClass());
    }
}
