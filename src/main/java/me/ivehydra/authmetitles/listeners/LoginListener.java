package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.events.LoginEvent;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.AbstractHandler;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.TitleHandler;
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

        if(abstractHandler != null)
            abstractHandler.stop();

        if(instanceAuthMe.isAuthenticated(p)) {
            TitleHandler.handle(p, "titles.login", true);
            ActionBarHandler.handle(p, "login");
        }

    }

}
