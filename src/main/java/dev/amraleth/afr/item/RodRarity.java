package dev.amraleth.afr.item;

import dev.amraleth.afr.AfrPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;

@RequiredArgsConstructor
@Getter
public enum RodRarity {
    COMMON("<gray>", "Common", 1),
    RARE("<green>", "Rare", 2),
    LEGENDARY("<gold>", "Legendary", 3),
    ANCIENT("<red>", "Ancient", 4);

    public static final NamespacedKey NAMESPACE_ROD_RARITY = new NamespacedKey(AfrPlugin.NAMESPACE, "rod_rarity");

    private final String color;
    private final String name;
    private final int identifier;

}
