package mem.sirius.example.java;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import static mem.sirius.example.java.App.mem_dir;
import static mem.sirius.example.java.App.myip;

public class get_list {
    private TreeMap<String, String> links = new TreeMap<String, String>();
    public get_list(Request a){
        links = a.links;
    }
    public Response getResponse(){
        parse_fol dir = new parse_fol();
        File memes = new File(mem_dir);
        dir.processFilesFromFolder(memes);
        TreeMap<String, ArrayList<String>> a = new TreeMap<String, ArrayList<String>>();
        a.put("links", dir.getLinksAccepted(Integer.parseInt(links.get("last")), Integer.parseInt(links.get("count")), myip));
        return (new Response(a));
    }
}
