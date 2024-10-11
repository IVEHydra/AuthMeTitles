package me.ivehydra.authmetitles;

import me.ivehydra.authmetitles.commands.AuthMeTitlesCommands;
import me.ivehydra.authmetitles.commands.AuthMeTitlesTabCompleter;
import me.ivehydra.authmetitles.handler.handlers.ActionBarHandler;
import me.ivehydra.authmetitles.handler.handlers.BossBarHandler;
import me.ivehydra.authmetitles.handler.handlers.TitleHandler;
import me.ivehydra.authmetitles.listeners.*;
import me.ivehydra.authmetitles.utils.MessageUtils;
import me.ivehydra.authmetitles.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Consumer;

public class AuthMeTitles extends JavaPlugin {

    private static AuthMeTitles instance;
    private Map<Player, TitleHandler> activeTitle;
    private Map<Player, ActionBarHandler> activeActionBar;
    private Map<Player, BossBarHandler> activeBossBar;

    @Override
    public void onEnable() {
        instance = this;
        activeTitle = new HashMap<>();
        activeActionBar = new HashMap<>();
        activeBossBar = new HashMap<>();

        if(!registerAuthMe()) {
            sendLog("[AuthMeTitles]" + ChatColor.RED + " AuthMe Not Found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if(isPlaceholderAPIPresent()) sendLog("[AuthMeTitles]" + ChatColor.GREEN + " PlaceholderAPI has been found. Now you can use PlaceholderAPI placeholders for Titles, Animated Titles, ActionBars and BossBars.");
        else sendLog("[AuthMeTitles]" + ChatColor.YELLOW + " PlaceholderAPI not found. The plugin will still function correctly, but you won't be able to use PlaceholderAPI placeholders for Titles, Animated Titles, ActionBars and BossBars.");

        registerConfigFile();
        registerCommands();
        registerListeners();

        updateChecker(version -> {
            if(getDescription().getVersion().equals(version)) sendLog(MessageUtils.LATEST_VERSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
            else instance.getConfig().getStringList(MessageUtils.NEW_VERSION.getPath()).forEach(message -> sendLog(StringUtils.getColoredString(message).replace("%prefix%", MessageUtils.PREFIX.toString())));
        });
    }

    public static AuthMeTitles getInstance() { return instance; }

    public Map<Player, TitleHandler> getActiveTitle() { return activeTitle; }

    public Map<Player, ActionBarHandler> getActiveActionBar() { return activeActionBar; }

    public Map<Player, BossBarHandler> getActiveBossBar() { return activeBossBar; }

    private boolean registerAuthMe() { return Bukkit.getPluginManager().getPlugin("AuthMe") != null; }

    public boolean isPlaceholderAPIPresent() { return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null; }

    private void registerConfigFile() {
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()) saveResource("config.yml", false);
        File config = new File(getDataFolder(), "config.yml");
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(config);
        InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(getResource("config.yml")), StandardCharsets.UTF_8);
        YamlConfiguration yamlReader = YamlConfiguration.loadConfiguration(reader);
        for(String string : yamlReader.getKeys(true))
            if(!yamlConfig.contains(string)) yamlConfig.set(string, yamlReader.get(string));
        try {
            yamlConfig.save(config);
        } catch(IOException e) {
            sendLog("[AuthMeTitles]" + ChatColor.RED + " An error occurred while trying to save the configuration file.");
            sendLog("[AuthMeTitles]" + ChatColor.RED + " Error details: " + e.getMessage());
        }
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("authmetitles")).setExecutor(new AuthMeTitlesCommands());
        Objects.requireNonNull(getCommand("authmetitles")).setTabCompleter(new AuthMeTitlesTabCompleter());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new RegisterListener(), this);
        pm.registerEvents(new LoginListener(), this);
        pm.registerEvents(new RestoreSessionListener(), this);
        pm.registerEvents(new UnregisterListener(), this);
        pm.registerEvents(new LogoutListener(), this);
    }

    private void updateChecker(Consumer<String> consumer) {
        if(!instance.getConfig().getBoolean("updateCheck")) return;
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try(InputStream stream = new URL("https://api.spigotmc.org/legacy/update.php?resource=111370").openStream()) {
                Scanner scanner = new Scanner(stream);
                if(scanner.hasNext()) consumer.accept(scanner.next());
            } catch(IOException e) {
                sendLog("[AuthMeTitles]" + ChatColor.RED + " Can't find a new version!");
                sendLog("[AuthMeTitles]" + ChatColor.RED + " Error details: " + e.getMessage());
            }
        });
    }

    public void sendLog(String string) { getServer().getConsoleSender().sendMessage(string); }

}
