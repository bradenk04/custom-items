package me.bradenk.customItems.command;

import me.bradenk.customItems.config.ConfigLoader;
import me.bradenk.customItems.items.CustomItem;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class CustomItemParameterType implements ParameterType<BukkitCommandActor, CustomItem> {
    @Override
    public CustomItem parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String inputId = input.readString();
        if (ConfigLoader.customItems.containsKey(inputId)) {
            return ConfigLoader.customItems.get(inputId);
        }
        throw new CommandErrorException("No item with id " + inputId + " exists!");
    }
}
