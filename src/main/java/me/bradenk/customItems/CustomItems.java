package me.bradenk.customItems;

import me.bradenk.customItems.command.ItemEditCommand;
import me.bradenk.customItems.gui.ItemEditSession;
import me.bradenk.customItems.gui.ItemGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CustomItems extends JavaPlugin {
    public static CustomItems instance;
    private ConcurrentHashMap<UUID, ItemEditSession> sessions = new ConcurrentHashMap<>();
    private ItemGUI itemGUI;

    @Override
    public void onEnable() {
        instance = this;
        itemGUI = new ItemGUI();
        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).build();
        lamp.register(new ItemEditCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConcurrentHashMap<UUID, ItemEditSession> getSessions() {
        return sessions;
    }

    public static Component colorCode(String value) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(value);
    }

    public ItemGUI getItemGUI() {
        return itemGUI;
    }
}
