package dev.amraleth.afo.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the different rarities a rod can have
 *
 * @author amraleth
 */
@RequiredArgsConstructor
@Getter
public enum RodRarity {
    COMMON("<gray>", "Common", 1),
    RARE("<green>", "Rare", 2),
    LEGENDARY("<gold>", "Legendary", 3),
    ANCIENT("<red>", "Ancient", 4);

    private final String color;
    private final String name;
    private final int identifier;

}
