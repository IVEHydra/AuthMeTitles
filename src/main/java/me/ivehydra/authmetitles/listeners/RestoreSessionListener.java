package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.events.RestoreSessionEvent;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.TitleHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RestoreSessionListener implements Listener {

    private final AuthMeTitles instance = AuthMeTitles.getInstance();

    @EventHandler
    public void onRestoreSessionEvent(RestoreSessionEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            TitleHandler.handle(p, "titles.sessionRestored", true);
            ActionBarHandler.handle(p, "sessionRestored");
        }, 3L);

    }

}
