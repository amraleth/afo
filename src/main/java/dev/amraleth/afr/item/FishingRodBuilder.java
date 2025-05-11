package dev.amraleth.afr.item;

import dev.amraleth.afr.AfrPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class FishingRodBuilder {
    private final @NotNull List<String> lore;
    private @NotNull RodRarity rodRarity;
    private @Nullable String name;
    private final @NotNull Map<FishingAttribute, Integer> attributes;

    public FishingRodBuilder() {
        this.lore = new ArrayList<>();
        this.rodRarity = RodRarity.COMMON;
        this.attributes = new HashMap<>();
    }

    public @NotNull FishingRodBuilder addLore(@NotNull List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public @NotNull FishingRodBuilder setRarity(@NotNull RodRarity rodRarity) {
        this.rodRarity = rodRarity;
        return this;
    }

    public @NotNull FishingRodBuilder setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    public @NotNull FishingRodBuilder addAttribute(@NotNull FishingAttribute fishingAttribute, int amount) {
        this.attributes.put(fishingAttribute, amount);
        return this;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(Material.FISHING_ROD);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (this.name != null) {
            itemMeta.displayName(AfrPlugin.MINI_MESSAGE.deserialize(
                    this.rodRarity.getColor() + this.name
            ));
        }

        List<Component> loreComponents = new ArrayList<>();
        loreComponents.add(AfrPlugin.MINI_MESSAGE.deserialize(this.rodRarity.getColor() + this.rodRarity.getName() + " Fishing Rod"));
        loreComponents.add(Component.text(" "));

        this.lore.forEach(line -> loreComponents.add(AfrPlugin.MINI_MESSAGE.deserialize(line)));

        loreComponents.add(Component.text(" "));

        this.attributes.forEach((attribute, value) -> loreComponents.add(AfrPlugin.MINI_MESSAGE.deserialize(
                "<blue>" + attribute.getName() + "<gray>: <gray>+" + value + (attribute.isPercent() ? "%" : "")
        )));

        itemMeta.lore(loreComponents);

        // todo: add all required

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // todo: add updating of rod items, like when adding enchants or power stones
}
