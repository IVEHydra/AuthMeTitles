package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.AbstractHandler;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar18Handler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar19Handler;
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
        AbstractHandler abstractHandler = instance.getActiveBossBar().get(p);

        if(abstractHandler != null) {
            if(VersionUtils.isAtLeastVersion19() && abstractHandler instanceof BossBar19Handler) abstractHandler.stop();
            else if(!VersionUtils.isAtLeastVersion19() && abstractHandler instanceof BossBar18Handler) abstractHandler.stop();
        }

        if(instanceAuthMe.isAuthenticated(p)) {
            TitleHandler.handle(p, "titles.login", true);
            ActionBarHandler.handle(p, "login");
        }

    }

}
