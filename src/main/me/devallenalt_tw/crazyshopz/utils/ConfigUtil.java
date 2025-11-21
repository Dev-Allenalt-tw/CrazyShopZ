package me.devallenalt_tw.crazyshopz.utils;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {
    
    private final CrazyShopZ plugin;
    
    public ConfigUtil(CrazyShopZ plugin) {
        this.plugin = plugin;
    }
    
    public ItemStack getGuiItem(String path) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("gui-items." + path);
        if (section == null) return null;
        
        String materialStr = section.getString("item", "STONE");
        Material material = Material.getMaterial(materialStr);
        if (material == null) material = Material.STONE;
        
        String name = Color.translate(section.getString("name", "&fItem"));
        List<String> lore = new ArrayList<>();
        
        if (section.contains("lore")) {
            for (String line : section.getStringList("lore")) {
                lore.add(Color.translate(line));
            }
        }
        
        return GUIBuilder.createItem(material, name, lore);
    }
    
    public int getGuiSlot(String path) {
        return plugin.getConfig().getInt("gui-items." + path + ".slot", 0);
    }
    
    public String getMenuTitle(String type) {
        return Color.translate(plugin.getConfig().getString("menu." + type + ".title", "&8Menu"));
    }
    
    public int getMenuSize(String type) {
        return plugin.getConfig().getInt("menu." + type + ".size", 54);
    }
    
    public ItemStack getFillerItem() {
        String materialStr = plugin.getConfig().getString("gui-items.filler.item", "GRAY_STAINED_GLASS_PANE");
        Material material = Material.getMaterial(materialStr);
        if (material == null) material = Material.GRAY_STAINED_GLASS_PANE;
        
        String name = Color.translate(plugin.getConfig().getString("gui-items.filler.name", " "));
        return GUIBuilder.createItem(material, name, new ArrayList<>());
    }
}
