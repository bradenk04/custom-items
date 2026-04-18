package me.bradenk.customItems;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.bradenk.customItems.command.*;
import me.bradenk.customItems.config.ConfigLoader;
import me.bradenk.customItems.gui.ItemEditSession;
import me.bradenk.customItems.gui.ItemGUI;
import me.bradenk.customItems.items.CustomItem;
import me.bradenk.customItems.listeners.ClickListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CustomItems extends JavaPlugin {
    public static CustomItems instance;
    private ConcurrentHashMap<UUID, ItemEditSession> session = new ConcurrentHashMap<>();
    private ItemGUI itemGUI;
    private final int  bstatsPluginId = 30837;

    @Override
    public void onEnable() {
        instance = this;
        Metrics metrics = new Metrics(this, bstatsPluginId);

        ConfigLoader.load();

        Lamp<BukkitCommandActor> lamp = BukkitLamp
                .builder(this)
                .parameterTypes(builder -> {
                    builder.addParameterType(CustomItem.class, new CustomItemParameterType());
                    builder.addParameterType(Enchantment.class, new EnchantParameterType());
                })
                .suggestionProviders(providers -> {
                    providers.addProvider(CustomItem.class, context -> Collections.list(ConfigLoader.customItems.keys()));
                    List<Enchantment> list = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).stream().toList();
                    providers.addProvider(Enchantment.class, context -> list.stream().map(i -> i.getKey().asString()).toList());
                })
                .build();
        lamp.register(new ItemEditCommand());
        lamp.register(new ItemGiveCommand());
        lamp.register(new ItemEditorCommand());
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
