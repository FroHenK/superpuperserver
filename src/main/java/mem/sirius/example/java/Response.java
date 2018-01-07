package mem.sirius.example.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.TreeMap;

public class Response {
    private TreeMap<String, String> links;
    public Response(TreeMap<String, String> a){
        links = a;
    }
    public String toString(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(links);
    }
}
