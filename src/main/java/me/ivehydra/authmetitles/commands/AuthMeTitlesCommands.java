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
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("authmetitles")) {
            if(!(sender instanceof Player)) {
                switch(args.length) {
                    case 0:
                        sendHelp(sender);
                        break;
                    case 1:
                        if(args[0].equalsIgnoreCase("help")) {
                            sendHelp(sender);
                        } else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                            instance.reloadConfig();
                            sender.sendMessage(MessageUtils.CONFIG_RELOADED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        } else {
                            sender.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                            return true;
                        }
                        break;
                    default:
                        sender.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        return true;
                }
                return true;
            }
            Player p = (Player) sender;
            switch(args.length) {
                case 0:
                    if(!p.hasPermission("authmetitles.help")) {
                        sendNoHelp(p);
                        return true;
                    }
                    sendHelp(p);
                    break;
                case 1:
                    if(args[0].equalsIgnoreCase("help")) {
                        if(!p.hasPermission("authmetitles.help")) {
                            sendNoHelp(p);
                            return true;
                        }
                        sendHelp(p);
                    } else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                        if(!p.hasPermission("authmetitles.reload")) {
                            p.sendMessage(MessageUtils.NO_PERMISSION.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                            return true;
                        }
                        instance.reloadConfig();
                        p.sendMessage(MessageUtils.CONFIG_RELOADED.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                    } else {
                        p.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                        return true;
                    }
                    break;
                default:
                    p.sendMessage(MessageUtils.WRONG_ARGUMENTS.getFormattedMessage("%prefix%", MessageUtils.PREFIX.toString()));
                    return true;
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.GREEN + "AuthMeTitles by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.GREEN + instance.getDescription().getVersion() + ChatColor.GRAY + " -------");
        sender.sendMessage(ChatColor.GREEN + "Commands:");
        sender.sendMessage(ChatColor.GREEN + "/authmetitles help" + ChatColor.GRAY + " - Sends a message with all commands and permissions.");
        sender.sendMessage(ChatColor.GREEN + "/authmetitles reload | rl" + ChatColor.GRAY + " - Reloads the configuration file.");
        sender.sendMessage(ChatColor.GREEN + "Permissions:");
        sender.sendMessage(ChatColor.GREEN + "authmetitles.*" + ChatColor.GRAY + " - Allows to execute all commands.");
        sender.sendMessage(ChatColor.GREEN + "authmetitles.help" + ChatColor.GRAY + " - Allows to see all commands and permissions.");
        sender.sendMessage(ChatColor.GREEN + "authmetitles.reload" + ChatColor.GRAY + " - Allows to reload the configuration file.");
        sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.GREEN + "AuthMeTitles by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.GREEN + instance.getDescription().getVersion() + ChatColor.GRAY + " -------");
    }

    private void sendNoHelp(CommandSender sender) { sender.sendMessage(ChatColor.GRAY + "------- " + ChatColor.GREEN + "AuthMeTitles by " + ChatColor.YELLOW + "IVEHydra" + ChatColor.GRAY + " v" + ChatColor.GREEN + instance.getDescription().getVersion() + ChatColor.GRAY + " -------"); }

}
