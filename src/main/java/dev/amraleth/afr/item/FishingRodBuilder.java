package dev.amraleth.afr.item;

import dev.amraleth.afr.AfoPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for building rod related {@link ItemStack}s
 *
 * @author amraleth
 */
@Getter
public class FishingRodBuilder {
    public static final NamespacedKey NAMESPACE_AFR_ROD = new NamespacedKey(AfoPlugin.NAMESPACE, "afr_rod");

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
        itemMeta.setUnbreakable(true);

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(NAMESPACE_AFR_ROD, PersistentDataType.BOOLEAN, true);

        if (this.name != null) {
            itemMeta.displayName(AfoPlugin.MINI_MESSAGE.deserialize(
                    this.rodRarity.getColor() + this.name
            ));
        }

        List<Component> loreComponents = new ArrayList<>();
        loreComponents.add(AfoPlugin.MINI_MESSAGE.deserialize(this.rodRarity.getColor() + this.rodRarity.getName() + " Fishing Rod"));
        loreComponents.add(Component.text(" "));

        this.lore.forEach(line -> loreComponents.add(AfoPlugin.MINI_MESSAGE.deserialize(line)));

        loreComponents.add(Component.text(" "));

        this.attributes.forEach((attribute, value) -> {
            loreComponents.add(AfoPlugin.MINI_MESSAGE.deserialize(
                    "<green>" + attribute.getName() + "<gray>: <gray>+" + value + (attribute.isPercent() ? "%" : "")
            ));
            persistentDataContainer.set(attribute.getKey(), PersistentDataType.INTEGER, value);
        });

        // last line
        loreComponents.add(Component.text(" "));

        itemMeta.lore(loreComponents);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // todo: add updating of rod items, like when adding enchants or power stones

    public static boolean isAfrRod(@NotNull ItemStack itemStack) {
        if (itemStack.getType() != Material.FISHING_ROD) return false;
        if (!itemStack.hasItemMeta()) return false;
        return itemStack.getPersistentDataContainer().getOrDefault(NAMESPACE_AFR_ROD, PersistentDataType.BOOLEAN, false);
    }
}
