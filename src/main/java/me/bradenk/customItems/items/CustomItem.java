package me.bradenk.customItems.items;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.bradenk.customItems.CustomItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomItem {
    private CommentedFileConfig config;

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    @NotNull
    private String id;
    @Nullable
    private Component displayName;
    @NotNull
    private Material material;
    @Nullable
    private ConcurrentHashMap<Enchantment, Integer> enchantments;
    @Nullable
    private List<Component> lore;
    @Nullable
    private List<Float> customModelData;
    private boolean unbreakable;

    private CustomItem(
            @NonNull CommentedFileConfig config,
            @NonNull String id,
            @Nullable Component name,
            @NonNull Material material,
            @Nullable ConcurrentHashMap<Enchantment, Integer> enchantments,
            @Nullable List<Component> lore,
            @Nullable List<Float> cmd,
            boolean unbreakable
    ) {
        this.config = config;
        this.id = id;
        this.displayName = name;
        this.material = material;
        this.enchantments = enchantments;
        this.lore = lore;
        this.customModelData = cmd;
        this.unbreakable = unbreakable;
    }

    @Nullable
    public static CustomItem from(CommentedFileConfig config) {

        Optional<HashMap<String, Integer>> enchantmentsRaw = config.getOptional("general.enchantments");
        ConcurrentHashMap<Enchantment, Integer> enchantments = new ConcurrentHashMap<>();
        enchantmentsRaw.ifPresent(stringIntegerHashMap -> stringIntegerHashMap.forEach((enchantName, level) -> {
            NamespacedKey key;
            if (enchantName.contains(":")) {
                String firstPart = enchantName.split(":")[0];
                String secondPart = enchantName.split(":")[1];
                key = new NamespacedKey(firstPart, secondPart);
            } else {
                key = NamespacedKey.minecraft(enchantName);
            }
            Enchantment enchant = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
            if (enchant == null) {
                CustomItems.instance.getLogger().warning("Enchantment " + enchantName + " does not exist!");
                return;
            }
            enchantments.put(enchant, level);
        }));

        Optional<List<String>> loreRaw = config.getOptional("general.lore");
        List<Component> lore = null;
        if (loreRaw.isPresent()) {
            lore = loreRaw.get().stream().map(miniMessage::deserialize).toList();
        }

        String materialRaw = config.get("general.material");
        Material mat = Material.matchMaterial(materialRaw);
        if (mat == null) {
            CustomItems.instance.getLogger().warning("Material " + materialRaw + " does not exist!");
            return null;
        }

        Optional<String> displayNameRaw = config.getOptional("general.display_name");

        Component displayName = null;
        if (displayNameRaw.isPresent()) {
            displayName = miniMessage.deserialize(displayNameRaw.get());
        }

        Optional<List<Float>> customModelDataRaw = config.getOptional("general.custom_model_data");
        List<Float> customModelData = null;
        if (customModelDataRaw.isPresent()) {
            customModelData = customModelDataRaw.get();
        }

        Optional<Boolean> unbreakableRaw = config.getOptional("general.unbreakable");
        boolean unbreakable = false;
        if (unbreakableRaw.isPresent()) {
            unbreakable = unbreakableRaw.get();
        }
        return new CustomItem(
                config,
                config.get("id"),
                displayName,
                mat,
                enchantments,
                lore,
                customModelData,
                unbreakable
        );
    }

    public @NonNull String getId() {
        return id;
    }

    public Component getName() {
        return displayName;
    }

    public void rename(Component component) {
        displayName = component;
        config.set("general.display_name", miniMessage.serialize(displayName));
    }

    public void setLore(Component[] lore) {
        this.lore = Arrays.asList(lore);
        config.set("general.lore", Arrays.stream(lore).map(miniMessage::serialize).toList());
    }

    public Component[] getLore() {
        return lore.toArray(new Component[0]);
    }

    public void addLore(Component... lore) {
        this.lore.addAll(Arrays.asList(lore));
        setLore(this.lore.toArray(new Component[0]));
    }

    public void addLore(Component lore) {
        this.lore.add(lore);
        setLore(this.lore.toArray(new Component[0]));
    }

    public void setCustomModelData(List<Float> data) {
        this.customModelData = data;
        config.set("general.custom_model_data", data);
    }

    public List<Float> getCustomModelData() {
        return customModelData;
    }

    public void setUnbreakable(boolean value) {
        this.unbreakable = value;
        config.set("general.unbreakable", value);
    }
    public boolean isUnbreakable() {
        return unbreakable;
    }

    private HashMap<String, Integer> enchantListToConfigurableList(ConcurrentHashMap<Enchantment, Integer> enchantments) {
        HashMap<String, Integer> configurableEnchantments = new HashMap<>();
        enchantments.forEach((enchant, level) -> {
            configurableEnchantments.put(enchant.getKey().toString(), level);
        });
        return configurableEnchantments;
    }

    public void addEnchant(String enchant, Integer level) {
        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        this.enchantments.put(Objects.requireNonNull(registry.get(NamespacedKey.minecraft(enchant))), level);
        config.set("general.enchantments", enchantListToConfigurableList(this.enchantments));
    }

    public void removeEnchant(String enchant) {
        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        this.enchantments.remove(Objects.requireNonNull(registry.get(NamespacedKey.minecraft(enchant))));
        config.set("general.enchantments", enchantListToConfigurableList(this.enchantments));
    }

    @SuppressWarnings("UnstableApiUsage")
    public ItemStack createItem() {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(unbreakable);
        if (displayName != null) {
            meta.displayName(displayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }
        if (lore != null) {
            List<Component> finalLore = lore.stream().map(component -> component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList();
            if (enchantments != null && !enchantments.isEmpty()) {
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
            meta.lore(finalLore);
        }
        if (customModelData != null) {
            CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            cmd.setFloats(customModelData);
            meta.setCustomModelDataComponent(cmd);
        }
        meta.addItemFlags(ItemFlag.values());
        if (enchantments != null) {
            enchantments.forEach(item::addUnsafeEnchantment);
        }
        item.setItemMeta(meta);
        return item;
    }
}
