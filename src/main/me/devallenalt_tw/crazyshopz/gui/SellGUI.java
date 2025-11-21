package me.devallenalt_tw.crazyshopz.gui;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import me.devallenalt_tw.crazyshopz.utils.Color;
import me.devallenalt_tw.crazyshopz.utils.EconomyUtil;
import me.devallenalt_tw.crazyshopz.utils.GUIBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellGUI implements Listener {
    
    private final CrazyShopZ plugin;
    private final Player player;
    private final Inventory inventory;
    private final List<Integer> sellSlots;
    private final Map<Integer, ItemStack> itemsToSell;
    
    public SellGUI(CrazyShopZ plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.itemsToSell = new HashMap<>();
        
        String title = plugin.getConfigUtil().getMenuTitle("sell-gui");
        int size = plugin.getConfigUtil().getMenuSize("sell-gui");
        this.inventory = Bukkit.createInventory(null, size, title);
        
        // Load sell slots from config
        this.sellSlots = plugin.getConfig().getIntegerList("sell-gui.slots");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupInventory();
    }
    
    private void setupInventory() {
        // Fill with filler items
        ItemStack filler = plugin.getConfigUtil().getFillerItem();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!sellSlots.contains(i)) {
                inventory.setItem(i, filler);
            }
        }
        
        // Add confirm button
        ItemStack confirm = getConfirmButton(0.0);
        int confirmSlot = plugin.getConfig().getInt("sell-gui.confirm-button.slot", 48);
        inventory.setItem(confirmSlot, confirm);
        
        // Add cancel button
        String cancelMaterial = plugin.getConfig().getString("sell-gui.cancel-button.item", "RED_DYE");
        Material cancelMat = Material.getMaterial(cancelMaterial);
        if (cancelMat == null) cancelMat = Material.RED_DYE;
        
        String cancelName = Color.translate(plugin.getConfig().getString("sell-gui.cancel-button.name", "&c&lCancel"));
        List<String> cancelLore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("sell-gui.cancel-button.lore")) {
            cancelLore.add(Color.translate(line));
        }
        
        ItemStack cancel = GUIBuilder.createItem(cancelMat, cancelName, cancelLore);
        int cancelSlot = plugin.getConfig().getInt("sell-gui.cancel-button.slot", 50);
        inventory.setItem(cancelSlot, cancel);
    }
    
    private ItemStack getConfirmButton(double totalValue) {
        String confirmMaterial = plugin.getConfig().getString("sell-gui.confirm-button.item", "LIME_DYE");
        Material confirmMat = Material.getMaterial(confirmMaterial);
        if (confirmMat == null) confirmMat = Material.LIME_DYE;
        
        String confirmName = Color.translate(plugin.getConfig().getString("sell-gui.confirm-button.name", "&a&lConfirm Sale"));
        List<String> confirmLore = new ArrayList<>();
        for (String line : plugin.getConfig().getStringList("sell-gui.confirm-button.lore")) {
            String replaced = line.replace("{value}", EconomyUtil.format(totalValue));
            confirmLore.add(Color.translate(replaced));
        }
        
        return GUIBuilder.createItem(confirmMat, confirmName, confirmLore);
    }
    
    private void updateConfirmButton() {
        double totalValue = calculateTotalValue();
        ItemStack confirm = getConfirmButton(totalValue);
        int confirmSlot = plugin.getConfig().getInt("sell-gui.confirm-button.slot", 48);
        inventory.setItem(confirmSlot, confirm);
    }
    
    private double calculateTotalValue() {
        double total = 0.0;
        
        for (int slot : sellSlots) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                double price = plugin.getItemManager().getSellPrice(item);
                if (price > 0) {
                    total += price * item.getAmount();
                }
            }
        }
        
        return total;
    }
    
    public void open() {
        player.openInventory(inventory);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        
        int confirmSlot = plugin.getConfig().getInt("sell-gui.confirm-button.slot", 48);
        int cancelSlot = plugin.getConfig().getInt("sell-gui.cancel-button.slot", 50);
        
        // If clicking in sell slots, allow normal interaction
        if (sellSlots.contains(slot)) {
            // Allow placing/removing items in sell slots
            Bukkit.getScheduler().runTaskLater(plugin, this::updateConfirmButton, 1L);
            return;
        }
        
        // Cancel all other clicks in GUI
        e.setCancelled(true);
        
        // Confirm button
        if (slot == confirmSlot) {
            confirmSale(p);
            return;
        }
        
        // Cancel button
        if (slot == cancelSlot) {
            cancelSale(p);
            return;
        }
    }
    
    private void confirmSale(Player p) {
        double totalValue = calculateTotalValue();
        
        if (totalValue == 0) {
            p.sendMessage(plugin.getMessage("sell-gui-empty"));
            return;
        }
        
        // Remove items from sell GUI and add money
        for (int slot : sellSlots) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                double price = plugin.getItemManager().getSellPrice(item);
                if (price > 0) {
                    inventory.setItem(slot, null);
                }
            }
        }
        
        EconomyUtil.deposit(p, totalValue);
        
        String message = plugin.getMessage("sell-success")
                .replace("{amount}", "all")
                .replace("{item}", "items")
                .replace("{price}", EconomyUtil.format(totalValue));
        p.sendMessage(message);
        
        p.closeInventory();
    }
    
    private void cancelSale(Player p) {
        // Return items to player inventory
        for (int slot : sellSlots) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(item);
                if (!leftover.isEmpty()) {
                    for (ItemStack drop : leftover.values()) {
                        p.getWorld().dropItemNaturally(p.getLocation(), drop);
                    }
                }
                inventory.setItem(slot, null);
            }
        }
        
        p.sendMessage(plugin.getMessage("sell-cancelled"));
        p.closeInventory();
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getInventory().equals(inventory)) return;
        
        Player p = (Player) e.getPlayer();
        
        // Return any items left in the GUI
        for (int slot : sellSlots) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(item);
                if (!leftover.isEmpty()) {
                    for (ItemStack drop : leftover.values()) {
                        p.getWorld().dropItemNaturally(p.getLocation(), drop);
                    }
                }
            }
        }
        
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }
}
