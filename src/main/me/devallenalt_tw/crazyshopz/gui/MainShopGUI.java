package me.devallenalt_tw.crazyshopz.gui;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import me.devallenalt_tw.crazyshopz.models.ShopCategory;
import me.devallenalt_tw.crazyshopz.utils.Color;
import me.devallenalt_tw.crazyshopz.utils.EconomyUtil;
import me.devallenalt_tw.crazyshopz.utils.GUIBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MainShopGUI implements Listener {
    
    private final CrazyShopZ plugin;
    private final Player player;
    private final Inventory inventory;
    
    public MainShopGUI(CrazyShopZ plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        
        String title = plugin.getConfigUtil().getMenuTitle("main-shop");
        int size = plugin.getConfigUtil().getMenuSize("main-shop");
        this.inventory = Bukkit.createInventory(null, size, title);
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupInventory();
    }
    
    private void setupInventory() {
        // Fill with filler items
        ItemStack filler = plugin.getConfigUtil().getFillerItem();
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }
        
        // Add info button
        ItemStack info = plugin.getConfigUtil().getGuiItem("info");
        if (info != null) {
            List<String> lore = new ArrayList<>();
            if (info.getItemMeta() != null && info.getItemMeta().getLore() != null) {
                for (String line : info.getItemMeta().getLore()) {
                    lore.add(line.replace("{categories}", String.valueOf(plugin.getCategoryManager().getAllCategories().size())));
                }
            }
            ItemStack updatedInfo = GUIBuilder.createItem(info.getType(), info.getItemMeta().getDisplayName(), lore);
            inventory.setItem(plugin.getConfigUtil().getGuiSlot("info"), updatedInfo);
        }
        
        // Add balance button
        ItemStack balance = plugin.getConfigUtil().getGuiItem("balance");
        if (balance != null) {
            List<String> lore = new ArrayList<>();
            if (balance.getItemMeta() != null && balance.getItemMeta().getLore() != null) {
                for (String line : balance.getItemMeta().getLore()) {
                    String replaced = line
                            .replace("{balance}", EconomyUtil.formatNoSymbol(EconomyUtil.getBalance(player)))
                            .replace("{spent}", EconomyUtil.formatNoSymbol(plugin.getSpent(player.getUniqueId())))
                            .replace("{earned}", EconomyUtil.formatNoSymbol(plugin.getEarned(player.getUniqueId())));
                    lore.add(replaced);
                }
            }
            ItemStack updatedBalance = GUIBuilder.createItem(balance.getType(), balance.getItemMeta().getDisplayName(), lore);
            inventory.setItem(plugin.getConfigUtil().getGuiSlot("balance"), updatedBalance);
        }
        
        // Add search button
        ItemStack search = plugin.getConfigUtil().getGuiItem("search");
        if (search != null) {
            inventory.setItem(plugin.getConfigUtil().getGuiSlot("search"), search);
        }
        
        // Add close button
        ItemStack close = plugin.getConfigUtil().getGuiItem("close-menu");
        if (close != null) {
            inventory.setItem(plugin.getConfigUtil().getGuiSlot("close-menu"), close);
        }
        
        // Add categories
        for (ShopCategory category : plugin.getCategoryManager().getAllCategories()) {
            ItemStack categoryItem = GUIBuilder.createItem(
                    category.getIcon(),
                    category.getName(),
                    category.getLore()
            );
            inventory.setItem(category.getSlot(), categoryItem);
        }
    }
    
    public void open() {
        player.openInventory(inventory);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(inventory)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) return;
        
        int slot = e.getSlot();
        
        // Close button
        if (slot == plugin.getConfigUtil().getGuiSlot("close-menu")) {
            p.closeInventory();
            return;
        }
        
        // Check if clicked on a category
        for (ShopCategory category : plugin.getCategoryManager().getAllCategories()) {
            if (slot == category.getSlot()) {
                plugin.getGuiManager().resetPage(p);
                plugin.getGuiManager().setCurrentCategory(p, category.getId());
                new CategoryGUI(plugin, p, category, 0).open();
                return;
            }
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) {
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);
        }
    }
}
