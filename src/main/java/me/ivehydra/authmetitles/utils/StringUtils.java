package me.ivehydra.authmetitles.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String getColoredString(String string) {
        if(VersionUtils.isAtLeastVersion116()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(string);

            while(matcher.find()) {
                String color = string.substring(matcher.start(), matcher.end());
                string = string.replace(color, String.valueOf(ChatColor.of(color)));
                matcher = pattern.matcher(string);
            }
        }

        string = ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

}
