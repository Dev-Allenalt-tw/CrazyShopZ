package me.devallenalt_tw.crazyshopz.gui;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import me.devallenalt_tw.crazyshopz.models.ShopCategory;
import me.devallenalt_tw.crazyshopz.models.ShopItem;
import me.devallenalt_tw.crazyshopz.utils.Color;
import me.devallenalt_tw.crazyshopz.utils.EconomyUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryGUI implements Listener {
    
    private final CrazyShopZ plugin;
    private final Player player;
    private final ShopCategory category;
    private final int page;
    private final Inventory inventory;
    private final Map<Integer, ShopItem> slotToItem;
    
    private static final int[] ITEM_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    
    private static final int ITEMS_PER_PAGE = ITEM_SLOTS.length;
    
    public CategoryGUI(CrazyShopZ plugin, Player player, ShopCategory category, int page) {
        this.plugin = plugin;
        this.player = player;
        this.category = category;
        this.page = page;
        this.slotToItem = new HashMap<>();
        
        String title = plugin.getConfigUtil().getMenuTitle("category").replace("{category}", category.getName());
        int size = plugin.getConfigUtil().getMenuSize("category");
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
        
        // Calculate pagination
        List<ShopItem> items = category.getItems();
        int totalPages = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());
        
        // Add items for current page
        int slotIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            ShopItem shopItem = items.get(i);
            ItemStack displayItem = plugin.getItemManager().createShopItemStack(shopItem);
            int slot = ITEM_SLOTS[slotIndex];
            inventory.setItem(slot, displayItem);
            slotToItem.put(slot, shopItem);
            slotIndex++;
        }
        
        // Add back button
        ItemStack back = plugin.getConfigUtil().getGuiItem("back");
        if (back != null) {
            inventory.setItem(plugin.getConfigUtil().getGuiSlot("back"), back);
        }
        
        // Add pagination buttons
        if (page > 0) {
            ItemStack previous = plugin.getConfigUtil().getGuiItem("previous-page");
            if (previous != null) {
                inventory.setItem(plugin.getConfigUtil().getGuiSlot("previous-page"), previous);
            }
        }
        
        if (page < totalPages - 1) {
            ItemStack next = plugin.getConfigUtil().getGuiItem("next-page");
            if (next != null) {
                inventory.setItem(plugin.getConfigUtil().getGuiSlot("next-page"), next);
            }
        }
        
        // Add close button
        ItemStack close = plugin.getConfigUtil().getGuiItem("close-menu");
        if (close != null) {
            inventory.setItem(plugin.getConfigUtil().getGuiSlot("close-menu"), close);
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
        ClickType clickType = e.getClick();
        
        // Back button
        if (slot == plugin.getConfigUtil().getGuiSlot("back")) {
            new MainShopGUI(plugin, p).open();
            return;
        }
        
        // Close button
        if (slot == plugin.getConfigUtil().getGuiSlot("close-menu")) {
            p.closeInventory();
            return;
        }
        
        // Previous page
        if (slot == plugin.getConfigUtil().getGuiSlot("previous-page") && page > 0) {
            plugin.getGuiManager().previousPage(p);
            new CategoryGUI(plugin, p, category, page - 1).open();
            return;
        }
        
        // Next page
        if (slot == plugin.getConfigUtil().getGuiSlot("next-page")) {
            int totalPages = (int) Math.ceil((double) category.getItems().size() / ITEMS_PER_PAGE);
            if (page < totalPages - 1) {
                plugin.getGuiManager().nextPage(p);
                new CategoryGUI(plugin, p, category, page + 1).open();
            }
            return;
        }
        
        // Handle item clicks
        ShopItem shopItem = slotToItem.get(slot);
        if (shopItem != null) {
            handleItemClick(p, shopItem, clickType);
        }
    }
    
    private void handleItemClick(Player p, ShopItem shopItem, ClickType clickType) {
        if (clickType == ClickType.LEFT && shopItem.canBuy()) {
            // Buy 1 item
            buyItem(p, shopItem, 1);
        } else if (clickType == ClickType.RIGHT && shopItem.canBuy()) {
            // Buy 64 items
            buyItem(p, shopItem, 64);
        } else if (clickType == ClickType.SHIFT_RIGHT && shopItem.canSell()) {
            // Sell all items
            sellAllItems(p, shopItem);
        }
    }
    
    private void buyItem(Player p, ShopItem shopItem, int amount) {
        double totalPrice = shopItem.getBuyPrice() * amount;
        
        if (!EconomyUtil.hasEnough(p, totalPrice)) {
            p.sendMessage(plugin.getMessage("not-enough-money").replace("{required}", EconomyUtil.format(totalPrice)));
            return;
        }
        
        // Check inventory space
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(plugin.getMessage("inventory-full"));
            return;
        }
        
        // Process purchase
        EconomyUtil.withdraw(p, totalPrice);
        ItemStack item = new ItemStack(shopItem.getMaterial(), amount);
        p.getInventory().addItem(item);
        
        String message = plugin.getMessage("purchase-success")
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", shopItem.getName())
                .replace("{price}", EconomyUtil.format(totalPrice));
        p.sendMessage(message);
        
        // Refresh GUI to update balance if displayed
        new CategoryGUI(plugin, p, category, page).open();
    }
    
    private void sellAllItems(Player p, ShopItem shopItem) {
        int amount = 0;
        
        // Count items in inventory
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == shopItem.getMaterial()) {
                amount += item.getAmount();
            }
        }
        
        if (amount == 0) {
            p.sendMessage(plugin.getMessage("not-enough-items"));
            return;
        }
        
        double totalPrice = shopItem.getSellPrice() * amount;
        
        // Remove items from inventory
        p.getInventory().remove(shopItem.getMaterial());
        
        // Add money
        EconomyUtil.deposit(p, totalPrice);
        
        String message = plugin.getMessage("sell-success")
                .replace("{amount}", String.valueOf(amount))
                .replace("{item}", shopItem.getName())
                .replace("{price}", EconomyUtil.format(totalPrice));
        p.sendMessage(message);
        
        // Refresh GUI
        new CategoryGUI(plugin, p, category, page).open();
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) {
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);
        }
    }
}
