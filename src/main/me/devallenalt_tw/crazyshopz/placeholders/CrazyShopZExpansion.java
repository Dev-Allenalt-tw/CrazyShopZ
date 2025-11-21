package me.devallenalt_tw.crazyshopz.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import me.devallenalt_tw.crazyshopz.utils.Color;
import me.devallenalt_tw.crazyshopz.utils.EconomyUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CrazyShopZExpansion extends PlaceholderExpansion {
    
    private final CrazyShopZ plugin;
    
    public CrazyShopZExpansion(CrazyShopZ plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public @NotNull String getIdentifier() {
        return "crazyshopz";
    }
    
    @Override
    public @NotNull String getAuthor() {
        return "Dev_Allenalt_tw";
    }
    
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }
        
        // %crazyshopz_prefix%
        if (params.equalsIgnoreCase("prefix")) {
            return Color.translate(plugin.getConfig().getString("prefix", "&6&lCRAZYSHOP&f&lZ"));
        }
        
        // %crazyshopz_spent%
        if (params.equalsIgnoreCase("spent")) {
            return EconomyUtil.formatNoSymbol(plugin.getSpent(player.getUniqueId()));
        }
        
        // %crazyshopz_earned%
        if (params.equalsIgnoreCase("earned")) {
            return EconomyUtil.formatNoSymbol(plugin.getEarned(player.getUniqueId()));
        }
        
        return null;
    }
}
