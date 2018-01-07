package mem.sirius.example.java;

import java.io.File;
import java.util.TreeMap;

import static mem.sirius.example.java.App.mem_dir;
import static mem.sirius.example.java.App.myip;
import static mem.sirius.example.java.App.serverport;

public class get_list {
    private TreeMap<String, String> links;
    public get_list(Request a){
        links = a.links;
    }
    public Response getResponse(){
        parse_fol dir = new parse_fol();
        File memes = new File(mem_dir);
        dir.processFilesFromFolder(memes);
        TreeMap<String, String> a = new TreeMap<String, String>();
        a.put("links", dir.getLinksAccepted(links.get("first"), Integer.parseInt(links.get("count")), "http://" + myip + ":" + serverport + "/"));
        return (new Response(a));
    }
}
