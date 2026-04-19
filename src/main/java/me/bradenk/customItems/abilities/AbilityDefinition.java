package me.bradenk.customItems.abilities;

import com.electronwill.nightconfig.core.CommentedConfig;

public record AbilityDefinition(
        String type,
        AbilityTrigger trigger,
        CommentedConfig data
) {}