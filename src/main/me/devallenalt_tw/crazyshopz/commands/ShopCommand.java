package me.devallenalt_tw.crazyshopz.commands;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import me.devallenalt_tw.crazyshopz.gui.CategoryGUI;
import me.devallenalt_tw.crazyshopz.gui.MainShopGUI;
import me.devallenalt_tw.crazyshopz.models.ShopCategory;
import me.devallenalt_tw.crazyshopz.utils.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    
    private final CrazyShopZ plugin;
    
    public ShopCommand(CrazyShopZ plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission(Permissions.USE)) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }
        
        // If args provided, try to open specific category
        if (args.length > 0) {
            String categoryId = args[0];
            ShopCategory category = plugin.getCategoryManager().getCategory(categoryId);
            
            if (category == null) {
                player.sendMessage(plugin.getMessage("category-not-found"));
                return true;
            }
            
            plugin.getGuiManager().resetPage(player);
            plugin.getGuiManager().setCurrentCategory(player, categoryId);
            new CategoryGUI(plugin, player, category, 0).open();
        } else {
            // Open main shop
            plugin.getGuiManager().resetPage(player);
            new MainShopGUI(plugin, player).open();
        }
        
        return true;
    }
}
