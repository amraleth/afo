package dev.amraleth.afr.item;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Contains all base rods without enchants and modifiers
 *
 * @author amraleth
 */
public class BaseRodItems {

    public static ItemStack STARTER_ROD() {
        return new FishingRodBuilder()
                .setName("Harald's Old Rod")
                .setRarity(RodRarity.COMMON)
                .addAttribute(FishingAttribute.FISHING_SPEED, 10)
                .addAttribute(FishingAttribute.TRESSURE_CHANCE, 2)
                .addAttribute(FishingAttribute.MULTI_CATCH, 1)
                .addLore(List.of(
                        "<gray><italic>\"One must learn to fish before sailing the seas\"",
                        " ",
                        "<gray>Crafted by the legendary fisherman Harald, this rod",
                        "<gray>teaches patience, precision and respect for the sea."
                ))
                .toItemStack();
    }
}
