package me.bradenk.customItems.abilities.impl;

import me.bradenk.customItems.CustomItems;
import me.bradenk.customItems.abilities.AbilityContext;
import me.bradenk.customItems.abilities.AbilityDefinition;
import me.bradenk.customItems.abilities.CustomAbility;
import me.bradenk.customItems.config.ConfigLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;

public class Message implements CustomAbility {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @Override
    public String id() {
        return "message";
    }

    @Override
    public void execute(AbilityContext context, AbilityDefinition definition) {
        Optional<String> text = ConfigLoader.getAbilitiesConfig().getOptional("abilities.message.text");
        text.ifPresent(s -> context.player().sendMessage(MiniMessage.miniMessage().deserialize(s)));
    }
}