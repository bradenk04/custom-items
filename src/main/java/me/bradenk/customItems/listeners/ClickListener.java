package me.bradenk.customItems.listeners;

import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.gui.ItemEditSession;
import me.bradenk.customItems.items.CustomItem;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClickListener implements Listener {

    private final CustomItems plugin = CustomItems.instance;
    private final ConcurrentHashMap<UUID, PendingAnvilInput> pendingInputs = new ConcurrentHashMap<>();

    @EventHandler
    public void onEditorClick(InventoryClickEvent event) {
        if (event.getView() instanceof AnvilView) return;
        plugin.getLogger().info("1");
        if (event.getCurrentItem() == null) return;
        plugin.getLogger().info("2");
        if (!(event.getWhoClicked() instanceof Player player)) return;
        plugin.getLogger().info("3");
        if (!isItemEditor(event.getView())) return;
        plugin.getLogger().info("4");

        event.setCancelled(true);

        ItemEditSession session = plugin.getSession(player.getUniqueId());
        if (session == null) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;

        switch (clicked.getType()) {
            case NAME_TAG -> openAnvilInput(
                    player,
                    InputType.DISPLAY_NAME,
                    "Display Name",
                    plainOrEmpty(session.getDisplayName()),
                    Material.NAME_TAG
            );

            case OAK_SIGN -> openAnvilInput(
                    player,
                    InputType.LORE,
                    "Lore",
                    loreToSingleInput(session.getLore()),
                    Material.OAK_SIGN
            );

            case LEVER -> {
                session.setUnbreakable(!session.isUnbreakable());
                player.sendMessage(Component.text("Set unbreakable to " + session.isUnbreakable()));
                player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.PLAYER, 1f, 1f));
                reopenEditor(player);
            }

            case ENCHANTED_BOOK -> openAnvilInput(
                    player,
                    InputType.ENCHANT,
                    "Enchant",
                    "sharpness:5",
                    Material.ENCHANTED_BOOK
            );

            case DISPENSER -> {
                CustomItem item = null;
                try {
                    item = session.toCustomItem();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                player.getInventory().addItem(item.createItem());
                player.sendMessage(Component.text("Given item " + item.getId()));
                player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.PLAYER, 1f, 1f));
                player.closeInventory();
            }

            case WRITABLE_BOOK -> {
                CustomItem item = null;
                try {
                    item = session.toCustomItem();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                item.save();
                player.sendMessage(Component.text("Saved item " + item.getId()));
                player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.PLAYER, 1f, 1f));
                player.closeInventory();
            }

            default -> {
                if (event.getSlot() == 12) {
                    openAnvilInput(
                            player,
                            InputType.MATERIAL,
                            "Material",
                            session.getMaterial().name(),
                            session.getMaterial()
                    );
                }
            }
        }
    }

    @EventHandler
    public void onPrepare(PrepareAnvilEvent event) {
        if (!(event.getView() instanceof AnvilView view)) return;
        plugin.getLogger().info("1.1");
        if (!(event.getView().getPlayer() instanceof Player player)) return;
        plugin.getLogger().info("1.2");
        if (!pendingInputs.containsKey(player.getUniqueId())) return;
        plugin.getLogger().info("1.3");

        String text = view.getRenameText();
        if (text == null || text.isBlank()) {
            event.setResult(null);
            plugin.getLogger().info("?>???");
            return;
        }

        ItemStack result = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = result.getItemMeta();
        meta.displayName(Component.text("Apply"));
        result.setItemMeta(meta);

        event.setResult(result);
        view.setRepairCost(0);
        view.setMaximumRepairCost(0);
        view.setRepairItemCountCost(0);
    }

    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        plugin.getLogger().info("2.1");

        PendingAnvilInput pending = pendingInputs.get(player.getUniqueId());
        if (pending == null) return;
        plugin.getLogger().info("2.2");
        if (!(event.getView() instanceof AnvilView anvilView)) return;
        plugin.getLogger().info("2.3");
        if (event.getRawSlot() != 2) return;
        plugin.getLogger().info("2.5");

        event.setCancelled(true);

        ItemEditSession session = plugin.getSession(player.getUniqueId());
        if (session == null) {
            pendingInputs.remove(player.getUniqueId());
            player.closeInventory();
            return;
        }

        String input = anvilView.getRenameText();
        if (input == null || input.isBlank()) {
            fail(player, "Input cannot be empty.");
            return;
        }

        boolean success = switch (pending.type()) {
            case DISPLAY_NAME -> applyDisplayName(session, input, player);
            case MATERIAL -> applyMaterial(session, input, player);
            case LORE -> applyLore(session, input, player);
            case ENCHANT -> applyEnchant(session, input, player);
        };

        if (!success) return;

        pending.setReopenEditor(false);
        pendingInputs.remove(player.getUniqueId());
        player.closeInventory();

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.PLAYER, 1f, 1f));
            reopenEditor(player);
        });
    }

    @EventHandler
    public void onAnvilClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(event.getView() instanceof AnvilView)) return;

        PendingAnvilInput pending = pendingInputs.get(player.getUniqueId());
        if (pending == null) return;

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (pendingInputs.containsKey(player.getUniqueId())) {
                if (pending.reopenEditor() && plugin.getSession(player.getUniqueId()) != null) {
                    reopenEditor(player);
                }
                pendingInputs.remove(player.getUniqueId());
            }
        });
    }

    private void openAnvilInput(Player player, InputType type, String title, String initialText, Material icon) {
        PendingAnvilInput existing = pendingInputs.remove(player.getUniqueId());
        if (existing != null) {
            existing.setReopenEditor(false);
        }

        AnvilView view = MenuType.ANVIL.builder()
                .title(Component.text(title))
                .build(player);

        ItemStack left = new ItemStack(icon);
        ItemMeta meta = left.getItemMeta();
        if (initialText != null && !initialText.isBlank()) {
            meta.displayName(Component.text(initialText));
        } else {
            meta.displayName(Component.text(" "));
        }
        left.setItemMeta(meta);

        view.getTopInventory().setItem(0, left);
        view.setRepairCost(0);
        view.setMaximumRepairCost(0);
        view.setRepairItemCountCost(0);

        PendingAnvilInput pending = new PendingAnvilInput(type);
        pendingInputs.put(player.getUniqueId(), pending);
        player.openInventory(view);
    }

    private boolean applyDisplayName(ItemEditSession session, String input, Player player) {
        session.setDisplayName(CustomItems.colorCode(input));
        player.sendMessage(Component.text("Updated display name."));
        return true;
    }

    private boolean applyMaterial(ItemEditSession session, String input, Player player) {

        Material material = Material.matchMaterial(input.trim());
        if (material == null) {
            fail(player, "Invalid material.");
            return false;
        }

        session.setMaterial(material);
        player.sendMessage(Component.text("Updated material to " + material.name()));
        return true;
    }

    private boolean applyLore(ItemEditSession session, String input, Player player) {
        List<Component> lore = new ArrayList<>();
        for (String part : input.split("\\|")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                lore.add(CustomItems.colorCode(trimmed));
            }
        }
        session.setLore(lore);
        player.sendMessage(Component.text(lore.isEmpty() ? "Cleared lore." : "Updated lore."));
        return true;
    }

    private boolean applyEnchant(ItemEditSession session, String input, Player player) {
        ParsedEnchant parsed = parseEnchant(input);
        if (parsed == null) {
            fail(player, "Use enchant:level like sharpness:5");
            return false;
        }

        if (session.getEnchantments() == null) {
            session.setEnchantments(new ConcurrentHashMap<>());
        }

        session.getEnchantments().put(parsed.enchantment(), parsed.level());
        player.sendMessage(Component.text("Added enchant " + parsed.enchantment().getKey().getKey() + ":" + parsed.level()));
        return true;
    }

    private void reopenEditor(Player player) {
        ItemEditSession session = plugin.getSession(player.getUniqueId());
        if (session == null) return;
        try {
            plugin.getItemGUI().openGUI(player, session.toCustomItem().getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isItemEditor(InventoryView view) {
        String title = PlainTextComponentSerializer.plainText().serialize(view.title());
        return title.startsWith("Custom Item: ");
    }

    private String plainOrEmpty(Component component) {
        if (component == null) return "";
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    private String loreToSingleInput(List<Component> lore) {
        if (lore == null || lore.isEmpty()) return "";
        List<String> lines = new ArrayList<>();
        for (Component component : lore) {
            lines.add(plainOrEmpty(component));
        }
        return String.join(" | ", lines);
    }

    private ParsedEnchant parseEnchant(String input) {
        String[] split = input.split(":");
        if (split.length != 2) return null;

        String enchantName = split[0].trim().toLowerCase(Locale.ROOT);
        int level;
        try {
            level = Integer.parseInt(split[1].trim());
        } catch (NumberFormatException e) {
            return null;
        }

        if (level <= 0) return null;

        Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantName));
        if (enchantment == null) {
            enchantment = Enchantment.getByName(enchantName.toUpperCase(Locale.ROOT));
        }
        if (enchantment == null) return null;

        return new ParsedEnchant(enchantment, level);
    }

    private void fail(Player player, String message) {
        player.sendMessage(Component.text(message));
        player.playSound(Sound.sound(Key.key("minecraft:entity.villager.no"), Sound.Source.PLAYER, 1f, 1f));
    }

    private enum InputType {
        DISPLAY_NAME,
        MATERIAL,
        LORE,
        ENCHANT
    }

    private static final class PendingAnvilInput {
        private final InputType type;
        private boolean reopenEditor = true;

        private PendingAnvilInput(InputType type) {
            this.type = type;
        }

        public InputType type() {
            return type;
        }

        public boolean reopenEditor() {
            return reopenEditor;
        }

        public void setReopenEditor(boolean reopenEditor) {
            this.reopenEditor = reopenEditor;
        }
    }

    private record ParsedEnchant(Enchantment enchantment, int level) {
    }
}