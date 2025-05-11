package dev.amraleth.afr.fishing;

import dev.amraleth.afr.AfoPlugin;
import dev.amraleth.afr.item.FishingAttribute;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Calculates the fishing rewards after a successful quicktime event
 *
 * @author amraleth
 */
@RequiredArgsConstructor
public class FishingReward {
    /**
     * The rod the quicktime event was completed with
     */
    private final ItemStack rodItemStack;

    /**
     * The player that completed the quicktime event
     */
    private final Player player;

    public void calculateFishingReward() {
        if (AfoPlugin.IS_DEBUG) {
            AfoPlugin.sendDebugMessage("Rewarding rod catch of {} with stats {}",
                    player.getName(),
                    FishingAttribute.getAttributesFromItem(rodItemStack));
        }
    }

}
