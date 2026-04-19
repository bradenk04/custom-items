package me.bradenk.customItems.abilities;

import me.bradenk.customItems.items.CustomItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record AbilityContext(
        Player player,
        ItemStack itemStack,
        CustomItem customItem,
        Entity target
) {}