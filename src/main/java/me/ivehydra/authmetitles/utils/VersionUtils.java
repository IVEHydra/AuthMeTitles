package me.ivehydra.authmetitles.utils;

import me.ivehydra.authmetitles.AuthMeTitles;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class VersionUtils {

    private static final AuthMeTitles instance = AuthMeTitles.getInstance();

    private static boolean isVersionAtLeast(int major, int min) {
        String version = Bukkit.getBukkitVersion();
        String numericVersion = version.split("-")[0];
        String[] args = numericVersion.split("\\.");

        try {
            int serverMajor = Integer.parseInt(args[0]);
            int serverMin = Integer.parseInt(args[1]);

            if(serverMajor > major) return true;
            else if(serverMajor == major) return serverMin >= min;
            else return false;
        } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            instance.sendLog("[AuthMeTitles]" + ChatColor.RED + " Error while parsing Bukkit Version: " + version);
            instance.sendLog("[AuthMeTitles]" + ChatColor.RED + " Error details: " + e.getMessage());
            return false;
        }
    }

    public static boolean isAtLeastVersion19() { return isVersionAtLeast(1, 9); }

    public static boolean isAtLeastVersion116() { return isVersionAtLeast(1, 16); }

}
