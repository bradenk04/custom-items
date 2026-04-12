package me.bradenk.customItems;

import me.bradenk.customItems.command.CustomItemParameterType;
import me.bradenk.customItems.command.ItemEditCommand;
import me.bradenk.customItems.command.ItemGiveCommand;
import me.bradenk.customItems.config.ConfigLoader;
import me.bradenk.customItems.items.CustomItem;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.Collections;

public final class CustomItems extends JavaPlugin {
    public static CustomItems instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigLoader.load();

        Lamp<BukkitCommandActor> lamp = BukkitLamp
                .builder(this)
                .parameterTypes(builder -> {
                    builder.addParameterType(CustomItem.class, new CustomItemParameterType());
                })
                .suggestionProviders(providers -> {
                    providers.addProvider(CustomItem.class, context -> Collections.list(ConfigLoader.customItems.keys()));
                })
                .build();
        lamp.register(new ItemEditCommand());
        lamp.register(new ItemGiveCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
