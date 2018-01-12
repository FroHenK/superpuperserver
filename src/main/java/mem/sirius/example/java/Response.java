package mem.sirius.example.java;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Response {
    private Object links;

    public Response(Object a) {
        links = a;
    }
    public String toString(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(links);
    }
}
