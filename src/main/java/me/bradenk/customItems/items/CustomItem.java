package me.bradenk.customItems.items;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.bradenk.customItems.CustomItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomItem {

    private Component displayName;
    private Material material;
    private ConcurrentHashMap<Enchantment, Integer> enchantments;
    private List<Component> lore;
    private List<Float> customModelData;
    private boolean unbreakable;

    public CustomItem(Component name,
                      Material material,
                      ConcurrentHashMap<Enchantment, Integer> enchantments,
                      List<Component> lore,
                      List<Float> cmd,
                      boolean unbreakable
    ) {
        this.displayName = name;
        this.material = material;
        this.enchantments = enchantments;
        this.lore = lore;
        this.unbreakable = unbreakable;
        this.customModelData = cmd;
    }

    public Component getName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public void rename(Component component) {
        displayName = component;
    }

    public void setLore(List<Component> lore) {
        this.lore = lore;
    }

    public List<Component> getLore() {
        return lore;
    }

    public void addLore(Component... lore) {
        this.lore.addAll(Arrays.asList(lore));
    }

    public void addLore(Component lore) {
        this.lore.add(lore);
    }

    public void setCustomModelData(List<Float> data) {
        this.customModelData = data;
    }

    public List<Float> getCustomModelData() {
        return customModelData;
    }

    public void setUnbreakable(boolean value) {
        this.unbreakable = value;
    }
    public boolean isUnbreakable() {
        return unbreakable;
    }

    public void addEnchant(String enchant, Integer level) {
        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        enchantments.put(Objects.requireNonNull(registry.get(NamespacedKey.minecraft(enchant))), level);
    }

    public void removeEnchant(String enchant) {
        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        enchantments.remove(Objects.requireNonNull(registry.get(NamespacedKey.minecraft(enchant))));
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(unbreakable);
        meta.displayName(displayName.append(Component.space()).append(Component.text("(").color(NamedTextColor.GRAY)));
        List<Component> finalLore = new ArrayList<>(lore);
        if (!enchantments.isEmpty()) {
            finalLore.add(Component.text(" "));
            List<String> enchantList = new ArrayList<>();
            enchantments.forEach((enchant, level) -> {
                String enchantName = enchant.getKey().getKey().toUpperCase().replace('_', ' ') + " " + level;
                enchantList.add(enchantName);
            });
            StringBuilder lineBuilder = new StringBuilder();
            int count = 0;
            for (int i = 0; i < enchantList.size(); i++) {
                String enchantStr = enchantList.get(i);
                if (count > 0) {
                    lineBuilder.append(", ");
                }
                lineBuilder.append(enchantStr);
                count++;
                boolean nextWouldOverflow = (i + 1 < enchantList.size() &&
                        lineBuilder.length() + 2 + enchantList.get(i + 1).length() > 35);
                boolean reachedEnchantLimit = count >= 3;
                if (nextWouldOverflow || reachedEnchantLimit || i == enchantList.size() - 1) {
                    finalLore.add(Component.text(lineBuilder.toString()).color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false));
                    lineBuilder.setLength(0);
                    count = 0;
                }
            }
        }
        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setFloats(customModelData);
        meta.setCustomModelDataComponent(cmd);
        meta.lore(finalLore);
        meta.addItemFlags(ItemFlag.values());
        enchantments.forEach(item::addUnsafeEnchantment);
        item.setItemMeta(meta);
        return item;
    }
}
