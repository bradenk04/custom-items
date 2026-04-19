package me.bradenk.customItems.command;

import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.abilities.AbilityDefinition;
import me.bradenk.customItems.config.ConfigLoader;
import me.bradenk.customItems.gui.ItemEditSession;
import me.bradenk.customItems.items.CustomItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Command("item")
public class ItemEditorCommand {
    @Subcommand("edit <id>")
    public void editCommand(BukkitCommandActor actor, String id) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }

        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }

        if (id == null || !id.matches("^[a-z_]+$")) {
            player.sendMessage(Component.text("Invalid key. Use a-z and underscores.", NamedTextColor.RED));
            return;
        }

        CustomItem item = ConfigLoader.customItems.get(id);
        if (item == null) {
            player.sendMessage(Component.text("Item " + id + " does not exist.", NamedTextColor.RED));
            return;
        }

        ItemEditSession session = new ItemEditSession(item);

        CustomItems.instance.getSession().put(player.getUniqueId(), session);
        CustomItems.instance.getItemGUI().openGUI(player, id);
    }

    @Subcommand("create <id>")
    public void createCommand(BukkitCommandActor actor, String id) {
        if (!actor.isPlayer()) {
            actor.sendRawMessage("You must be a player to edit an item!");
            return;
        }

        Player player = actor.asPlayer();
        if (player == null) {
            throw new IllegalStateException("Player is null after checking if it is a player!");
        }

        if (id == null || !id.matches("^[a-z_]+$")) {
            player.sendMessage(Component.text("Invalid key. Use a-z and underscores.", NamedTextColor.RED));
            return;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must hold an item to create an editor session.", NamedTextColor.RED));
            return;
        }

        ItemMeta meta = held.getItemMeta();

        Component name;
        if (meta != null && meta.hasDisplayName() && meta.displayName() != null) {
            name = meta.displayName();
        } else {
            name = held.effectiveName();
        }

        Material material = held.getType();
        ConcurrentHashMap<Enchantment, Integer> enchants = new ConcurrentHashMap<>(held.getEnchantments());

        List<Component> lore = meta != null && meta.hasLore() && meta.lore() != null
                ? new ArrayList<>(meta.lore())
                : new ArrayList<>();

        List<Float> cmd = meta != null && meta.hasCustomModelDataComponent()
                ? new ArrayList<>(meta.getCustomModelDataComponent().getFloats())
                : new ArrayList<>();

        boolean unbreakable = meta != null && meta.isUnbreakable();

        List<AbilityDefinition> abilities = getAbilitiesFromHeldItem(held);

        ItemEditSession session = new ItemEditSession(
                id,
                name,
                material,
                held.getAmount(),
                enchants,
                lore,
                cmd,
                unbreakable,
                abilities
        );

        CustomItems.instance.getSession().put(player.getUniqueId(), session);
        CustomItems.instance.getItemGUI().openGUI(player, id);
    }

    private List<AbilityDefinition> getAbilitiesFromHeldItem(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return new ArrayList<>();
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return new ArrayList<>();
        }

        String itemId = meta.getPersistentDataContainer().get(
                new NamespacedKey(CustomItems.instance, "custom_item_id"),
                PersistentDataType.STRING
        );

        if (itemId == null || itemId.isBlank()) {
            return new ArrayList<>();
        }

        CustomItem customItem = ConfigLoader.customItems.get(itemId);
        if (customItem == null || customItem.getAbilities() == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(customItem.getAbilities());
    }
}
