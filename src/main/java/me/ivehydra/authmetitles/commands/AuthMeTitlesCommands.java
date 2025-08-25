package me.ivehydra.authmetitles.commands;

import me.ivehydra.authmetitles.AuthMeTitles;
import me.ivehydra.authmetitles.utils.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AuthMeTitlesCommands implements CommandExecutor {

    private final AuthMeTitles instance = AuthMeTitles.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("authmetitles")) {

            boolean isPlayer = sender instanceof Player;
            Player p = isPlayer ? (Player) sender : null;

            if(args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                if(isPlayer && !p.hasPermission("authmetitles.help"))
                    sendNoHelp(p);
                else
                    sendHelp(sender);
                return true;
            }

            if(args.length == 1 && (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl"))) {
                if(isPlayer && !p.hasPermission("authmetitles.reload")) {
                    p.sendMessage(MessageUtils.NO_PERMISSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                    return true;
                }
                instance.reloadConfig();
                sender.sendMessage(MessageUtils.CONFIG_RELOADED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                return true;
            }

            sender.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
            return true;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sendNoHelp(sender);
        sender.sendMessage(ChatColor.GREEN + "Commands:");
        sender.sendMessage(ChatColor.GREEN + "/authmetitles help" + ChatColor.GRAY + " - Sends a message with all commands and permissions.");
        sender.sendMessage(ChatColor.GREEN + "/authmetitles reload | rl" + ChatColor.GRAY + " - Reloads the configuration file.");
        sender.sendMessage(ChatColor.GREEN + "Permissions:");
        sender.sendMessage(ChatColor.GREEN + "authmetitles.*" + ChatColor.GRAY + " - Allows to execute all commands.");
        sender.sendMessage(ChatColor.GREEN + "authmetitles.help" + ChatColor.GRAY + " - Allows to see all commands and permissions.");
        sender.sendMessage(ChatColor.GREEN + "authmetitles.reload" + ChatColor.GRAY + " - Allows to reload the configuration file.");
        sendNoHelp(sender);
    }

    private void sendNoHelp(CommandSender sender) {
        String latestVersion = instance.getLatestVersion();
        String currentVersion = instance.getDescription().getVersion();
        String color = ChatColor.GREEN.toString();

        if(latestVersion != null && !currentVersion.equals(latestVersion))
            color = ChatColor.RED.toString();

        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.GREEN + "AuthMeTitles by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + color + currentVersion + ChatColor.GRAY + " -------");
    }

}
