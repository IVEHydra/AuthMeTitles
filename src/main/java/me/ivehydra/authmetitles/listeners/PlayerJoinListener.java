package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.api.v3.AuthMeApi;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar18Handler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar19Handler;
import me.ivehydra.authmetitles.handler.handlers.TitleHandler;
import me.ivehydra.authmetitles.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final AuthMeTitles instance = AuthMeTitles.getInstance();
    private final AuthMeApi instanceAuthMe = AuthMeApi.getInstance();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            if(!instanceAuthMe.isRegistered(p.getName())) {
                TitleHandler.handle(p, "titles.noRegister", false);
                ActionBarHandler.handle(p, "noRegister");
                if(VersionUtils.isAtLeastVersion19()) BossBar19Handler.handle(p, "noRegister");
                else BossBar18Handler.handle(p, "noRegister");
            } else if(!instanceAuthMe.isAuthenticated(p)) {
                TitleHandler.handle(p, "titles.noLogin", false);
                ActionBarHandler.handle(p, "noLogin");
                if(VersionUtils.isAtLeastVersion19()) BossBar19Handler.handle(p, "noLogin");
                else BossBar18Handler.handle(p, "noLogin");
            }
        }, 20L);

    }

}
