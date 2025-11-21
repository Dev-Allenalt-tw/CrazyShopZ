# CrazyShopZ

[![Build Status](https://github.com/Dev_Allenalt_tw/CrazyShopZ/workflows/Build%20CrazyShopZ/badge.svg)](https://github.com/Dev_Allenalt_tw/CrazyShopZ/actions)

**CrazyShopZ** is an advanced, fully-featured shop plugin for Minecraft 1.21.x servers using Spigot/Paper API.

## Features

- 📦 **Category-Based Shop System** - Organize items into configurable categories
- 💰 **Buy & Sell System** - Players can buy items and sell them back
- 🎨 **Customizable GUIs** - Full control over menu appearance and layout
- 📄 **Pagination Support** - Handle unlimited items with automatic page navigation
- 💳 **Vault Economy Integration** - Works with any Vault-compatible economy plugin
- 🔒 **LuckPerms Integration** - Fine-grained permission control
- 📊 **PlaceholderAPI Support** - Track player spending and earnings
- ⚙️ **YAML Configuration** - Easy to configure categories, items, and prices
- 🔄 **Sell GUI** - ShopGUIPlus-style interface for selling items

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/shop` | Opens the main shop menu | `crazyshopz.use` |
| `/shop <category>` | Opens a specific category | `crazyshopz.use` |
| `/sellgui` | Opens the sell GUI | `crazyshopz.sell` |

## Permissions

- `crazyshopz.use` - Access to shop command (default: true)
- `crazyshopz.sell` - Access to sell GUI (default: true)
- `crazyshopz.admin` - Admin permissions (default: op)

## PlaceholderAPI Placeholders

- `%crazyshopz_prefix%` - Plugin prefix
- `%crazyshopz_spent%` - Total money spent by player
- `%crazyshopz_earned%` - Total money earned by player

## Dependencies

**Required:**
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [Vault](https://www.spigotmc.org/resources/vault.34315/)
- [LuckPerms](https://luckperms.net/)

## Installation

1. Download the latest release from [Releases](https://github.com/Dev_Allenalt_tw/CrazyShopZ/releases)
2. Place the JAR file in your server's `plugins` folder
3. Install required dependencies (PlaceholderAPI, Vault, LuckPerms)
4. Restart your server
5. Configure `config.yml` and `categories.yml` to your liking
6. Reload with `/plugman reload CrazyShopZ` or restart the server

## Configuration

### config.yml
Contains general settings, GUI item configurations, menu settings, and economy options.

### categories.yml
Define your shop categories and items here. Each category can have:
- Custom icon and display name
- Custom lore
- Slot position in main menu
- List of items with buy/sell prices

### messages.yml
Customize all plugin messages and notifications.

## Building from Source

```bash
git clone https://github.com/Dev_Allenalt_tw/CrazyShopZ.git
cd CrazyShopZ
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## Support

For issues, feature requests, or questions:
- Open an issue on [GitHub](https://github.com/Dev_Allenalt_tw/CrazyShopZ/issues)
- Join our Discord: [Coming Soon]

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

**Author:** Dev_Allenalt_tw  
**Version:** 1.0.0  
**Minecraft Version:** 1.21.x

---

Made with ❤️ for the Minecraft community
