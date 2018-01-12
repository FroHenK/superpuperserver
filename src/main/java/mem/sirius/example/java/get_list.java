package mem.sirius.example.java;

import mem.sirius.example.java.database.Meme;

import java.util.ArrayList;
import java.util.TreeMap;

import static mem.sirius.example.java.App.memeAppDatabase;

public class get_list {
    private TreeMap<String, String> links = new TreeMap<String, String>();
    public get_list(Request a){
        links = a.links;
    }
    public Response getResponse(){
        TreeMap<String, Object> a = new TreeMap<String, Object>();
        ArrayList<Meme> memes = memeAppDatabase.memesList(Integer.parseInt(links.get("last")), Integer.parseInt(links.get("count")));
        /*ArrayList<String> urls = new ArrayList<String>();
        for (Meme meme :
                memes) {
            urls.add(meme.getUrl());
        }*/


        a.put("links", memes);
        return (new Response(a));
    }
}
