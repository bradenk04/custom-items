package me.bradenk.customItems.gui;

import me.bradenk.customItems.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ItemEditSession {
    private Component displayName;
    private Material material;
    private ConcurrentHashMap<Enchantment, Integer> enchantments;
    private List<Component> lore;
    private List<Float> customModelData;
    private boolean unbreakable;

    public ItemEditSession(Component name,
                           Material material,
                           int amount,
                           ConcurrentHashMap<Enchantment, Integer> enchantments,
                           List<Component> lore,
                           List<Float> cmd,
                           boolean unbreakable) {
        this.displayName = name == null ? Component.text("None") : name;
        this.material = material == null ? Material.STONE : material;
        this.enchantments = enchantments == null ? new ConcurrentHashMap<>() : enchantments;
        this.lore = lore == null ? new ArrayList<>() : lore;
        this.unbreakable = unbreakable;
        this.customModelData = cmd == null ? new ArrayList<>() : cmd;
    }

    public ItemEditSession(CustomItem item) {
        this.displayName = item.getName();
        this.material = item.getMaterial();
        this.enchantments = new ConcurrentHashMap<>(item.getEnchantments());
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

    public CustomItem toCustomItem() {
        return new CustomItem(displayName, material, enchantments, lore, customModelData, unbreakable);
    }
}

