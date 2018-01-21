package mem.sirius.example.java;

import mem.sirius.example.java.database.Documentable;
import mem.sirius.example.java.database.Meme;
import mem.sirius.example.java.database.User;
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
    public HashMap<String, Object> getResponse(@RequestParam(value = "auth_token") String authToken,
                                               @RequestParam(value = "count") Integer count,
                                               @RequestParam(value = "last") String last) {
        if (Objects.equals(last, "null"))
            last = null;

        HashMap<String, Object> a = new HashMap<String, Object>();
        ArrayList<Meme> memes;

        User user = memeAppDatabase.getUserByAuthToken(authToken);
        if (user == null) {
            a.put("status", "fail");
            a.put("message", "auth_token_is_invalid");

            return a;
        }

        //sort_by:"rating", "time"
        memes = memeAppDatabase.topMemesList(count, Documentable.toObjectId(last));

        HashMap<String, String> usernamesMap = new HashMap<>();
        //assigning our rating to posts, so we could display that properly
        HashMap<String, Integer> isRatedByUserMap = new HashMap<>();

        for (Meme meme :
                memes) {
            usernamesMap.put(meme.getAuthorId().toHexString(), null);
            isRatedByUserMap.put(meme.getId().toHexString(), user.getIsPostLiked(meme.getId()));
        }

        memeAppDatabase.assignUsernamesToIds(usernamesMap);


        a.put("status", "success");
        a.put("links", memes);
        a.put("usernames", usernamesMap);
        a.put("likes", isRatedByUserMap);

        return a;
    }
}
