package mem.sirius.example.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.TreeMap;

public class Response {
    private TreeMap<String, ArrayList<String>> links = new TreeMap<String, ArrayList<String>>();

    public Response(TreeMap<String, ArrayList<String>> a) {
        links = a;
    }
    public String toString(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(links);
    }
}
