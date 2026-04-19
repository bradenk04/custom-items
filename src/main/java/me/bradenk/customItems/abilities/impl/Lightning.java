package me.bradenk.customItems.abilities.impl;

import me.bradenk.customItems.abilities.AbilityContext;
import me.bradenk.customItems.abilities.AbilityDefinition;
import me.bradenk.customItems.abilities.CustomAbility;
import org.bukkit.Location;

public class Lightning implements CustomAbility {
    @Override
    public String id() {
        return "lightning";
    }

    @Override
    public void execute(AbilityContext context, AbilityDefinition definition) {
        Location loc = context.player().getTargetBlockExact(50) != null
                ? context.player().getTargetBlockExact(50).getLocation()
                : context.player().getLocation();

        context.player().getWorld().strikeLightning(loc);
    }
}