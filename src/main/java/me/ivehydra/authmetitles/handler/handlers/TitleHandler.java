package me.ivehydra.authmetitles.handler.handlers;

import com.cryptomorin.xseries.messages.Titles;
import me.clip.placeholderapi.PlaceholderAPI;
import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.handler.AbstractHandler;
import me.ivehydra.authmetitles.utils.MessageUtils;
import me.ivehydra.authmetitles.utils.StringUtils;
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
        super(p, titles.isEmpty() ? "" : titles.get(0));
        this.titles = titles;
        this.interval = interval;
        this.loop = loop;
        this.last = last;
    }

    @Override
    public void start() {
        if(!titles.isEmpty())
            send();
    }

    private void send() {
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

        setTaskId(Bukkit.getScheduler().scheduleSyncDelayedTask(instance, this::send, interval));

        index++;
    }

    @Override
    public void stop() {
        super.stop();
        if(last) instance.getActiveTitle().remove(p);
    }

    public static void handle(Player p, String path, boolean last) {
        AuthMeTitles instance = AuthMeTitles.getInstance();
        TitleHandler animation = instance.getActiveTitle().get(p);

        if(animation != null)
            animation.stop();

        if(instance.getConfig().getBoolean(path + ".animated.enabled")) {
            List<String> titles = instance.getConfig().getStringList(path + ".animated.titles");
            titles = instance.isPlaceholderAPIPresent() ? titles.stream().map(title -> PlaceholderAPI.setPlaceholders(p, title)).collect(Collectors.toList()) : titles;
            int interval = instance.getConfig().getInt(path + ".animated.interval");
            boolean loop = instance.getConfig().getBoolean(path + ".animated.loop");
            TitleHandler successTitle = new TitleHandler(p, titles, interval, loop, last);

            successTitle.start();
            instance.getActiveTitle().put(p, successTitle);
        } else {
            String[] args = Objects.requireNonNull(instance.getConfig().getString(path + ".static.title")).replace("%prefix%", MessageUtils.PREFIX.getPath()).split(";");
            if(instance.isPlaceholderAPIPresent()) {
                args[0] = PlaceholderAPI.setPlaceholders(p, args[0]);
                args[1] = PlaceholderAPI.setPlaceholders(p, args[1]);
            }
            Titles.sendTitle(p, Integer.parseInt(args[2]) * 20, Integer.parseInt(args[3]) * 20, Integer.parseInt(args[4]) * 20, StringUtils.getColoredString(args[0]), StringUtils.getColoredString(args[1]));
        }
    }

}
