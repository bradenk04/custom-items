package me.bradenk.customItems.command;

import me.bradenk.customItems.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public class ItemGiveCommand {
    @Command("item give")
    public void give(BukkitCommandActor actor, CustomItem item) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to give an item!");
            return;
        }
        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }
        ItemStack itemStack = item.createItem();
        player.getInventory().addItem(itemStack);
    }
}
