package me.ivehydra.authmetitles.listeners;

import fr.xephi.authme.events.UnregisterByAdminEvent;
import fr.xephi.authme.events.UnregisterByPlayerEvent;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar18Handler;
import me.ivehydra.authmetitles.handler.handlers.bossbar.BossBar19Handler;
import me.ivehydra.authmetitles.handler.handlers.TitleHandler;
import me.ivehydra.authmetitles.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UnregisterListener implements Listener {

    @EventHandler
    public void onAdminUnregisterEvent(UnregisterByAdminEvent e) {
        Player p = e.getPlayer();

        TitleHandler.handle(p, "titles.noRegister", false);
        ActionBarHandler.handle(p, "noRegister");
        if(VersionUtils.isAtLeastVersion19()) BossBar19Handler.handle(p, "noRegister");
        else BossBar18Handler.handle(p, "noRegister");

    }

    @EventHandler
    public void onPlayerUnregisterEvent(UnregisterByPlayerEvent e) {
        Player p = e.getPlayer();

        TitleHandler.handle(p, "titles.noRegister", false);
        ActionBarHandler.handle(p, "noRegister");
        if(VersionUtils.isAtLeastVersion19()) BossBar19Handler.handle(p, "noRegister");
        else BossBar18Handler.handle(p, "noRegister");

    }

}
