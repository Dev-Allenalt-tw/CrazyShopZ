package me.devallenalt_tw.crazyshopz.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Color {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    
    public static String translate(String message) {
        if (message == null) return "";
        
        // Translate hex colors
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, "§x"
                    + "§" + group.charAt(0) + "§" + group.charAt(1)
                    + "§" + group.charAt(2) + "§" + group.charAt(3)
                    + "§" + group.charAt(4) + "§" + group.charAt(5));
        }
        
        String translated = matcher.appendTail(buffer).toString();
        
        // Translate standard color codes
        return ChatColor.translateAlternateColorCodes('&', translated);
    }
    
    public static List<String> translate(List<String> messages) {
        List<String> translated = new ArrayList<>();
        for (String message : messages) {
            translated.add(translate(message));
        }
        return translated;
    }
    
    public static String strip(String message) {
        return ChatColor.stripColor(translate(message));
    }
}
