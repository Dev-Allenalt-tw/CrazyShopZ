package me.devallenalt_tw.crazyshopz.utils;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class EconomyUtil {
    
    private static final CrazyShopZ plugin = CrazyShopZ.getInstance();
    private static final DecimalFormat df = new DecimalFormat("#.##");
    
    public static boolean hasEnough(Player player, double amount) {
        Economy economy = plugin.getEconomy();
        return economy.has(player, amount);
    }
    
    public static boolean withdraw(Player player, double amount) {
        Economy economy = plugin.getEconomy();
        if (economy.has(player, amount)) {
            economy.withdrawPlayer(player, amount);
            plugin.addSpent(player.getUniqueId(), amount);
            return true;
        }
        return false;
    }
    
    public static void deposit(Player player, double amount) {
        Economy economy = plugin.getEconomy();
        economy.depositPlayer(player, amount);
        plugin.addEarned(player.getUniqueId(), amount);
    }
    
    public static double getBalance(Player player) {
        Economy economy = plugin.getEconomy();
        return economy.getBalance(player);
    }
    
    public static String format(double amount) {
        String symbol = plugin.getConfig().getString("economy.currency-symbol", "$");
        return symbol + df.format(amount);
    }
    
    public static String formatNoSymbol(double amount) {
        return df.format(amount);
    }
}
