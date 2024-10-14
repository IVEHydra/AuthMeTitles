package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.events.RegisterEvent;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.AbstractHandler;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar18Handler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar19Handler;
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
        AbstractHandler abstractHandler = instance.getActiveBossBar().get(p);

        if(abstractHandler != null) {
            if(VersionUtils.isAtLeastVersion19() && abstractHandler instanceof BossBar19Handler) abstractHandler.stop();
            else if(!VersionUtils.isAtLeastVersion19() && abstractHandler instanceof BossBar18Handler) abstractHandler.stop();
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            TitleHandler.handle(p, "titles.register", true);
            ActionBarHandler.handle(p, "register");
        }, 3L);

    }

}
