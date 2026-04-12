package me.bradenk.customItems;

import me.bradenk.customItems.command.ItemEditCommand;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public final class CustomItems extends JavaPlugin {
    public static CustomItems instance;

    @Override
    public void onEnable() {
        instance = this;
        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).build();
        lamp.register(new ItemEditCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
