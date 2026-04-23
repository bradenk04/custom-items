package me.bradenk.customItems.abilities;

import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {
    private final Map<String, CustomAbility> abilities = new HashMap<>();

    public void register(CustomAbility ability) {
        abilities.put(ability.id().toLowerCase(), ability);
    }

    public CustomAbility get(String id) {
        if (id == null) return null;
        return abilities.get(id.toLowerCase());
    }
}