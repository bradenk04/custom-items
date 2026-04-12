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
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Command("item edit")
@CommandPermission("customitems.command.item.edit")
public class ItemEditCommand {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Subcommand("name")
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

    @Subcommand("create <id>")
    public void create(BukkitCommandActor actor, String id) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        if (id == null || !id.matches("^[a-z_]+$")) {
            actor.asPlayer().sendMessage(Component.text("Invalid key. Use a-z and underscores.", NamedTextColor.RED));
            return;
        }
        ItemStack held = player.getInventory().getItemInMainHand();

        Component name = held.effectiveName();
        Material material = held.getType();
        ConcurrentHashMap<Enchantment, Integer> enchants = new ConcurrentHashMap<>(held.getEnchantments());

        List<Component> lore = held.hasItemMeta() && held.getItemMeta().hasLore()
                ? new ArrayList<>(held.getItemMeta().lore())
                : new ArrayList<>();

        List<Float> cmd = held.hasItemMeta() && held.getItemMeta().hasCustomModelDataComponent()
                ? new ArrayList<>(held.getItemMeta().getCustomModelDataComponent().getFloats())
                : new ArrayList<>();

        boolean unbreakable = held.hasItemMeta() && held.getItemMeta().isUnbreakable();

        ItemEditSession session = new ItemEditSession(
                id,
                name,
                material,
                held.getAmount(),
                enchants,
                lore,
                cmd,
                unbreakable
        );

        CustomItems.instance.getSession().put(player.getUniqueId(), session);
        CustomItems.instance.getItemGUI().openGUI(player, id);
    }
}
