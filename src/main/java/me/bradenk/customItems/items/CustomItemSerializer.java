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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.Option;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CustomItemSerializer {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Optional<CustomItem> deserialize(CommentedFileConfig config) {
        String id = config.get("id");
        if (id == null || id.isBlank()) {
            CustomItems.instance.getLogger().warning("A custom item is missing its id. (" + config.getFile().getPath() + ")");
            return Optional.empty();
        }

        String materialId = config.get("general.material");
        if (materialId == null || materialId.isBlank()) {
            CustomItems.instance.getLogger().warning("A custom item is missing its material. (" + config.getFile().getPath() + ")");
            return Optional.empty();
        }
        Material material = Material.matchMaterial(materialId);
        if (material == null) {
            CustomItems.instance.getLogger().warning("A custom item has an invalid material: " + materialId + " (" + config.getFile().getPath() + ")");
            return Optional.empty();
        }

        Component displayName = config.<String>getOptional("general.display_name")
                .map(miniMessage::deserialize)
                .orElse(null);

        HashMap<Enchantment, Integer> enchantments = deserializeEnchantments(config).orElse(null);

        List<Component> lore = config.<List<String>>getOptional("general.lore")
                .map(lines -> lines.stream().map(miniMessage::deserialize).toList())
                .orElse(null);

        List<Float> customModelData = config.<List<Float>>getOptional("general.custom_model_data")
                .orElse(null);

        boolean unbreakable = config.<Boolean>getOptional("general.unbreakable").orElse(false);

        List<AbilityDefinition> abilities = deserializeAbilities(config).orElse(null);

        return Optional.of(new CustomItem(id, displayName, material, enchantments, lore, customModelData, unbreakable, abilities));
    }

    public static void serialize(CustomItem item, @Nullable CommentedFileConfig config) throws FileAlreadyExistsException {
        if (config == null) {
            File configFile = new File(CustomItems.instance.getDataFolder(), "items/" + item.getId() + ".toml");
            if (configFile.exists()) {
                throw new FileAlreadyExistsException("Trying to save an item without a config passed, but the file already exists.");
            }
            config = CommentedFileConfig.of(configFile);
        }

        config.set("id", item.getId());
        config.set("general.material", item.getMaterial().getKey().asString());

        HashMap<Enchantment, Integer> enchantments = item.getEnchantments();
        if (enchantments != null && !enchantments.isEmpty()) {
            config.set("general.enchantments", serializeEnchantments(enchantments));
        }

        if (item.getLore() != null) {
            config.set("general.lore", item.getLore().stream().map(miniMessage::serialize).toList());
        }

        if (item.getCustomModelData() != null) {
            config.set("general.custom_model_data", item.getCustomModelData());
        }

        if (item.isUnbreakable()) {
            config.set("general.unbreakable", true);
        }

        List<AbilityDefinition> abilities = item.getAbilities();
        if (abilities != null && !abilities.isEmpty()) {
            config.set("abilities", serializeAbilities(abilities));
        }

        config.save();
        TomlWriter writer = new TomlWriter();
        writer.write(config, config.getFile(), WritingMode.REPLACE_ATOMIC);
    }
    public static void serialize(CustomItem item) throws FileAlreadyExistsException {
        serialize(item, null);
    }

    private static Optional<HashMap<Enchantment, Integer>> deserializeEnchantments(CommentedFileConfig config) {
        Optional<CommentedConfig> raw = config.getOptional("general.enchantments");
        if (raw.isEmpty()) return Optional.empty();
        HashMap<Enchantment, Integer> enchantments = new HashMap<>();
        raw.get().entrySet().forEach(entry -> {
            String enchantName = entry.getKey();
            int level = entry.getValue();

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
        });

        return Optional.of(enchantments);
    }

    private static CommentedConfig serializeEnchantments(HashMap<Enchantment, Integer> enchantments) {
        CommentedConfig config = CommentedConfig.inMemory();
        enchantments.forEach((enchant, level) -> config.set(enchant.getKey().asString(), level));
        return config;
    }

    private static Optional<List<AbilityDefinition>> deserializeAbilities(CommentedFileConfig config) {
        Optional<CommentedConfig> raw = config.getOptional("abilities");
        if (raw.isEmpty()) return Optional.empty();

        List<AbilityDefinition> abilities = new ArrayList<>();
        raw.get().entrySet().forEach(entry -> {
           String key = entry.getKey();
           CommentedConfig abilityConfig = entry.getValue();

           String type = abilityConfig.get("type");
           if (type == null) {
               CustomItems.instance.getLogger().warning("Ability " + key + " is missing a type");
               return;
           }
           String trigger = abilityConfig.get("trigger");
            if (trigger == null) {
                CustomItems.instance.getLogger().warning("Ability " + key + " is missing a trigger");
                return;
            }
            try {
                AbilityTrigger parsedTrigger = AbilityTrigger.valueOf(trigger.toUpperCase());
                abilities.add(new AbilityDefinition(key, type, parsedTrigger, abilityConfig));
            } catch (IllegalArgumentException ex) {
                CustomItems.instance.getLogger().warning("Invalid trigger: " + trigger);
            }
        });

        if (abilities.isEmpty()) return Optional.empty();
        return Optional.of(abilities);
    }

    private static CommentedConfig serializeAbilities(List<AbilityDefinition> abilities) {
        CommentedConfig abilitiesSection = CommentedConfig.inMemory();

        for (AbilityDefinition ability : abilities) {
            CommentedConfig abilityConfig = CommentedConfig.inMemory();
            abilityConfig.set("type", ability.type());
            abilityConfig.set("trigger", ability.trigger().name());
            abilitiesSection.set(ability.id(), abilityConfig);
        }

        return abilitiesSection;
    }
}
