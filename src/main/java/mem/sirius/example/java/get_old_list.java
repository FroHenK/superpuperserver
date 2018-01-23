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
                               @RequestParam(value = "last") String last,
                               @RequestParam(value = "amoral", defaultValue = "false") Boolean amoral) {
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

        memes = memeAppDatabase.oldMemesList(user, count, Documentable.toObjectId(last), amoral);

        HashMap<String, String> usernamesMap = new HashMap<>();
        //assigning our rating to posts, so we could display that properly
        HashMap<String, Integer> isRatedByUserMap = new HashMap<>();

        HashMap<String, String> avatarUrlsMap = new HashMap<>();
        for (Meme meme :
                memes) {
            usernamesMap.put(meme.getAuthorId().toHexString(), null);
            isRatedByUserMap.put(meme.getId().toHexString(), user.getIsPostLiked(meme.getId()));
            avatarUrlsMap.put(meme.getAuthorId().toHexString(), null);
        }

        memeAppDatabase.assignAvatarUrlToIds(avatarUrlsMap);
        memeAppDatabase.assignUsernamesToIds(usernamesMap);


        a.put("status", "success");
        a.put("links", memes);
        a.put("usernames", usernamesMap);
        a.put("likes", isRatedByUserMap);
        a.put("avatar_urls", avatarUrlsMap);
        return a;
    }
}
