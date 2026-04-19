package me.bradenk.customItems.command;

import com.electronwill.nightconfig.core.CommentedConfig;
import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.abilities.AbilityDefinition;
import me.bradenk.customItems.abilities.AbilityTrigger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;


@Command("item ability")
@CommandPermission("customitems.item.ability")
public class ItemAbilityCommand {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final NamespacedKey ABILITIES_KEY = new NamespacedKey(CustomItems.instance, "custom_item_abilities");

    @Subcommand("add <type>")
    public void addAbility(BukkitCommandActor actor, String type) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to use this command.");
            return;
        }

        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item.", NamedTextColor.RED));
            return;
        }

        ItemMeta meta = held.getItemMeta();
        if (meta == null) {
            player.sendMessage(Component.text("That item cannot store abilities.", NamedTextColor.RED));
            return;
        }

        String normalizedType = type.trim().toLowerCase();
        AbilityTrigger trigger = AbilityTrigger.RIGHT_CLICK;

        List<AbilityDefinition> abilities = readAbilities(meta);
        String nextId = "ability" + (abilities.size() + 1);

        CommentedConfig data = CommentedConfig.inMemory();
        data.set("description", toTitleCase(normalizedType));
        data.set("cooldown", 0);

        abilities.add(new AbilityDefinition(
                nextId,
                normalizedType,
                trigger,
                data
        ));

        meta.getPersistentDataContainer().set(
                ABILITIES_KEY,
                PersistentDataType.STRING,
                serializeAbilities(abilities)
        );

        held.setItemMeta(meta);
        refreshAbilityDisplayLore(held);
        player.getInventory().setItemInMainHand(held);

        player.sendMessage(
                Component.text("Added ability ", NamedTextColor.GREEN)
                        .append(Component.text(normalizedType, NamedTextColor.YELLOW))
                        .append(Component.text(" with trigger ", NamedTextColor.GREEN))
                        .append(Component.text(trigger.name(), NamedTextColor.AQUA))
        );
    }

    @Subcommand("add <type> <trigger>")
    public void addAbilityWithTrigger(BukkitCommandActor actor, String type, String trigger) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to use this command.");
            return;
        }

        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item.", NamedTextColor.RED));
            return;
        }

        AbilityTrigger parsedTrigger;
        try {
            parsedTrigger = AbilityTrigger.valueOf(trigger.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            player.sendMessage(Component.text("Invalid trigger. Use RIGHT_CLICK, LEFT_CLICK, HIT_ENTITY, KILL_ENTITY, HOLD", NamedTextColor.RED));
            return;
        }

        ItemMeta meta = held.getItemMeta();
        if (meta == null) {
            player.sendMessage(Component.text("That item cannot store abilities.", NamedTextColor.RED));
            return;
        }

        String normalizedType = type.trim().toLowerCase();

        List<AbilityDefinition> abilities = readAbilities(meta);
        String nextId = "ability" + (abilities.size() + 1);

        CommentedConfig data = CommentedConfig.inMemory();
        data.set("description", toTitleCase(normalizedType));
        data.set("cooldown", 0);

        abilities.add(new AbilityDefinition(
                nextId,
                normalizedType,
                parsedTrigger,
                data
        ));

        meta.getPersistentDataContainer().set(
                ABILITIES_KEY,
                PersistentDataType.STRING,
                serializeAbilities(abilities)
        );

        held.setItemMeta(meta);
        refreshAbilityDisplayLore(held);
        player.getInventory().setItemInMainHand(held);

        player.sendMessage(
                Component.text("Added ability ", NamedTextColor.GREEN)
                        .append(Component.text(normalizedType, NamedTextColor.YELLOW))
                        .append(Component.text(" with trigger ", NamedTextColor.GREEN))
                        .append(Component.text(parsedTrigger.name(), NamedTextColor.AQUA))
        );
    }

    @Subcommand("remove")
    public void removeLastAbility(BukkitCommandActor actor) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to use this command.");
            return;
        }

        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item.", NamedTextColor.RED));
            return;
        }

        ItemMeta meta = held.getItemMeta();
        if (meta == null) {
            player.sendMessage(Component.text("That item cannot store abilities.", NamedTextColor.RED));
            return;
        }

        List<AbilityDefinition> abilities = readAbilities(meta);
        if (abilities.isEmpty()) {
            player.sendMessage(Component.text("This item has no abilities.", NamedTextColor.RED));
            return;
        }

        AbilityDefinition removed = abilities.remove(abilities.size() - 1);

        if (abilities.isEmpty()) {
            meta.getPersistentDataContainer().remove(ABILITIES_KEY);
        } else {
            meta.getPersistentDataContainer().set(
                    ABILITIES_KEY,
                    PersistentDataType.STRING,
                    serializeAbilities(abilities)
            );
        }

        held.setItemMeta(meta);
        refreshAbilityDisplayLore(held);
        player.getInventory().setItemInMainHand(held);

        player.sendMessage(
                Component.text("Removed ability ", NamedTextColor.GREEN)
                        .append(Component.text(removed.type(), NamedTextColor.YELLOW))
        );
    }

    private static List<AbilityDefinition> readAbilities(ItemMeta meta) {
        String raw = meta.getPersistentDataContainer().get(ABILITIES_KEY, PersistentDataType.STRING);
        return deserializeAbilities(raw);
    }

    private void refreshAbilityDisplayLore(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;

        List<Component> lore = meta.hasLore() && meta.lore() != null
                ? new ArrayList<>(meta.lore())
                : new ArrayList<>();

        lore = stripGeneratedAbilityLore(lore);

        List<AbilityDefinition> abilities = readAbilities(meta);
        List<Component> abilityLore = buildAbilityLore(abilities);

        if (!abilityLore.isEmpty()) {
            if (!lore.isEmpty()) {
                lore.add(markAbilityLore(Component.text(" ").decoration(TextDecoration.ITALIC, false)));
            }
            lore.addAll(abilityLore);
        }

        meta.lore(lore.isEmpty() ? null : lore);
        meta.addItemFlags(ItemFlag.values());
        stack.setItemMeta(meta);
    }

    private List<Component> stripGeneratedAbilityLore(List<Component> originalLore) {
        if (originalLore == null || originalLore.isEmpty()) {
            return new ArrayList<>();
        }

        List<Component> cleaned = new ArrayList<>();

        for (Component line : originalLore) {
            String plain = PlainTextComponentSerializer.plainText().serialize(line);
            if (plain.contains("ᶦ")) {
                continue;
            }
            cleaned.add(line);
        }

        while (!cleaned.isEmpty()) {
            String plain = PlainTextComponentSerializer.plainText()
                    .serialize(cleaned.get(cleaned.size() - 1))
                    .trim();

            if (plain.isEmpty()) {
                cleaned.remove(cleaned.size() - 1);
            } else {
                break;
            }
        }

        return cleaned;
    }

    private List<Component> buildAbilityLore(List<AbilityDefinition> abilities) {
        List<Component> lines = new ArrayList<>();
        if (abilities == null || abilities.isEmpty()) return lines;

        for (int i = 0; i < abilities.size(); i++) {
            AbilityDefinition ability = abilities.get(i);

            String name = toTitleCase(ability.id());

            Component header = MINI_MESSAGE.deserialize("<gold>Ability: <yellow>" + name)
                    .decoration(TextDecoration.ITALIC, false);
            lines.add(markAbilityLore(header));

            Object descRaw = ability.data().get("description");
            if (descRaw instanceof String desc && !desc.isBlank()) {
                Component descLine = MINI_MESSAGE.deserialize("<dark_gray>(" + desc + ")")
                        .decoration(TextDecoration.ITALIC, false);
                lines.add(markAbilityLore(descLine));
            }

            Object cooldownRaw = ability.data().get("cooldown");
            if (cooldownRaw instanceof Number cd && cd.doubleValue() > 0) {
                String cooldownText = cd.doubleValue() == cd.intValue()
                        ? String.valueOf(cd.intValue())
                        : String.valueOf(cd.doubleValue());

                Component cooldownLine = MINI_MESSAGE.deserialize("<gray>Cooldown: <cyan>" + cooldownText)
                        .decoration(TextDecoration.ITALIC, false);
                lines.add(markAbilityLore(cooldownLine));
            }

            if (i < abilities.size() - 1) {
                lines.add(markAbilityLore(Component.text(" ").decoration(TextDecoration.ITALIC, false)));
            }
        }

        return lines;
    }

    private Component markAbilityLore(Component line) {
        return line.append(Component.text("ᶦ").color(NamedTextColor.BLACK));
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

    private static List<AbilityDefinition> deserializeAbilities(String raw) {
        List<AbilityDefinition> abilities = new ArrayList<>();
        if (raw == null || raw.isBlank()) return abilities;

        String[] entries = raw.split("\\|\\|");
        for (String entry : entries) {
            if (entry.isBlank()) continue;

            String[] parts = splitEscaped(entry, ';', 5);
            if (parts.length < 5) continue;

            String id = unescape(parts[0]);
            String type = unescape(parts[1]);
            String triggerRaw = unescape(parts[2]);
            String description = unescape(parts[3]);
            String cooldownRaw = unescape(parts[4]);

            AbilityTrigger trigger;
            try {
                trigger = AbilityTrigger.valueOf(triggerRaw.toUpperCase());
            } catch (IllegalArgumentException ex) {
                continue;
            }

            CommentedConfig data = CommentedConfig.inMemory();
            if (!description.isBlank()) {
                data.set("description", description);
            }

            try {
                if (!cooldownRaw.isBlank()) {
                    data.set("cooldown", Double.parseDouble(cooldownRaw));
                }
            } catch (NumberFormatException ignored) {
                data.set("cooldown", 0);
            }

            abilities.add(new AbilityDefinition(id, type, trigger, data));
        }

        return abilities;
    }

    private static String[] splitEscaped(String input, char separator, int expectedParts) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (char c : input.toCharArray()) {
            if (escaping) {
                current.append(c);
                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else if (c == separator && parts.size() < expectedParts - 1) {
                parts.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        parts.add(current.toString());
        return parts.toArray(String[]::new);
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

    private static String unescape(String input) {
        StringBuilder out = new StringBuilder();
        boolean escaping = false;

        for (char c : input.toCharArray()) {
            if (escaping) {
                out.append(c);
                escaping = false;
            } else if (c == '\\') {
                escaping = true;
            } else {
                out.append(c);
            }
        }

        return out.toString();
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
}