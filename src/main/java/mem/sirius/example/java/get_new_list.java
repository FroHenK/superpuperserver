package mem.sirius.example.java;

import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.User;

import java.util.ArrayList;
import java.util.TreeMap;

import static mem.sirius.example.java.App.memeAppDatabase;

public class get_new_list {//TODO rewrite
    private TreeMap<String, String> links = new TreeMap<String, String>();

    public get_new_list(Request a) {
        links = a.links;
    }

    public Response getResponse() {
        TreeMap<String, Object> a = new TreeMap<String, Object>();
        ArrayList<Meme> memes = new ArrayList<>();

        Integer count = Integer.parseInt(links.get("count"));
        String authToken = links.get("auth_token");
        User user = memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            return new Response(a);
        }

        //sort_by:"rating", "time"
        memes = memeAppDatabase.memesList(user, count, links.get("sort_by"));

        a.put("status", "success");
        a.put("links", memes);
        return (new Response(a));
    }
}
