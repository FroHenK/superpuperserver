package mem.sirius.example.java;

import mem.sirius.example.java.database.Documentable;
import mem.sirius.example.java.database.Meme;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

import static mem.sirius.example.java.App.memeAppDatabase;

@RestController
public class get_user_list {
    @RequestMapping(value = "/get_user_memes_list")
    public HashMap getResponse(@RequestParam(value = "user_id") String user_id,
                               @RequestParam(value = "count") Integer count,
                               @RequestParam(value = "last") String last) {
        if (last.equals("null"))
            last = null;


        HashMap<String, Object> a = new HashMap<>();
        ArrayList<Meme> memes;
        ObjectId userId = new ObjectId(user_id);

        memes = memeAppDatabase.userMemesList(userId, count, Documentable.toObjectId(last));

        HashMap<String, String> usernamesMap = new HashMap<>();
        for (Meme comment :
                memes) {
            usernamesMap.put(comment.getAuthorId().toHexString(), null);
        }

        memeAppDatabase.assignUsernamesToIds(usernamesMap);

        a.put("status", "success");
        a.put("links", memes);
        a.put("usernames", usernamesMap);
        return a;
    }
}
