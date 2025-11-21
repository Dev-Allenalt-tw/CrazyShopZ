package me.devallenalt_tw.crazyshopz.commands;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import me.devallenalt_tw.crazyshopz.gui.SellGUI;
import me.devallenalt_tw.crazyshopz.utils.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellGUICommand implements CommandExecutor {
    
    private final CrazyShopZ plugin;
    
    public SellGUICommand(CrazyShopZ plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission(Permissions.SELL)) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        new SellGUI(plugin, player).open();
        
        return true;
    }
}
