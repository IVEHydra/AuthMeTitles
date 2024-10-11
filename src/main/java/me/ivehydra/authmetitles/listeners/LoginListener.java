package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.BossBarHandler;
import me.ivehydra.authmetitles.handler.handlers.TitleHandler;
import me.ivehydra.authmetitles.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LoginListener implements Listener {

    private final AuthMeTitles instance = AuthMeTitles.getInstance();
    private final AuthMeApi instanceAuthMe = AuthMeApi.getInstance();

    @EventHandler
    public void onLoginEvent(LoginEvent e) {
        Player p = e.getPlayer();

        if(VersionUtils.isAtLeastVersion19()) {
            BossBarHandler bossBarHandler = instance.getActiveBossBar().get(p);
            if(bossBarHandler != null) bossBarHandler.stop();
        }

        if(instanceAuthMe.isAuthenticated(p)) {
            TitleHandler.handle(p, "titles.login", true);
            ActionBarHandler.handle(p, "login");
        }

    }

}
