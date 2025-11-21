package me.devallenalt_tw.crazyshopz.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopCategory {
    
    private final String id;
    private final String name;
    private final int slot;
    private final Material icon;
    private final List<String> lore;
    private final List<ShopItem> items;
    
    public ShopCategory(String id, String name, int slot, Material icon, List<String> lore) {
        this.id = id;
        this.name = name;
        this.slot = slot;
        this.icon = icon;
        this.lore = lore != null ? lore : new ArrayList<>();
        this.items = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public Material getIcon() {
        return icon;
    }
    
    public List<String> getLore() {
        return lore;
    }
    
    public List<ShopItem> getItems() {
        return items;
    }
    
    public void addItem(ShopItem item) {
        items.add(item);
    }
}
