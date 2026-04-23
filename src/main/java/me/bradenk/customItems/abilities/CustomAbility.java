package me.bradenk.customItems.abilities;

public interface CustomAbility {
    String id();
    void execute(AbilityContext context, AbilityDefinition definition);
}