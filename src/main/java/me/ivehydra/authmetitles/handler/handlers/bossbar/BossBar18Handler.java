package me.ivehydra.authmetitles.handler.handlers.bossbar;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.v3.AuthMeApi;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.AbstractHandler;
import me.ivehydra.authmetitles.utils.BossBarUtils;
import me.ivehydra.authmetitles.utils.MessageUtils;
import me.ivehydra.authmetitles.utils.StringUtils;
import me.ivehydra.authmetitles.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class BossBar18Handler extends AbstractHandler {

    private final AuthMeTitles instance = AuthMeTitles.getInstance();
    private final int timeout;
    private final boolean progress;

    public BossBar18Handler(Player p, String string, int timeout, boolean progress) {
        super(p, string);
        this.timeout = timeout;
        this.progress = progress;
    }

    @Override
    public void start() {
        if(VersionUtils.isAtLeastVersion19()) return;

        BossBarUtils.addWither(p, StringUtils.getColoredString(string).replace("%authme_time%", String.valueOf(timeout)).replace("%prefix%", MessageUtils.PREFIX.toString()), 300);

        setTaskId(new BukkitRunnable() {
            int timeLeft = timeout;
            @Override
            public void run() {
                if(timeLeft <= 0 || !p.isOnline()) {
                    stop();
                    return;
                }
                BossBarUtils.updateText(p, StringUtils.getColoredString(string).replace("%authme_time%", String.valueOf(timeLeft)).replace("%prefix%", MessageUtils.PREFIX.toString()));
                if(progress) {
                    float value = (float) timeLeft / timeout * 300;
                    BossBarUtils.updateHealth(p, value);
                }
                timeLeft--;
            }
        }.runTaskTimer(instance, 0L, 20L).getTaskId());

    }

    @Override
    public void stop() {
        super.stop();
        BossBarUtils.removeWither(p);
        instance.getActiveBossBar().remove(p);
    }

    public static void handle(Player p, String path) {
        if(p == null || !p.isOnline()) return;

        AuthMeTitles instance = AuthMeTitles.getInstance();
        boolean enabled = instance.getConfig().getBoolean("bossBar.enabled");

        if(!enabled) return;

        AbstractHandler abstractHandler = instance.getActiveBossBar().get(p);

        if(abstractHandler != null)
            abstractHandler.stop();

        boolean progress = instance.getConfig().getBoolean("bossBar.progress");
        String string = instance.getConfig().getString("bossBar.message." + path);
        string = instance.isPlaceholderAPIPresent() ? PlaceholderAPI.setPlaceholders(p, Objects.requireNonNull(string)) : string;
        AuthMe authMe = AuthMeApi.getInstance().getPlugin();
        int timeOut = authMe.getConfig().getInt("settings.restrictions.timeout");
        BossBar18Handler newBossBarHandler = new BossBar18Handler(p, string, timeOut, progress);

        newBossBarHandler.start();
        instance.getActiveBossBar().put(p, newBossBarHandler);
    }

}
