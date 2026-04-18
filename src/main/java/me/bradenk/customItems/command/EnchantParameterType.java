package me.bradenk.customItems.command;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class EnchantParameterType implements ParameterType<BukkitCommandActor, Enchantment> {
    @Override
    public Enchantment parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String enchantName = input.readString();
        NamespacedKey key;
        if (enchantName.contains(":")) {
            String firstPart = enchantName.split(":")[0];
            String secondPart = enchantName.split(":")[1];
            key = new NamespacedKey(firstPart, secondPart);
        } else {
            key = NamespacedKey.minecraft(enchantName);
        }
        Enchantment enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
        if (enchant != null) return enchant;

        throw new CommandErrorException("Enchantment " + enchantName + " does not exist!");
    }
}
