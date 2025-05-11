package dev.amraleth.afr.item;

import dev.amraleth.afr.AfrPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents different types of attributes a rod or armor may add
 *
 * @author amraleth
 */
@RequiredArgsConstructor
@Getter
public enum FishingAttribute {
    /**
     * Decreases the duration of the quicktime event
     */
    FISHING_SPEED(new NamespacedKey(AfrPlugin.NAMESPACE, "attribute_fishing_speed"), "Fishing Speed", false),

    /**
     * Increases the chance for rare creatures to spawn
     */
    LURE_OF_THE_DEEP(new NamespacedKey(AfrPlugin.NAMESPACE, "attribute_lure_of_the_deep"), "Lure of the Deep", false),

    /**
     * Increases the odds to finding rare sea creatures
     */
    TRESSURE_CHANCE(new NamespacedKey(AfrPlugin.NAMESPACE, "attribute_tressure_chance"), "Tressure Chance", true),

    /**
     * Increases the odds to catch two times with one hook
     */
    MULTI_CATCH(new NamespacedKey(AfrPlugin.NAMESPACE, "attribute_multi_catch"), "Multicatch", true);

    /**
     * The key under which the attribute is stored
     */
    private final NamespacedKey key;

    /**
     * The human-readable name of the attribute
     */
    private final String name;

    /**
     * Weather or not this attribute should be displayed as a percentage
     */
    private final boolean percent;

    /**
     * Gets all attributes from an {@link ItemStack}
     *
     * @param itemStack The ItemStack to get attributes from
     * @return An unmodifiable map of all available attributes in the form of attribute <-> value pairs. If an attribute
     * was not found on the item, 0 is returned for it.
     */
    public static @Nullable @Unmodifiable Map<FishingAttribute, Integer> getAttributesFromItem(@NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;
        PersistentDataContainer persistentDataContainer = itemStack.getItemMeta().getPersistentDataContainer();

        Map<FishingAttribute, Integer> attributes = new HashMap<>();
        Arrays.stream(values()).forEach(value -> {
            attributes.put(value, persistentDataContainer.getOrDefault(value.key, PersistentDataType.INTEGER, 0));
        });
        return attributes;
    }

    /**
     * Applies a map of attributes to a given {@link ItemStack}
     *
     * @param itemStack         The ItemStack to apply the attributes to
     * @param fishingAttributes An attribute <-> value map
     * @return The finished ItemStack
     */
    public static @NotNull ItemStack applyAttributes(@NotNull ItemStack itemStack, @NotNull Map<FishingAttribute, Integer> fishingAttributes) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        fishingAttributes.forEach((attribute, value) -> {
            int finalAttributeValue = value;
            if (container.has(attribute.getKey())) {
                finalAttributeValue += container.getOrDefault(attribute.getKey(), PersistentDataType.INTEGER, 0);
            }
            container.set(attribute.getKey(), PersistentDataType.INTEGER, finalAttributeValue);
        });
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
