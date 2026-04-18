
<div align="center" id="user-content-toc">
    <ul><summary><h1>Custom Items</h1></summary></ul>
    <h3>The all-in-one custom item editor for Minecraft servers</h3>
    <p>
        Rename, relore, enchant, and fully customize any items without leaving the game.
    </p>
    <div>
        <a href="https://bstats.org/plugin/bukkit/Claims/30837" target="_blank" rel="noopener noreferrer"><img alt="bStats Servers" src="https://img.shields.io/bstats/servers/29288?style=for-the-badge&link=https%3A%2F%2Fbstats.org%2Fplugin%2Fbukkit%2FClaims%2F29288"></a>
        <a href="https://bstats.org/plugin/bukkit/Claims/30837" target="_blank" rel="noopener noreferrer"><img alt="bStats Players" src="https://img.shields.io/bstats/players/29288?style=for-the-badge&link=https%3A%2F%2Fbstats.org%2Fplugin%2Fbukkit%2FClaims%2F29288"></a>
        <a href="https://discord.gg/MRB9kvSuhW" target="_blank" rel="noopener noreferrer"><img alt="Discord" src="https://img.shields.io/discord/1467775220260798559?style=for-the-badge"></a>
        <a href="https://modrinth.com/plugin/braden-custom-items" target="_blank" rel="noopener noreferrer"><img alt="Downloads" src="https://img.shields.io/endpoint?url=https%3A%2F%2Fdownload-aggregate.bradenk04.workers.dev%2F%3Fgh%3Dbradenk04%2Fclaims%26mr%3Dbraden-claims%26hg%3Dbraden-claims&style=for-the-badge"></a>
    </div>
</div>

---

## 🧰 What is Custom Items?
Custom Items is a lightweight item customization plugin for Paper 26.1+ servers. Whether you have a huge RPG world with hundreds of items, a small survival SMP, or a creative server, Custom Items gives you the power to fully personalize every item in you server without tabbing out of the game.

No complicated setup. No dependencies. Just drag and drop

---

## ✨ Features

| Feature                   | Description                                                                      |
|---------------------------|----------------------------------------------------------------------------------|
| 🏷️ **Custom Names**      | Rename any item with full MiniMessage support.                                   |
| 📃 **Custom Lore**        | Add, remove, edit, or clear multi-line lore with in depth MiniMessage support.   |
| 🧙 **Enchantments**       | Apply any item with any level to your items.                                     |
| 🖼️ **Custom Model Data** | Full custom model data support allows you to use custom textures for your items. |
| 🖥️ **GUI Editor**        | Fully edit your items without leaving the game.                                  |
| ⚙️ **Easy Configuration** | Easily edit TOML configs to customize every part of the plugin.                  |
| 📦 **No Dependencies**    | No need to hunt down external dependencies, there are none!                      |

---

## Commands
| Command                                     | Description                                                        | Example                                          |
|---------------------------------------------|--------------------------------------------------------------------|--------------------------------------------------|
| `/item create <id>`                         | Creates a custom item and saves it to a config then opens the GUI  | `/item create legendary_sword`                   |
| `/item edit <id>`                           | Opens the Custom Item editor. All changes are saved to the config. | `/item edit legendary_sword`                     |
| `/item edit rename`                         | Sets a custom name to the item in your hand.                       | `/item edit rename <gold>Legendary Sword`        |
| `/item edit lore clear`                     | Clears an items lore completely                                    | `/item edit lore clear`                          |
| `/item edit lore line add <content>`        | Adds a line to an items lore                                       | `/item edit lore line add <red>Very Dangerous`   |
| `/item edit lore line set <line> <content>` | Sets a line on ann items lore                                      | `/item edit lore line set 1 <red>Very Dangerous` |
| `/item edit lore line remove <line>`        | Removes a line from an items lore                                  | `/item edit lore line remove 1`                  |
| `/item edit enchant <enchant>`              | Enchants an item with an enchantment                               | `/item edit enchant protection 3`                |
| `/item edit modeldata <data>`               | Sets a custom model data to the item in your hand.                 | `/item edit modeldata 12345`                     |

---

## Permissions
| Permission              | Description                                    |
|-------------------------|------------------------------------------------|
| `customitems.item.edit` | Allows you to edit items that are config-bound |
| `customitems.item.give` | Allows you to give custom items to players.    |

---

## 🚀 Installation

1. **Download** the latest `.jar`
2. **Drop it** into your server's `/plugins` folder
3. **Start/Restart** your server
4. **Enjoy!**

---

## 🐛 Issues & Suggestions
Found a bug? Have a feature rquest? We'd love to hear from you!

- [Submit an issue](https://github.com/bradenk04/custom-items/issues)
- [Request a feature](https://github.com/bradenk04/custom-items/discussions)
- Or join our [Discord](https://discord.gg/MRB9kvSuhW) for faster support and community discussion