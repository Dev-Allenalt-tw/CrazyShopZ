package me.devallenalt_tw.crazyshopz.managers;

import me.devallenalt_tw.crazyshopz.CrazyShopZ;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {
    
    private final CrazyShopZ plugin;
    private final Map<UUID, Integer> currentPage;
    private final Map<UUID, String> currentCategory;
    
    public GUIManager(CrazyShopZ plugin) {
        this.plugin = plugin;
        this.currentPage = new HashMap<>();
        this.currentCategory = new HashMap<>();
    }
    
    public int getCurrentPage(Player player) {
        return currentPage.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void setCurrentPage(Player player, int page) {
        currentPage.put(player.getUniqueId(), page);
    }
    
    public void nextPage(Player player) {
        int page = getCurrentPage(player);
        currentPage.put(player.getUniqueId(), page + 1);
    }
    
    public void previousPage(Player player) {
        int page = getCurrentPage(player);
        if (page > 0) {
            currentPage.put(player.getUniqueId(), page - 1);
        }
    }
    
    public void resetPage(Player player) {
        currentPage.put(player.getUniqueId(), 0);
    }
    
    public String getCurrentCategory(Player player) {
        return currentCategory.get(player.getUniqueId());
    }
    
    public void setCurrentCategory(Player player, String category) {
        currentCategory.put(player.getUniqueId(), category);
    }
    
    public void clearPlayerData(Player player) {
        currentPage.remove(player.getUniqueId());
        currentCategory.remove(player.getUniqueId());
    }
}
