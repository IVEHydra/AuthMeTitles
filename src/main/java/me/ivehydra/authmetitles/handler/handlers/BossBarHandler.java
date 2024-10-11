package me.ivehydra.authmetitles.handler.handlers;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.v3.AuthMeApi;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.AbstractHandler;
import me.ivehydra.authmetitles.utils.MessageUtils;
import me.ivehydra.authmetitles.utils.StringUtils;
import me.ivehydra.authmetitles.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class BossBarHandler extends AbstractHandler {

    private final int timeout;
    private BossBar bossBar;
    private final boolean progress;
    private final BarColor color;
    private final BarStyle style;

    public BossBarHandler(Player p, String string, int timeout, boolean progress, BarColor color, BarStyle style) {
        super(p, string);
        this.timeout = timeout;
        this.progress = progress;
        this.color = color;
        this.style = style;
    }

    @Override
    public void start() {
        if(!VersionUtils.isAtLeastVersion19()) return;

        stop();

        bossBar = Bukkit.createBossBar(StringUtils.getColoredString(string).replace("%authme_time%", String.valueOf(timeout)).replace("%prefix%", MessageUtils.PREFIX.getPath()), color, style);
        bossBar.addPlayer(p);

        setTaskId(new BukkitRunnable() {
            int timeLeft = timeout;
            @Override
            public void run() {
                if(timeLeft <= 0 || !p.isOnline()) {
                    stop();
                    return;
                }
                bossBar.setTitle(StringUtils.getColoredString(string).replace("%authme_time%", String.valueOf(timeLeft)).replace("%prefix%", MessageUtils.PREFIX.getPath()));
                if(progress) {
                    double value = (double) timeLeft / timeout;
                    bossBar.setProgress(value);
                }
                timeLeft--;
            }
        }.runTaskTimer(instance, 0L, 20L).getTaskId());
    }

    @Override
    public void stop() {
        super.stop();
        if(bossBar != null) {
            instance.getActiveBossBar().remove(p);
            bossBar.removePlayer(p);
            if(bossBar.getPlayers().isEmpty())
                bossBar = null;
        }
    }

    public static void handle(Player p, String path) {
        AuthMeTitles instance = AuthMeTitles.getInstance();
        BossBarHandler bossBarHandler = instance.getActiveBossBar().get(p);

        if(bossBarHandler != null)
            bossBarHandler.stop();

        boolean progress = instance.getConfig().getBoolean("bossBar.progress");
        BarColor color = BarColor.valueOf(instance.getConfig().getString("bossBar.color"));
        BarStyle style = BarStyle.valueOf(instance.getConfig().getString("bossBar.style"));
        String string = instance.getConfig().getString("bossBar.message." + path);

        string = instance.isPlaceholderAPIPresent() ? PlaceholderAPI.setPlaceholders(p, Objects.requireNonNull(string)) : string;

        AuthMe authMe = AuthMeApi.getInstance().getPlugin();
        int timeOut = authMe.getConfig().getInt("settings.restrictions.timeout");
        BossBarHandler newBossBarHandler = new BossBarHandler(p, string, timeOut, progress, color, style);

        newBossBarHandler.start();
        instance.getActiveBossBar().put(p, newBossBarHandler);
    }

}
