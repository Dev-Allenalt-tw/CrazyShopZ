package me.devallenalt_tw.crazyshopz.models;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {
    
    private final String id;
    private final Material material;
    private final String name;
    private final int slot;
    private final double buyPrice;
    private final double sellPrice;
    private final List<String> customLore;
    
    public ShopItem(String id, Material material, String name, int slot, double buyPrice, double sellPrice, List<String> customLore) {
        this.id = id;
        this.material = material;
        this.name = name;
        this.slot = slot;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.customLore = customLore != null ? customLore : new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getName() {
        return name;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public double getBuyPrice() {
        return buyPrice;
    }
    
    public double getSellPrice() {
        return sellPrice;
    }
    
    public List<String> getCustomLore() {
        return customLore;
    }
    
    public boolean canBuy() {
        return buyPrice > 0;
    }
    
    public boolean canSell() {
        return sellPrice > 0;
    }
}
