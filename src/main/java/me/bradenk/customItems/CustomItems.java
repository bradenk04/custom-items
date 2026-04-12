package me.bradenk.customItems;

import me.bradenk.customItems.command.CustomItemParameterType;
import me.bradenk.customItems.command.ItemEditCommand;
import me.bradenk.customItems.command.ItemGiveCommand;
import me.bradenk.customItems.config.ConfigLoader;
import me.bradenk.customItems.gui.ItemEditSession;
import me.bradenk.customItems.gui.ItemGUI;
import me.bradenk.customItems.items.CustomItem;
import me.bradenk.customItems.listeners.ClickListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CustomItems extends JavaPlugin {
    public static CustomItems instance;
    private ConcurrentHashMap<UUID, ItemEditSession> session = new ConcurrentHashMap<>();
    private ItemGUI itemGUI;

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
        getServer().getPluginManager().registerEvents(new ClickListener(), this);
        itemGUI = new ItemGUI();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ItemEditSession getSession(UUID uuid) {
        return session.get(uuid);
    }

    public ConcurrentHashMap<UUID, ItemEditSession> getSession() {
        return session;
    }

    public ItemGUI getItemGUI() {
        return itemGUI;
    }

    public static Component colorCode(String value) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(value);
    }

}
