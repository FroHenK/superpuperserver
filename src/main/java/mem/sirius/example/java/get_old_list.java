package mem.sirius.example.java;

import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.TreeMap;

import static mem.sirius.example.java.App.memeAppDatabase;

public class get_old_list {
    private TreeMap<String, String> links = new TreeMap<String, String>();

    public get_old_list(Request a) {
        links = a.links;
    }

    public Response getResponse() {
        TreeMap<String, Object> a = new TreeMap<String, Object>();
        ArrayList<Meme> memes = new ArrayList<>();

        Integer last = Integer.parseInt(links.get("last"));
        Integer count = Integer.parseInt(links.get("count"));
        String authToken = links.get("auth_token");
        User user = memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            return new Response(a);
        }

        ArrayList<String> listMemId = user.getListOfViewed();


        for (Integer i = listMemId.size() - 1 - last; i >= 0 && i + count >= listMemId.size(); i--) {
            memes.add(memeAppDatabase.getMemeById(new ObjectId(listMemId.get(i))));
        }

        a.put("status", "success");
        a.put("links", memes);
        return (new Response(a));
    }
}
