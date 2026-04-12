package me.bradenk.customItems.gui;

import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ItemGUI {

    public void openGUI(Player player, String id) {
        Inventory gui = Bukkit.createInventory(null, 5 * 9,  Component.text("Custom Item: " + id).color(NamedTextColor.YELLOW));

        for (int a = 0; a < 45; a++) {
            gui.setItem(a, createGlass());
        }

        gui.setItem(11, makeButton(Material.NAME_TAG, Component.text("Display Name: ").color(NamedTextColor.GOLD)
                .append(CustomItems.instance.getSession(player.getUniqueId()).getDisplayName().color(NamedTextColor.YELLOW)), List.of(Component.empty()), player));

        gui.setItem(12, makeButton(CustomItems.instance.getSession(player.getUniqueId()).getMaterial(), Component.text("Material: ").color(NamedTextColor.GOLD)
                .append(Component.text(CustomItems.instance.getSession(player.getUniqueId()).getMaterial().getKey().getKey().toUpperCase()).color(NamedTextColor.YELLOW)), List.of(Component.empty()), player));

        gui.setItem(13, makeButton(Material.OAK_SIGN,
                Component.text("Lore: ").color(NamedTextColor.GOLD).append(Component.space()),
                CustomItems.instance.getSession(player.getUniqueId()).getLore()
                , player));

        gui.setItem(14, makeButton(Material.LEVER, Component.text("Unbreakable: ").color(NamedTextColor.GOLD)
                .append(Component.text(CustomItems.instance.getSession(player.getUniqueId()).isUnbreakable()).color(NamedTextColor.YELLOW)), List.of(Component.empty()), player));

        gui.setItem(15, makeButton(Material.ENCHANTED_BOOK, Component.text("Enchantments: ").color(NamedTextColor.GOLD)
                .append(Component.space()), TextUtils.enchantComponents(CustomItems.instance.getSession(player.getUniqueId()).getEnchantments()), player));

        gui.setItem(30, makeButton(Material.DISPENSER, Component.text("Give Item").color(NamedTextColor.GOLD), List.of(Component.empty()), player));

        gui.setItem(32, makeButton(Material.WRITABLE_BOOK, Component.text("Save Item").color(NamedTextColor.GOLD), List.of(Component.empty()), player));


        player.openInventory(gui);
    }

    private ItemStack makeButton(Material material, Component name, List<Component> lore, Player player) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(name.decoration(TextDecoration.ITALIC, false));
            List<Component> finalLore = new ArrayList<>();
            switch (material) {
                case OAK_SIGN:
                    for (Component component : lore) {
                        finalLore.add(Component.text("- ", NamedTextColor.YELLOW).append(component));
                    }
                    break;
                case ENCHANTED_BOOK:
                    ConcurrentHashMap<Enchantment, Integer> enchantments = CustomItems.instance.getSession(player.getUniqueId()).getEnchantments();
                    for (Enchantment enchantment : CustomItems.instance.getSession(player.getUniqueId()).getEnchantments().keySet()) {
                        finalLore.add(Component.text("- ", NamedTextColor.YELLOW).append(
                                Component.text(enchantment.getKey().getKey())).append(
                                        Component.space())
                                .append(Component.text(enchantments.get(enchantment))).color(NamedTextColor.YELLOW));
                    }
            }
            meta.lore(finalLore);
            item.setItemMeta(meta);
            item.setAmount(1);
            item.addItemFlags(ItemFlag.values());
        }
        return item;
    }

    private ItemStack createGlass() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        assert meta != null;
        meta.displayName(Component.text(" "));
        glass.setItemMeta(meta);
        return glass;
    }
}

