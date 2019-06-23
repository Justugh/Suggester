package net.justugh.sb.manager;

import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.justugh.sb.data.UserData;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UserManager extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if(getUserData(event.getUser().getIdLong()) == null) {
            UserData userData = new UserData(event.getUser().getIdLong());

            try {
                FileUtils.write(userData.getUserFile(), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(userData.getUserFile()), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public UserData getUserData(long userID) {
        File playerFile = new File("users" + File.separator + userID + ".json");

        try {
            return playerFile.exists() ? new GsonBuilder().create().fromJson(new FileReader(playerFile), UserData.class) : null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
