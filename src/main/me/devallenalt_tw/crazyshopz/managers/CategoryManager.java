package me.devallenalt_tw.crazyshopz.managers;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import me.devallenalt_tw.crazyshopz.models.ShopCategory;
import me.devallenalt_tw.crazyshopz.models.ShopItem;
import me.devallenalt_tw.crazyshopz.utils.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class CategoryManager {
    
    private final CrazyShopZ plugin;
    private final Map<String, ShopCategory> categories;
    
    public CategoryManager(CrazyShopZ plugin) {
        this.plugin = plugin;
        this.categories = new LinkedHashMap<>();
    }
    
    public void loadCategories() {
        categories.clear();
        
        ConfigurationSection categoriesSection = plugin.getCategoriesConfig().getConfigurationSection("categories");
        if (categoriesSection == null) {
            plugin.getLogger().warning("No categories found in categories.yml!");
            return;
        }
        
        for (String categoryId : categoriesSection.getKeys(false)) {
            ConfigurationSection categorySection = categoriesSection.getConfigurationSection(categoryId);
            if (categorySection == null) continue;
            
            String name = Color.translate(categorySection.getString("name", categoryId));
            int slot = categorySection.getInt("slot", 0);
            
            String iconStr = categorySection.getString("item", "CHEST");
            Material icon = Material.getMaterial(iconStr);
            if (icon == null) icon = Material.CHEST;
            
            List<String> lore = new ArrayList<>();
            if (categorySection.contains("lore")) {
                for (String line : categorySection.getStringList("lore")) {
                    lore.add(Color.translate(line));
                }
            }
            
            ShopCategory category = new ShopCategory(categoryId, name, slot, icon, lore);
            
            // Load items for this category
            ConfigurationSection itemsSection = categorySection.getConfigurationSection("items");
            if (itemsSection != null) {
                for (String itemId : itemsSection.getKeys(false)) {
                    ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemId);
                    if (itemSection == null) continue;
                    
                    String materialStr = itemSection.getString("material", "STONE");
                    Material material = Material.getMaterial(materialStr);
                    if (material == null) material = Material.STONE;
                    
                    String itemName = Color.translate(itemSection.getString("name", itemId));
                    int itemSlot = itemSection.getInt("slot", 0);
                    double buyPrice = itemSection.getDouble("buy-price", -1.0);
                    double sellPrice = itemSection.getDouble("sell-price", -1.0);
                    
                    List<String> customLore = new ArrayList<>();
                    if (itemSection.contains("custom-lore")) {
                        for (String line : itemSection.getStringList("custom-lore")) {
                            customLore.add(Color.translate(line));
                        }
                    }
                    
                    ShopItem shopItem = new ShopItem(itemId, material, itemName, itemSlot, buyPrice, sellPrice, customLore);
                    category.addItem(shopItem);
                }
            }
            
            categories.put(categoryId, category);
        }
        
        plugin.getLogger().info("Loaded " + categories.size() + " categories.");
    }
    
    public ShopCategory getCategory(String id) {
        return categories.get(id);
    }
    
    public Collection<ShopCategory> getAllCategories() {
        return categories.values();
    }
    
    public Map<String, ShopCategory> getCategoriesMap() {
        return categories;
    }
}
