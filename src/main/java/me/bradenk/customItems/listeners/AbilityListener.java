package me.bradenk.customItems.listeners;

import com.electronwill.nightconfig.core.CommentedConfig;
import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.abilities.AbilityContext;
import me.bradenk.customItems.abilities.AbilityDefinition;
import me.bradenk.customItems.abilities.AbilityTrigger;
import me.bradenk.customItems.abilities.CustomAbility;
import me.bradenk.customItems.config.ConfigLoader;
import me.bradenk.customItems.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbilityListener implements Listener {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    private static final CustomItems plugin = CustomItems.instance;
    private static final NamespacedKey ABILITIES_KEY = new NamespacedKey(plugin, "custom_item_abilities");

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();
        if (stack == null) return;

        if (event.getAction().isRightClick()) {
            executeAbilities(player, stack, AbilityTrigger.RIGHT_CLICK, null);
        }

        if (event.getAction().isLeftClick()) {
            executeAbilities(player, stack, AbilityTrigger.LEFT_CLICK, null);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        executeAbilities(player, item, AbilityTrigger.HIT_ENTITY, event.getEntity());
    }

    private void executeAbilities(Player player, ItemStack stack, AbilityTrigger trigger, Entity target) {
        if (stack == null || stack.getType() == Material.AIR) return;

        List<AbilityDefinition> abilities = getAbilitiesFromStack(stack);
        if (abilities.isEmpty()) return;

        CustomItem item = getItemFromStack(stack);

        for (AbilityDefinition ability : abilities) {
            if (ability.trigger() != trigger) continue;

            CustomAbility runtime = CustomItems.getAbilityRegistry().get(ability.type());
            if (runtime == null) {
                plugin.getLogger().warning("Unknown ability: " + ability.type());
                continue;
            }

            long now = System.currentTimeMillis();
            double cooldownSeconds = numberValue(ability.data().get("cooldown"), 0);

            Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
            String cooldownKey = ability.id() + ":" + ability.type();

            if (cooldownSeconds > 0) {
                long lastUse = playerCooldowns.getOrDefault(cooldownKey, 0L);
                long cooldownMillis = (long) (cooldownSeconds * 1000);

                if (now - lastUse < cooldownMillis) {
                    continue;
                }

                playerCooldowns.put(cooldownKey, now);
            }

            runtime.execute(new AbilityContext(player, stack, item, target), ability);
        }
    }

    private List<AbilityDefinition> getAbilitiesFromStack(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return new ArrayList<>();

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return new ArrayList<>();

        String raw = meta.getPersistentDataContainer().get(ABILITIES_KEY, PersistentDataType.STRING);
        if (raw != null && !raw.isBlank()) {
            return deserializeAbilities(raw);
        }

        CustomItem item = getItemFromStack(stack);
        if (item != null && item.getAbilities() != null) {
            return new ArrayList<>(item.getAbilities());
        }

        return new ArrayList<>();
    }

    private CustomItem getItemFromStack(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;

        String id = meta.getPersistentDataContainer().get(
                new NamespacedKey(plugin, "custom_item_id"),
                PersistentDataType.STRING
        );

        if (id == null) return null;
        return ConfigLoader.customItems.get(id);
    }

    private static List<AbilityDefinition> deserializeAbilities(String raw) {
        List<AbilityDefinition> abilities = new ArrayList<>();
        if (raw == null || raw.isBlank()) return abilities;

        String[] entries = raw.split("\\|\\|");
        for (String entry : entries) {
            if (entry.isBlank()) continue;

            String[] parts = entry.split(";", -1);
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

    private static double numberValue(Object obj, double fallback) {
        return obj instanceof Number n ? n.doubleValue() : fallback;
    }
}