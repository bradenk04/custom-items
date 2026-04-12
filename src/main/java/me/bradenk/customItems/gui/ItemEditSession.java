package me.bradenk.customItems.gui;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.config.ConfigLoader;
import me.bradenk.customItems.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ItemEditSession {
    private final String id;
    private Component displayName;
    private Material material;
    private ConcurrentHashMap<Enchantment, Integer> enchantments;
    private List<Component> lore;
    private List<Float> customModelData;
    private boolean unbreakable;

    public ItemEditSession(
            String id,
            Component name,
            Material material,
            int amount,
            ConcurrentHashMap<Enchantment, Integer> enchantments,
            List<Component> lore,
            List<Float> cmd,
            boolean unbreakable
    ) {
        this.id = id;
        this.displayName = name == null ? Component.text("None") : name;
        this.material = material == null ? Material.STONE : material;
        this.enchantments = enchantments == null ? new ConcurrentHashMap<>() : enchantments;
        this.lore = lore == null ? new ArrayList<>() : lore;
        this.unbreakable = unbreakable;
        this.customModelData = cmd == null ? new ArrayList<>() : cmd;
    }

    public ItemEditSession(CustomItem item) {
        this.id = item.getId();
        this.displayName = item.getName();
        this.material = item.getMaterial();
        this.enchantments = item.getEnchantments();
        this.lore = item.getLore();
        this.unbreakable = item.isUnbreakable();
        this.customModelData = item.getCustomModelData();
    }

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public ConcurrentHashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(ConcurrentHashMap<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public List<Component> getLore() {
        return lore;
    }

    public void setLore(List<Component> lore) {
        this.lore = lore;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public CustomItem toCustomItem() throws IOException {
        File configFile = new File(CustomItems.instance.getDataFolder(), "items/" + id + ".toml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }
        CommentedFileConfig config = CommentedFileConfig.of(configFile);
        CustomItem item = new CustomItem(config, id, displayName, material, enchantments, lore, customModelData, unbreakable);
        item.save();
        ConfigLoader.customItems.put(id, item);
        return item;
    }
}

