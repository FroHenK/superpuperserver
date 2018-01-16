package mem.sirius.example.java;

import mem.sirius.example.java.database.Documentable;
import mem.sirius.example.java.database.Meme;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static mem.sirius.example.java.App.memeAppDatabase;

@RestController
public class get_top_list {
    @RequestMapping(value = "/get_top_list")
    public HashMap<String, Object> getResponse(@RequestParam(value = "count") Integer count,
                                               @RequestParam(value = "last") String last) {
        if (Objects.equals(last, "null"))
            last = null;

        HashMap<String, Object> a = new HashMap<String, Object>();
        ArrayList<Meme> memes;


        //sort_by:"rating", "time"
        memes = memeAppDatabase.topMemesList(count, Documentable.toObjectId(last));

        a.put("status", "success");
        a.put("links", memes);
        return a;
    }
}
