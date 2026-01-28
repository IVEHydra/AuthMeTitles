package me.ivehydra.authmetitles.handler.handlers;

import com.cryptomorin.xseries.messages.Titles;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.AbstractHandler;
import me.ivehydra.authmetitles.utils.MessageUtils;
import me.ivehydra.authmetitles.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TitleHandler extends AbstractHandler {

    private final List<String> titles;
    private final int interval;
    private final boolean loop;
    private final boolean last;
    private int index = 0;

    public TitleHandler(Player p, List<String> titles, int interval, boolean loop, boolean last) {
        super(p, validateTitles(titles));
        this.titles = titles;
        this.interval = interval;
        this.loop = loop;
        this.last = last;
    }

    private static String validateTitles(List<String> titles) {
        if(titles == null || titles.isEmpty())
            throw new IllegalArgumentException("[AuthMeTitles]" + ChatColor.RED + " Titles list cannot be null or empty.");
        return titles.get(0);
    }

    @Override
    public void start() {
        if(!p.isOnline()) {
            stop();
            return;
        }

        if(index >= titles.size()) {
            if(loop && !last)
                index = 0;
            else {
                stop();
                return;
            }
        }

        String[] args = titles.get(index).split(";");
        String title = args[0];
        String subTitle = args[1];
        int fadeIn = Integer.parseInt(args[2]);
        int stay = Integer.parseInt(args[3]);
        int fadeOut = Integer.parseInt(args[4]);

        Titles.sendTitle(p, fadeIn * 20, stay * 20, fadeOut * 20, StringUtils.getColoredString(title).replace("%prefix%", MessageUtils.PREFIX.getPath()), StringUtils.getColoredString(subTitle).replace("%prefix%", MessageUtils.PREFIX.getPath()));

        int delay = (args.length == 6) ? Integer.parseInt(args[5]) : -1;
        long nextDelay = (delay != -1) ? delay : interval;

        setTaskId(Bukkit.getScheduler().scheduleSyncDelayedTask(instance, this::start, nextDelay));

        index++;
    }

    @Override
    public void stop() {
        super.stop();
        instance.getActiveTitle().remove(p.getName());
    }

    public static void handle(Player p, String path, boolean last) {
        if(p == null || !p.isOnline()) return;

        String name = p.getName();
        AuthMeTitles instance = AuthMeTitles.getInstance();
        TitleHandler animation = instance.getActiveTitle().get(name);

        if(animation != null)
            animation.stop();

        boolean enabled = instance.getConfig().getBoolean(path + ".animated.enabled");

        if(enabled) {
            List<String> titles = instance.getConfig().getStringList(path + ".animated.titles");
            titles = instance.isPluginPresent("PlaceholderAPI") ? titles.stream().map(title -> PlaceholderAPI.setPlaceholders(p, title)).collect(Collectors.toList()) : titles;
            int interval = instance.getConfig().getInt(path + ".animated.interval");
            boolean loop = instance.getConfig().getBoolean(path + ".animated.loop");
            TitleHandler successTitle = new TitleHandler(p, titles, interval, loop, last);

            successTitle.start();
            instance.getActiveTitle().put(name, successTitle);
        } else {
            String[] args = Objects.requireNonNull(instance.getConfig().getString(path + ".static.title")).replace("%prefix%", MessageUtils.PREFIX.getPath()).split(";");
            String title = args[0];
            String subTitle = args[1];
            int fadeIn = Integer.parseInt(args[2]);
            int stay = Integer.parseInt(args[3]);
            int fadeOut = Integer.parseInt(args[4]);

            if(instance.isPluginPresent("PlaceholderAPI")) {
                title = PlaceholderAPI.setPlaceholders(p, title);
                subTitle = PlaceholderAPI.setPlaceholders(p, subTitle);
            }
            Titles.sendTitle(p, fadeIn * 20, stay * 20, fadeOut * 20, StringUtils.getColoredString(title), StringUtils.getColoredString(subTitle));
        }
    }

}
