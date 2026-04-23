package me.bradenk.customItems.items;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlWriter;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.abilities.AbilityDefinition;
import me.bradenk.customItems.abilities.AbilityTrigger;
import me.bradenk.customItems.config.ConfigLoader;
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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomItem {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final NamespacedKey ABILITIES_KEY = new NamespacedKey(CustomItems.instance, "custom_item_abilities");

    @NotNull
    private String id;
    @Nullable
    private Component displayName;
    @NotNull
    private Material material;
    @Nullable
    private HashMap<Enchantment, Integer> enchantments;
    @Nullable
    private List<Component> lore;
    @Nullable
    private List<Float> customModelData;
    private boolean unbreakable;
    @Nullable
    private List<AbilityDefinition> abilities;

    public CustomItem(
            @NonNull String id,
            @Nullable Component name,
            @NonNull Material material,
            @Nullable HashMap<Enchantment, Integer> enchantments,
            @Nullable List<Component> lore,
            @Nullable List<Float> cmd,
            boolean unbreakable,
            @Nullable List<AbilityDefinition> abilities
    ) {
        this.id = id;
        this.displayName = name;
        this.material = material;
        this.enchantments = enchantments;
        this.lore = lore;
        this.customModelData = cmd;
        this.unbreakable = unbreakable;
        this.abilities = abilities;
    }
    public @NonNull String getId() {
        return id;
    }

    public Component getName() {
        return displayName;
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
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }
        this.lore.addAll(Arrays.asList(lore));
        setLore(this.lore);
    }

    public void addLore(Component lore) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }
        this.lore.add(lore);
        setLore(this.lore);
    }

    public @NonNull Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
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

    public List<AbilityDefinition> getAbilities() {
        return abilities;
    }

    private CommentedConfig enchantListToConfigurableList(ConcurrentHashMap<Enchantment, Integer> enchantments) {
        CommentedConfig configEnchants = CommentedConfig.inMemory();
        enchantments.forEach((enchant, level) -> configEnchants.set(enchant.getKey().toString(), level));
        return configEnchants;
    }

    public void addEnchant(String enchant, Integer level) {
        Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        this.enchantments.put(Objects.requireNonNull(registry.get(NamespacedKey.minecraft(enchant))), level);
    }

    public void removeEnchant(String enchant) {
        if (this.enchantments != null) {
            Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            this.enchantments.remove(Objects.requireNonNull(registry.get(NamespacedKey.minecraft(enchant))));
        }
    }

    public void addAbility(String type, AbilityTrigger trigger) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Ability type cannot be null or blank.");
        }
        if (trigger == null) {
            throw new IllegalArgumentException("Ability trigger cannot be null.");
        }

        if (abilities == null) {
            abilities = new ArrayList<>();
        }

        String normalizedType = type.trim().toLowerCase();
        String nextId = "ability" + (abilities.size() + 1);

        CommentedConfig data = CommentedConfig.inMemory();
        data.set("cooldown", 0);

        AbilityDefinition ability = new AbilityDefinition(
                nextId,
                normalizedType,
                trigger,
                data
        );

        abilities.add(ability);
    }

    public void removeLastAbility() {
        if (abilities == null || abilities.isEmpty()) {
            return;
        }

        abilities.removeLast();
    }
    @SuppressWarnings("UnstableApiUsage")
    public ItemStack createItem() {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(unbreakable);

        if (displayName != null) {
            meta.displayName(displayName.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        List<Component> finalLore = new ArrayList<>();
        if (lore != null) {
            finalLore.addAll(
                    lore.stream()
                            .map(component -> component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                            .toList()
            );
        }

        CommentedFileConfig mainConfig = ConfigLoader.getMainConfig();
        boolean debug = mainConfig.get("use_vanilla_enchants_in_lore") instanceof Boolean b && b;

        if (enchantments != null && !enchantments.isEmpty() && !debug) {
            if (!finalLore.isEmpty()) {
                finalLore.add(Component.text(" "));
            }

            List<String> enchantList = new ArrayList<>();
            enchantments.forEach((enchant, level) -> {
                String enchantName = enchant.getKey().getKey().toUpperCase().replace('_', ' ') + " " + level;
                enchantList.add(enchantName);
            });

            StringBuilder lineBuilder = new StringBuilder();
            int count = 0;
            boolean firstEnchantLine = true;

            for (int i = 0; i < enchantList.size(); i++) {
                String enchantStr = enchantList.get(i);

                if (count > 0) {
                    lineBuilder.append(", ");
                }

                lineBuilder.append(enchantStr);
                count++;

                boolean nextWouldOverflow = i + 1 < enchantList.size()
                        && lineBuilder.length() + 2 + enchantList.get(i + 1).length() > 35;
                boolean reachedEnchantLimit = count >= 3;

                if (nextWouldOverflow || reachedEnchantLimit || i == enchantList.size() - 1) {
                    Component line = Component.text(lineBuilder.toString())
                            .color(NamedTextColor.BLUE)
                            .decoration(TextDecoration.ITALIC, false);

                    if (firstEnchantLine) {
                        line = line.append(Component.text("ᶦ").color(NamedTextColor.BLACK));
                        firstEnchantLine = false;
                    }

                    finalLore.add(line);
                    lineBuilder.setLength(0);
                    count = 0;
                }
            }
        }

        List<Component> abilityLore = buildAbilityLore();
        if (!abilityLore.isEmpty()) {
            if (!finalLore.isEmpty()) {
                finalLore.add(markAbilityLore(Component.text(" ").decoration(TextDecoration.ITALIC, false)));
            }
            finalLore.addAll(abilityLore);
        }

        if (!finalLore.isEmpty()) {
            meta.lore(finalLore);
        }

        if (customModelData != null && !customModelData.isEmpty()) {
            CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
            cmd.setFloats(customModelData);
            meta.setCustomModelDataComponent(cmd);
        }

        if (abilities != null && !abilities.isEmpty()) {
            meta.getPersistentDataContainer().set(
                    ABILITIES_KEY,
                    PersistentDataType.STRING,
                    serializeAbilities(abilities)
            );
        } else {
            meta.getPersistentDataContainer().remove(ABILITIES_KEY);
        }

        meta.addItemFlags(ItemFlag.values());
        if (debug) {
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (enchantments != null) {
            for (Enchantment ench : enchantments.keySet()) {
                meta.addEnchant(ench, enchantments.get(ench), true);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private static List<AbilityDefinition> parseAbilities(CommentedFileConfig config) {
        Object raw = config.get("abilities");

        if (!(raw instanceof CommentedConfig abilitiesSection)) {
            return new ArrayList<>();
        }

        List<AbilityDefinition> abilities = new ArrayList<>();

        for (String key : abilitiesSection.valueMap().keySet()) {
            Object obj = abilitiesSection.get(key);

            if (!(obj instanceof CommentedConfig abilityConfig)) continue;

            String type = abilityConfig.get("type");
            String triggerRaw = abilityConfig.get("trigger");

            if (type == null || triggerRaw == null) continue;

            AbilityTrigger trigger;
            try {
                trigger = AbilityTrigger.valueOf(triggerRaw.toUpperCase());
            } catch (IllegalArgumentException e) {
                CustomItems.instance.getLogger().warning("Invalid trigger: " + triggerRaw);
                continue;
            }

            abilities.add(new AbilityDefinition(key, type, trigger, abilityConfig));
        }

        return abilities;
    }

    private static String toTitleCase(String input) {
        if (input == null || input.isBlank()) return "";

        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;
            result.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase())
                    .append(" ");
        }

        return result.toString().trim();
    }

    private Component markAbilityLore(Component line) {
        return line.append(Component.text("ᵃ").color(NamedTextColor.BLACK));
    }

    private List<Component> buildAbilityLore() {
        List<Component> lines = new ArrayList<>();
        if (abilities == null || abilities.isEmpty()) return lines;

        for (int i = 0; i < abilities.size(); i++) {
            AbilityDefinition ability = abilities.get(i);

            String triggerName = toTitleCase(ability.trigger().name());
            String abilityName = toTitleCase(ability.id());

            Component header = miniMessage.deserialize("<gold>" + triggerName + ": <yellow>" + abilityName)
                    .decoration(TextDecoration.ITALIC, false);
            lines.add(markAbilityLore(header));

            Object descRaw = ability.data().get("description");
            String description = (descRaw instanceof String desc && !desc.isBlank()) ? desc : "Ability Description";
            Component descLine = miniMessage.deserialize("<gray><italic>" + description)
                    .decoration(TextDecoration.ITALIC, false);
            lines.add(markAbilityLore(descLine));

            Object cooldownRaw = ability.data().get("cooldown");
            if (cooldownRaw instanceof Number cd && cd.doubleValue() > 0) {
                String cooldownText = cd.doubleValue() == cd.intValue()
                        ? String.valueOf(cd.intValue())
                        : String.valueOf(cd.doubleValue());

                Component cooldownLine = miniMessage.deserialize("<dark_gray>Cooldown: <dark_aqua>" + cooldownText + "s")
                        .decoration(TextDecoration.ITALIC, false);
                lines.add(markAbilityLore(cooldownLine));
            }

            if (i < abilities.size() - 1) {
                lines.add(markAbilityLore(Component.text(" ").decoration(TextDecoration.ITALIC, false)));
            }
        }

        return lines;
    }

    private static String serializeAbilities(List<AbilityDefinition> abilities) {
        List<String> entries = new ArrayList<>();

        for (AbilityDefinition ability : abilities) {
            String description = stringValue(ability.data().get("description"));
            String cooldown = String.valueOf(numberValue(ability.data().get("cooldown"), 0));

            String entry = escape(ability.id()) + ";"
                    + escape(ability.type()) + ";"
                    + escape(ability.trigger().name()) + ";"
                    + escape(description) + ";"
                    + escape(cooldown);

            entries.add(entry);
        }

        return String.join("||", entries);
    }

    private static String stringValue(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    private static double numberValue(Object obj, double fallback) {
        return obj instanceof Number n ? n.doubleValue() : fallback;
    }

    private static String escape(String input) {
        return input.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace("|", "\\|");
    }
}