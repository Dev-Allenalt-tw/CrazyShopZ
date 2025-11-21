package me.devallenalt_tw.crazyshopz;

import me.devallenalt_tw.crazyshopz.commands.ShopCommand;
import me.devallenalt_tw.crazyshopz.commands.SellGUICommand;
import me.devallenalt_tw.crazyshopz.managers.CategoryManager;
import me.devallenalt_tw.crazyshopz.managers.GUIManager;
import me.devallenalt_tw.crazyshopz.managers.ItemManager;
import me.devallenalt_tw.crazyshopz.placeholders.CrazyShopZExpansion;
import me.devallenalt_tw.crazyshopz.utils.Color;
import me.devallenalt_tw.crazyshopz.utils.ConfigUtil;
import me.devallenalt_tw.crazyshopz.utils.EconomyUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CrazyShopZ extends JavaPlugin {
    
    private static CrazyShopZ instance;
    private Economy economy;
    private CategoryManager categoryManager;
    private ItemManager itemManager;
    private GUIManager guiManager;
    private ConfigUtil configUtil;
    
    private FileConfiguration messagesConfig;
    private FileConfiguration categoriesConfig;
    private FileConfiguration playerDataConfig;
    private File playerDataFile;
    
    private Map<UUID, Double> playerSpent = new HashMap<>();
    private Map<UUID, Double> playerEarned = new HashMap<>();
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default configs
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("categories.yml", false);
        
        // Load configurations
        loadConfigurations();
        
        // Setup Vault economy
        if (!setupEconomy()) {
            getLogger().severe("Vault not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Initialize managers
        configUtil = new ConfigUtil(this);
        categoryManager = new CategoryManager(this);
        itemManager = new ItemManager(this);
        guiManager = new GUIManager(this);
        
        // Load categories and items
        categoryManager.loadCategories();
        
        // Register commands
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("sellgui").setExecutor(new SellGUICommand(this));
        
        // Register PlaceholderAPI expansion
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new CrazyShopZExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered!");
        }
        
        // Load player data
        loadPlayerData();
        
        getLogger().info("CrazyShopZ v1.0.0 has been enabled!");
    }
    
    @Override
    public void onDisable() {
        savePlayerData();
        getLogger().info("CrazyShopZ has been disabled!");
    }
    
    private void loadConfigurations() {
        // Load messages.yml
        File messagesFile = new File(getDataFolder(), "messages.yml");
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        // Load categories.yml
        File categoriesFile = new File(getDataFolder(), "categories.yml");
        categoriesConfig = YamlConfiguration.loadConfiguration(categoriesFile);
        
        // Load playerdata.yml
        playerDataFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
    }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
    
    private void loadPlayerData() {
        if (playerDataConfig.contains("players")) {
            for (String uuidStr : playerDataConfig.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                double spent = playerDataConfig.getDouble("players." + uuidStr + ".spent", 0.0);
                double earned = playerDataConfig.getDouble("players." + uuidStr + ".earned", 0.0);
                playerSpent.put(uuid, spent);
                playerEarned.put(uuid, earned);
            }
        }
    }
    
    private void savePlayerData() {
        try {
            for (Map.Entry<UUID, Double> entry : playerSpent.entrySet()) {
                playerDataConfig.set("players." + entry.getKey().toString() + ".spent", entry.getValue());
            }
            for (Map.Entry<UUID, Double> entry : playerEarned.entrySet()) {
                playerDataConfig.set("players." + entry.getKey().toString() + ".earned", entry.getValue());
            }
            playerDataConfig.save(playerDataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addSpent(UUID uuid, double amount) {
        playerSpent.put(uuid, playerSpent.getOrDefault(uuid, 0.0) + amount);
    }
    
    public void addEarned(UUID uuid, double amount) {
        playerEarned.put(uuid, playerEarned.getOrDefault(uuid, 0.0) + amount);
    }
    
    public double getSpent(UUID uuid) {
        return playerSpent.getOrDefault(uuid, 0.0);
    }
    
    public double getEarned(UUID uuid) {
        return playerEarned.getOrDefault(uuid, 0.0);
    }
    
    public String getMessage(String path) {
        String message = messagesConfig.getString(path, "&cMessage not found: " + path);
        return Color.translate(getConfig().getString("prefix", "") + " " + message);
    }
    
    public String getMessageNoPrefix(String path) {
        String message = messagesConfig.getString(path, "&cMessage not found: " + path);
        return Color.translate(message);
    }
    
    public static CrazyShopZ getInstance() {
        return instance;
    }
    
    public Economy getEconomy() {
        return economy;
    }
    
    public CategoryManager getCategoryManager() {
        return categoryManager;
    }
    
    public ItemManager getItemManager() {
        return itemManager;
    }
    
    public GUIManager getGuiManager() {
        return guiManager;
    }
    
    public ConfigUtil getConfigUtil() {
        return configUtil;
    }
    
    public FileConfiguration getCategoriesConfig() {
        return categoriesConfig;
    }
    
    public void reloadConfigs() {
        reloadConfig();
        loadConfigurations();
        categoryManager.loadCategories();
    }
}
