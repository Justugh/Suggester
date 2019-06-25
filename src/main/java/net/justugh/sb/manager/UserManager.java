package net.justugh.sb.manager;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.guild.data.UserData;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class UserManager extends ListenerAdapter {

    private HashMap<Long, UserData> userCache = new HashMap<>();

    /**
     * Get User data.
     *
     * @param guildID The Guild to grab the data from.
     * @param userID The User's ID
     * @return The User's suggestion data.
     */
    public UserData getUserData(long guildID, long userID) {
        File playerFile = new File("guilds" + File.separator + guildID + File.separator + "users" + File.separator + userID + ".json");

        if(userCache.containsKey(userID)) {
            return userCache.get(userID);
        }

        try {
            if(!playerFile.exists()) {
                UserData userData = new UserData(userID, guildID);

                FileUtils.write(userData.getUserFile(), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(userData), StandardCharsets.UTF_8);

                return userData;
            } else {
                UserData data = new GsonBuilder().setPrettyPrinting().create().fromJson(new FileReader(playerFile), UserData.class);
                userCache.put(userID, data);

                return data;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Post the User's data to hastebin.
     *
     * @param data The User's data.
     * @return The hastebin URL.
     */
    public String postDataToHastebin(UserData data) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("https://hastebin.com/documents");

        try {
            post.setEntity(new StringEntity(new GsonBuilder().setPrettyPrinting().create().toJson(data)));

            HttpResponse response = client.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            return "https://hastebin.com/" + new JsonParser().parse(result).getAsJsonObject().get("key").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error posting to hastebin";
    }

}
