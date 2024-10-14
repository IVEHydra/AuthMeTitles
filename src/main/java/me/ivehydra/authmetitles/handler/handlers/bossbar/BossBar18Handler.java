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

        stop();

        try {
            BossBarUtils.addBossBar(p, StringUtils.getColoredString(string).replace("%authme_time%", String.valueOf(timeout)).replace("%prefix%", MessageUtils.PREFIX.toString()), 100);
        } catch(Exception e) {
            instance.sendLog("[AuthMeTitles] " + e.getMessage());
        }

        setTaskId(new BukkitRunnable() {
            int timeLeft = timeout;
            @Override
            public void run() {
                if(timeLeft <= 0 || !p.isOnline()) {
                    stop();
                    return;
                }
                try {
                    BossBarUtils.updateText(p, StringUtils.getColoredString(string).replace("%authme_time%", String.valueOf(timeLeft)).replace("%prefix%", MessageUtils.PREFIX.toString()));
                    if(progress) {
                        double value = (double) timeLeft / timeout;
                        BossBarUtils.updateHealth(p, (float) value);
                    }
                } catch (Exception e) {
                    instance.sendLog("[AuthMeTitles] " + e.getMessage());
                }
                timeLeft--;
            }
        }.runTaskTimer(instance, 0L, 20L).getTaskId());

    }

    @Override
    public void stop() {
        super.stop();
        try {
            instance.getActiveBossBar().remove(p);
            BossBarUtils.removeBossBar(p);
        } catch (Exception e) {
            instance.sendLog("[AuthMeTitles] " + e.getMessage());
        }
    }

    public static void handle(Player p, String path) {
        AuthMeTitles instance = AuthMeTitles.getInstance();
        AbstractHandler abstractHandler = instance.getActiveBossBar().get(p);
        if(abstractHandler != null) {
            if(abstractHandler instanceof BossBar18Handler) {
                instance.getLogger().info("BossBar18Handler : AbstractHandler");
                abstractHandler.stop();
            }
        }

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
