package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.events.RegisterEvent;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.BossBarHandler;
import me.ivehydra.authmetitles.handler.handlers.TitleHandler;
import me.ivehydra.authmetitles.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegisterListener implements Listener {

    private final AuthMeTitles instance = AuthMeTitles.getInstance();

    @EventHandler
    public void onRegisterEvent(RegisterEvent e) {
        Player p = e.getPlayer();

        if(VersionUtils.isAtLeastVersion19()) {
            BossBarHandler bossBarHandler = instance.getActiveBossBar().get(p);
            if(bossBarHandler != null) bossBarHandler.stop();
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            TitleHandler.handle(p, "titles.register", true);
            ActionBarHandler.handle(p, "register");
        }, 3L);

    }

}
