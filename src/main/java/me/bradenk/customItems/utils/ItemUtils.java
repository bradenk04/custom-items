package me.bradenk.customItems.utils;

import me.bradenk.customItems.CustomItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ItemUtils {

    private static Component label(String prefixColored, String text) {
        return CustomItems.colorCode("&r")
                .append(CustomItems.colorCode(prefixColored)
                        .append(Component.text(text).color(NamedTextColor.GRAY)))
                .decoration(TextDecoration.ITALIC, false);
    }

    static final Component DAMAGE              = label("&4⚔ ", "Damage: ");
    static final Component ATTACK_SPEED        = label("&6⚔ ", "Attack Speed: ");
    static final Component BLOCK_BREAK_SPEED   = label("&6⛏ ", "Block Break Speed: ");
    static final Component GRAVITY             = label("&9⬇ ", "Gravity: ");
    static final Component LUCK                = label("&a☘ ", "Luck: ");
    static final Component MAX_HEALTH          = label("&a❤ ", "Max Health: ");
    static final Component MOVEMENT_SPEED      = label("&9༄ ", "Speed: ");
    static final Component SCALE               = label("&9🌲 ", "Scale: ");
    static final Component FOLLOW_RANGE                    = label("&b👁 ", "Follow Range: ");
    static final Component KNOCKBACK_RESISTANCE            = label("&d🛡 ", "Knockback Resist: ");
    static final Component FLYING_SPEED                    = label("&b🪽 ", "Flying Speed: ");
    static final Component ATTACK_KNOCKBACK                = label("&c💥 ", "Attack Knockback: ");
    static final Component ARMOR                           = label("&7🛡 ", "Armor: ");
    static final Component ARMOR_TOUGHNESS                 = label("&7🛡+", "Armor Toughness: ");
    static final Component FALL_DAMAGE_MULTIPLIER          = label("&e⬇ ", "Fall Dmg Multiplier: ");
    static final Component MAX_ABSORPTION                  = label("&6🟨 ", "Max Absorption: ");
    static final Component SAFE_FALL_DISTANCE              = label("&e⬍ ", "Safe Fall Distance: ");
    static final Component STEP_HEIGHT                     = label("&e⤴ ", "Step Height: ");
    static final Component JUMP_STRENGTH                   = label("&a⬆ ", "Jump Strength: ");
    static final Component BURNING_TIME                    = label("&c🔥 ", "Burning Time: ");
    static final Component CAMERA_DISTANCE                 = label("&b🎥 ", "Camera Distance: ");
    static final Component EXPLOSION_KB_RESIST             = label("&d🧨 ", "Explosion KB Resist: ");
    static final Component MOVEMENT_EFFICIENCY             = label("&b➠ ", "Movement Efficiency: ");
    static final Component OXYGEN_BONUS                    = label("&b🫧 ", "Oxygen Bonus: ");
    static final Component WATER_MOVEMENT_EFFICIENCY       = label("&3🌊 ", "Water Move Efficiency: ");
    static final Component TEMPT_RANGE                     = label("&d🥕 ", "Tempt Range: ");
    static final Component BLOCK_INTERACTION_RANGE         = label("&f🖐 ", "Block Interaction Range: ");
    static final Component ENTITY_INTERACTION_RANGE        = label("&f🤝 ", "Entity Interaction Range: ");
    static final Component MINING_EFFICIENCY               = label("&6⛏+", "Mining Efficiency: ");
    static final Component SNEAKING_SPEED                  = label("&8👣 ", "Sneaking Speed: ");
    static final Component SUBMERGED_MINING_SPEED          = label("&3⛏ ", "Submerged Mining Speed: ");
    static final Component SWEEPING_DAMAGE_RATIO           = label("&c〰 ", "Sweeping Dmg Ratio: ");
    static final Component SPAWN_REINFORCEMENTS            = label("&5⚑ ", "Spawn Reinforcements: ");
    static final Component WAYPOINT_TRANSMIT_RANGE         = label("&d📡 ", "Waypoint Transmit Range: ");
    static final Component WAYPOINT_RECEIVE_RANGE          = label("&d📶 ", "Waypoint Receive Range: ");

    /**
     * Maps a namespaced attribute key (e.g. "minecraft:attack_damage") to a formatted display prefix.
     * Keep the TEXT after each label consistent with your lore builder (it appends the value).
     */
    public static Component getAttributeDisplay(String nk) {
        if (nk == null) throw new IllegalStateException("Unexpected value: null");
        switch (nk) {
            case "minecraft:max_health":                   return MAX_HEALTH;
            case "minecraft:follow_range":                 return FOLLOW_RANGE;
            case "minecraft:knockback_resistance":         return KNOCKBACK_RESISTANCE;
            case "minecraft:movement_speed":               return MOVEMENT_SPEED;
            case "minecraft:flying_speed":                 return FLYING_SPEED;
            case "minecraft:attack_damage":                return DAMAGE;
            case "minecraft:attack_knockback":             return ATTACK_KNOCKBACK;
            case "minecraft:attack_speed":                 return ATTACK_SPEED;
            case "minecraft:armor":                        return ARMOR;
            case "minecraft:armor_toughness":              return ARMOR_TOUGHNESS;
            case "minecraft:fall_damage_multiplier":       return FALL_DAMAGE_MULTIPLIER;
            case "minecraft:luck":                         return LUCK;
            case "minecraft:max_absorption":               return MAX_ABSORPTION;
            case "minecraft:safe_fall_distance":           return SAFE_FALL_DISTANCE;
            case "minecraft:scale":                        return SCALE;
            case "minecraft:step_height":                  return STEP_HEIGHT;
            case "minecraft:gravity":                      return GRAVITY;
            case "minecraft:jump_strength":                return JUMP_STRENGTH;
            case "minecraft:burning_time":                 return BURNING_TIME;
            case "minecraft:camera_distance":              return CAMERA_DISTANCE;
            case "minecraft:explosion_knockback_resistance": return EXPLOSION_KB_RESIST;
            case "minecraft:movement_efficiency":          return MOVEMENT_EFFICIENCY;
            case "minecraft:oxygen_bonus":                 return OXYGEN_BONUS;
            case "minecraft:water_movement_efficiency":    return WATER_MOVEMENT_EFFICIENCY;
            case "minecraft:tempt_range":                  return TEMPT_RANGE;
            case "minecraft:block_interaction_range":      return BLOCK_INTERACTION_RANGE;
            case "minecraft:entity_interaction_range":     return ENTITY_INTERACTION_RANGE;

            case "minecraft:block_break_speed":            return BLOCK_BREAK_SPEED;
            case "minecraft:mining_efficiency":            return MINING_EFFICIENCY;
            case "minecraft:sneaking_speed":               return SNEAKING_SPEED;
            case "minecraft:submerged_mining_speed":       return SUBMERGED_MINING_SPEED;

            case "minecraft:sweeping_damage_ratio":        return SWEEPING_DAMAGE_RATIO;
            case "minecraft:spawn_reinforcements":         return SPAWN_REINFORCEMENTS;
            case "minecraft:waypoint_transmit_range":      return WAYPOINT_TRANSMIT_RANGE;
            case "minecraft:waypoint_receive_range":       return WAYPOINT_RECEIVE_RANGE;

            default:
                throw new IllegalStateException("Unexpected value: " + nk);
        }
    }

//    public static BasicItem wrapAsBasicItem(ItemStack stack) {
//        if (stack != null && !stack.getType().isAir()) {
//            ItemMeta meta = stack.getItemMeta();
//
//            Component displayName;
//            if (meta.displayName() != null && meta.displayName().contains(Rarity.BASIC.getDisplay())) {
//                String plain = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
//                String rarityPlain = PlainTextComponentSerializer.plainText().serialize(Rarity.BASIC.getDisplay());
//                if (plain.contains(rarityPlain)) {
//                    plain = plain.replace(rarityPlain, "").trim();
//                }
//                displayName = Component.text(plain);
//            } else {
//                displayName = Component.text(BasicItemParser.toTitleCase(stack.getType().name().toUpperCase()).replace("_", " "));
//            }
//
//            Component defaultDescription = Component.text("Vanilla Item", NamedTextColor.GRAY);
//            Rarity defaultRarity = Rarity.BASIC;
//
//            var enchants = new java.util.concurrent.ConcurrentHashMap<org.bukkit.enchantments.Enchantment, Integer>(stack.getEnchantments());
//            Map<Attribute, Integer> attrs = (meta != null) ? accumulateAttributes(meta) : new java.util.concurrent.ConcurrentHashMap<>();
//
//            return new BasicItem.Builder()
//                    .displayName(displayName)
//                    .description(defaultDescription)
//                    .material(stack.getType())
//                    .amount(stack.getAmount())
//                    .id(0)
//                    .key("null")
//                    .rarity(defaultRarity)
//                    .upgraded(false)
//                    .addAllEnchantments(enchants)
//                    .addAllAttributes(attrs)
//                    .lore(java.util.Collections.emptyList())
//                    .build();
//        }
//        return null;
//    }
//
//    private static ConcurrentHashMap<Attribute, Integer> accumulateAttributes(ItemMeta meta) {
//        var out = new ConcurrentHashMap<Attribute, Integer>();
//        var multimap = meta.getAttributeModifiers();
//        if (multimap == null) return out;
//        for (var entry : multimap.entries()) {
//            var attr = entry.getKey();
//            var mod  = entry.getValue();
//            if (mod.getOperation() == AttributeModifier.Operation.ADD_NUMBER) {
//                out.merge(attr, (int)Math.round(mod.getAmount()), Integer::sum);
//            }
//        }
//        return out;
//    }

}
