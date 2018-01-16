package mem.sirius.example.java;

import mem.sirius.example.java.database.Documentable;
import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

import static mem.sirius.example.java.App.memeAppDatabase;

@RestController
public class get_old_list {

    @RequestMapping(value = "/get_old_list")
    public HashMap getResponse(@RequestParam(value = "auth_token") String authToken,
                               @RequestParam(value = "count") Integer count,
                               @RequestParam(value = "last") String last) {
        if (last.equals("null"))
            last = null;

        HashMap<String, Object> a = new HashMap<String, Object>();
        ArrayList<Meme> memes = new ArrayList<>();

        User user = memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "auth_token_is_invalid");

            return a;
        }

        memes = memeAppDatabase.oldMemesList(user, count, Documentable.toObjectId(last));

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
