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

import java.util.Map;


@RequiredArgsConstructor
@Getter
public enum FishingAttribute {
    FISHING_SPEED(new NamespacedKey(AfrPlugin.NAMESPACE, "attribute_fishing_speed"), "Fishing Speed", false);

    private final NamespacedKey key;
    private final String name;
    private final boolean percent;

    private static int getAttribute(@NotNull NamespacedKey namespacedKey, @NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return 0;
        return itemStack.getItemMeta().getPersistentDataContainer().getOrDefault(namespacedKey, PersistentDataType.INTEGER, 0);
    }

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
