package me.bradenk.customItems.command;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command("item edit")
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
        meta.lore();
        item.setItemMeta(meta);
        player.getInventory().setItemInMainHand(item);
    }
}
