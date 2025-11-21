package me.devallenalt_tw.crazyshopz.managers;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import me.devallenalt_tw.crazyshopz.models.ShopItem;
import me.devallenalt_tw.crazyshopz.utils.Color;
import me.devallenalt_tw.crazyshopz.utils.EconomyUtil;
import me.devallenalt_tw.crazyshopz.utils.GUIBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    
    private final CrazyShopZ plugin;
    
    public ItemManager(CrazyShopZ plugin) {
        this.plugin = plugin;
    }
    
    public ItemStack createShopItemStack(ShopItem shopItem) {
        List<String> lore = new ArrayList<>();
        
        // Add custom lore if exists
        if (!shopItem.getCustomLore().isEmpty()) {
            lore.addAll(shopItem.getCustomLore());
            lore.add("");
        }
        
        // Add buy/sell info
        if (shopItem.canBuy() && shopItem.canSell()) {
            for (String line : plugin.getConfig().getStringList("item-display.buy-lore")) {
                String replaced = line
                        .replace("{buy-price}", EconomyUtil.format(shopItem.getBuyPrice()))
                        .replace("{sell-price}", EconomyUtil.format(shopItem.getSellPrice()));
                lore.add(Color.translate(replaced));
            }
        } else if (shopItem.canBuy()) {
            for (String line : plugin.getConfig().getStringList("item-display.no-sell")) {
                String replaced = line
                        .replace("{buy-price}", EconomyUtil.format(shopItem.getBuyPrice()));
                lore.add(Color.translate(replaced));
            }
        } else if (shopItem.canSell()) {
            for (String line : plugin.getConfig().getStringList("item-display.no-buy")) {
                String replaced = line
                        .replace("{sell-price}", EconomyUtil.format(shopItem.getSellPrice()));
                lore.add(Color.translate(replaced));
            }
        }
        
        return GUIBuilder.createItem(shopItem.getMaterial(), shopItem.getName(), lore);
    }
    
    public double getSellPrice(ItemStack item) {
        if (item == null || item.getType() == org.bukkit.Material.AIR) return 0.0;
        
        // Search through all categories for matching item
        for (var category : plugin.getCategoryManager().getAllCategories()) {
            for (ShopItem shopItem : category.getItems()) {
                if (shopItem.getMaterial() == item.getType() && shopItem.canSell()) {
                    return shopItem.getSellPrice();
                }
            }
        }
        
        return 0.0;
    }
    
    public boolean isSellable(ItemStack item) {
        return getSellPrice(item) > 0;
    }
}
