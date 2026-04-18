package me.bradenk.customItems.command;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.gui.ItemEditSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Command("item edit")
@CommandPermission("customitems.item.edit")
public class ItemEditCommand {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Subcommand({"name", "rename"})
    public void editName(BukkitCommandActor actor, String name) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item to edit.", NamedTextColor.RED));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        Component itemName = miniMessage.deserialize(name).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        meta.displayName(itemName);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);

        player.sendMessage(
                Component.text("Item name set to: ", NamedTextColor.WHITE).append(itemName)
        );
    }

    @Subcommand("lore clear")
    public void clearLore(BukkitCommandActor actor) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item to edit.", NamedTextColor.RED));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.lore(new ArrayList<>());
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);

        player.sendMessage(
                Component.text("Lore has been cleared.", NamedTextColor.GREEN)
        );
    }

    @Subcommand("lore line add")
    public void addLoreLine(BukkitCommandActor actor, String line) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item to edit.", NamedTextColor.RED));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();

        Component newLine = miniMessage.deserialize(line).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);

        lore.add(newLine);
        meta.lore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);

        player.sendMessage(
                Component.text("Lore has been updated.", NamedTextColor.GREEN)
        );
    }

    @Subcommand("lore line remove")
    public void removeLoreLine(BukkitCommandActor actor, int line) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item to edit.", NamedTextColor.RED));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null) {
            throw new IllegalStateException("You must be a player to edit an item!");
        }
        lore.remove(line - 1);
        meta.lore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(
                Component.text("Lore has been removed.", NamedTextColor.GREEN)
        );
    }

    @Subcommand("lore line set")
    public void setLoreLine(BukkitCommandActor actor, int line, String content) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item to edit.", NamedTextColor.RED));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = meta.lore();
        if (lore == null) {
            throw new IllegalStateException("You must be a player to edit an item!");
        }
        Component newLine = miniMessage.deserialize(content).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
        lore.set(line - 1, newLine);
        meta.lore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
        player.sendMessage(
                Component.text("Lore has been updated.", NamedTextColor.GREEN)
        );
    }

    @Subcommand("enchant")
    public void editEnchant(BukkitCommandActor actor, Enchantment enchantment, int level) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item to edit.", NamedTextColor.RED));
            return;
        }
        item.addUnsafeEnchantment(enchantment, level);
        player.getInventory().setItemInMainHand(item);
    }

    @Subcommand("modeldata")
    public void editModelData(BukkitCommandActor actor, Float[] data) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item to edit.", NamedTextColor.RED));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        CustomModelDataComponent modelData = meta.getCustomModelDataComponent();
        modelData.setFloats(Arrays.asList(data));
        meta.setCustomModelDataComponent(modelData);
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
    }
}
