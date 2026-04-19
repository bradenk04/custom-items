package me.bradenk.customItems.abilities.impl;

import me.bradenk.customItems.abilities.AbilityContext;
import me.bradenk.customItems.abilities.AbilityDefinition;
import me.bradenk.customItems.abilities.CustomAbility;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Message implements CustomAbility {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    public String id() {
        return "message";
    }

    @Override
    public void execute(AbilityContext context, AbilityDefinition definition) {
        Object raw = definition.data().get("text");
        if (raw instanceof String text) {
            context.player().sendMessage(MINI_MESSAGE.deserialize(text));
        }
    }
}