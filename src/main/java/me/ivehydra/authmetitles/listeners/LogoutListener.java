package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.events.LogoutEvent;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar18Handler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar19Handler;
import me.ivehydra.authmetitles.handler.handlers.TitleHandler;
import me.ivehydra.authmetitles.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LogoutListener implements Listener {

    @EventHandler
    public void onLogoutEvent(LogoutEvent e) {
        Player p = e.getPlayer();

        TitleHandler.handle(p, "titles.noLogin", false);
        ActionBarHandler.handle(p, "noLogin");
        if(VersionUtils.isAtLeastVersion19()) BossBar19Handler.handle(p, "noLogin");
        else BossBar18Handler.handle(p, "noLogin");

    }

}
