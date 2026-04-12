package me.bradenk.customItems.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TextUtils {

    public static List<Component> toComponentList(final String text) {
        final List<Component> components = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int charCount = 0;
        int wordCount = 0;

        for (String word : words) {
            if (wordCount >= 7 || charCount + word.length() > 35) {
                if (!currentLine.isEmpty()) {
                    String trimmed = currentLine.toString().trim();
                    if (trimmed.matches("\\p{Punct}")) {
                        int lastIndex = components.size() - 1;
                        if (lastIndex >= 0) {
                            Component last = components.remove(lastIndex);
                            String lastStr = PlainTextComponentSerializer.plainText().serialize(last);
                            last = Component.text(lastStr + trimmed);
                            components.add(last.color(NamedTextColor.DARK_GRAY));
                        }
                    } else {
                        components.add(Component.text(trimmed).color(NamedTextColor.DARK_GRAY));
                    }
                }
                currentLine = new StringBuilder();
                charCount = 0;
                wordCount = 0;
            }
            currentLine.append(word).append(" ");
            charCount += word.length() + 1;
            wordCount++;
        }
        if (!currentLine.isEmpty()) {
            components.add(Component.text(currentLine.toString().trim()).color(NamedTextColor.DARK_GRAY));
        }
        return components;
    }



    public static List<Component> enchantComponents(final ConcurrentHashMap<Enchantment, Integer> enchantments) {
        List<Component> components = new ArrayList<>();
        for (final Enchantment enchantment : enchantments.keySet()) {
            components.add(Component.text(enchantment.getKey().getKey()).color(NamedTextColor.YELLOW)
                    .append(Component.space()
                            .append(Component.text(enchantments.get(enchantment)).color(NamedTextColor.YELLOW))));
        }
        return components;
    }

    public static List<Component> attributeComponents(final ConcurrentHashMap<Attribute, Integer> attributes) {
        List<Component> components = new ArrayList<>();
        for (final Attribute attribute : attributes.keySet()) {
            components.add(Component.text(attribute.getKey().getKey()).color(NamedTextColor.YELLOW)
                    .append(Component.space()
                            .append(Component.text(attributes.get(attribute)).color(NamedTextColor.YELLOW))));
        }
        return components;
    }

}
