package mem.sirius.example.java;

import java.util.ArrayList;
import java.util.TreeMap;

public class user_process {
    private TreeMap<String, String> links = new TreeMap<String, String>();

    public user_process(Request a) {
        links = a.links;
    }

    public Response create() {
        TreeMap<String, ArrayList<String>> a = new TreeMap<String, ArrayList<String>>();
        /*
        Обработка какой-то херни с api
         */
        return new Response(a);
    }
}
