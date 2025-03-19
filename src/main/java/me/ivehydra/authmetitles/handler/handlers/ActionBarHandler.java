package me.ivehydra.authmetitles.handler.handlers;

import com.cryptomorin.xseries.messages.ActionBar;
import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.v3.AuthMeApi;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.AbstractHandler;
import me.ivehydra.authmetitles.utils.MessageUtils;
import me.ivehydra.authmetitles.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class ActionBarHandler extends AbstractHandler {

    private final int timeout;
    private final boolean dynamic;

    public ActionBarHandler(Player p, String string, int timeout, boolean dynamic) {
        super(p, string);
        this.timeout = timeout;
        this.dynamic = dynamic;
    }

    @Override
    public void start() {

        if(!dynamic) {
            ActionBar.sendActionBar(p, StringUtils.getColoredString(string).replace("%prefix%", MessageUtils.PREFIX.getPath()));
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, this::stop, 1L);
        } else {
            setTaskId(new BukkitRunnable() {
                int timeLeft = timeout;
                @Override
                public void run() {
                    if(timeLeft <= 0 || !p.isOnline()) {
                        stop();
                        return;
                    }
                    ActionBar.sendActionBar(p, StringUtils.getColoredString(string).replace("%authme_time%", String.valueOf(timeLeft)).replace("%prefix%", MessageUtils.PREFIX.getPath()));
                    timeLeft--;
                }
            }.runTaskTimer(instance, 0L, 20L).getTaskId());
        }
    }

    @Override
    public void stop() {
        super.stop();
        instance.getActiveActionBar().remove(p);
    }

    public static void handle(Player p, String path) {
        if(p == null || !p.isOnline()) return;

        AuthMeTitles instance = AuthMeTitles.getInstance();
        boolean enabled = instance.getConfig().getBoolean("actionBar.enabled");

        if(!enabled) return;

        ActionBarHandler actionBarHandler = instance.getActiveActionBar().get(p);

        if(actionBarHandler != null)
            actionBarHandler.stop();

        String actionBar = instance.getConfig().getString("actionBar." + path);
        actionBar = instance.isPlaceholderAPIPresent() ? PlaceholderAPI.setPlaceholders(p, Objects.requireNonNull(actionBar)) : actionBar;
        boolean dynamic = path.equals("noRegister") || path.equals("noLogin");
        AuthMe authMe = AuthMeApi.getInstance().getPlugin();
        int configTimeOut = authMe.getConfig().getInt("settings.restrictions.timeout");
        int timeout = dynamic ? configTimeOut : 0;
        ActionBarHandler newActionBarHandler = new ActionBarHandler(p, actionBar, timeout, dynamic);

        newActionBarHandler.start();
        instance.getActiveActionBar().put(p, newActionBarHandler);
    }

}
