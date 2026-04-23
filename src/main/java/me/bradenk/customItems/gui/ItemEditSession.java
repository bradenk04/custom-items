package me.bradenk.customItems.gui;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.abilities.AbilityDefinition;
import me.bradenk.customItems.config.ConfigLoader;
import me.bradenk.customItems.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private List<AbilityDefinition> abilities;

    public ItemEditSession(
            String id,
            Component name,
            Material material,
            int amount,
            ConcurrentHashMap<Enchantment, Integer> enchantments,
            List<Component> lore,
            List<Float> cmd,
            boolean unbreakable,
            List<AbilityDefinition> abilities
    ) {
        this.id = id;
        this.displayName = name == null ? Component.text("None") : name;
        this.material = material == null ? Material.STONE : material;
        this.enchantments = enchantments == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(enchantments);
        boolean useVanilla = ConfigLoader.getMainConfig()
                .get("use_vanilla_enchants_in_lore") instanceof Boolean b && b;

        this.lore = useVanilla
                ? new ArrayList<>(lore)
                : stripGeneratedEnchantLore(lore);
        this.unbreakable = unbreakable;
        this.customModelData = cmd == null ? new ArrayList<>() : new ArrayList<>(cmd);
    }

    public ItemEditSession(CustomItem item) {
        this.id = item.getId();
        this.displayName = item.getName();
        this.material = item.getMaterial();
        this.enchantments = item.getEnchantments();

        boolean useVanilla = ConfigLoader.getMainConfig()
                .get("use_vanilla_enchants_in_lore") instanceof Boolean b && b;

        this.lore = useVanilla
                ? new ArrayList<>(item.getLore())
                : stripGeneratedEnchantLore(item.getLore());

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
        CustomItem item = new CustomItem(config, id, displayName, material, enchantments, lore, customModelData, unbreakable, abilities);
        item.save();
        ConfigLoader.customItems.put(id, item);
        return item;
    }


    private List<Component> stripGeneratedEnchantLore(List<Component> originalLore) {
        if (originalLore == null || originalLore.isEmpty()) {
            return new ArrayList<>();
        }

        List<Component> cleaned = new ArrayList<>();

        for (Component component : originalLore) {
            String plain = PlainTextComponentSerializer.plainText().serialize(component);

            if (plain.contains("ᶦ")) {
                break;
            }

            cleaned.add(component);
        }

        while (!cleaned.isEmpty()
                && PlainTextComponentSerializer.plainText().serialize(cleaned.get(cleaned.size() - 1)).trim().isEmpty()) {
            cleaned.remove(cleaned.size() - 1);
        }

        return cleaned;
    }
}