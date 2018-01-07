package mem.sirius.example.java;

import com.google.gson.Gson;

import java.util.TreeMap;

public class Request {
    public TreeMap<String, String> links;
    public Request(String s){
        links = new Gson().fromJson(s, links.getClass());
    }
}
