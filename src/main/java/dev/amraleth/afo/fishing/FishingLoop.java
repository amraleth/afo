package dev.amraleth.afo.fishing;

import dev.amraleth.afo.AfoPlugin;
import dev.amraleth.afo.event.ReelInEvent;
import dev.amraleth.afo.item.FishingAttribute;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Represents a fishing loop that happens after a rod has been cast
 *
 * @author amraleth
 */
@Getter
public class FishingLoop {
    /**
     * The player that cast the fishing rod
     */
    private final Player player;

    /**
     * The item of the fishing rod
     */
    private final ItemStack rodItemStack;

    /**
     * An instance of the main class, used for registering schedulers and calling events
     */
    private final AfoPlugin afoPlugin;

    /**
     * When the loop was created, not started (!)
     */
    private final long startedAt;

    // two bars for the quicktime event
    private final BossBar bossBar;
    private final BossBar bossBarTwo;

    /**
     * The runnable that runs the quicktime animation
     */
    private BukkitRunnable runnable;

    // minor things used for calculating the current quicktime progress
    private boolean increasing = true;
    private float progress = 0.0f;

    /**
     * If the loop is running
     */
    private boolean isActive = false;

    /**
     * If the loop was stopped because of a player reeling the rod in before the ten tick waiting time
     */
    @Setter
    private boolean stopped = false;

    /**
     * @param afoPlugin    An instance of the main plugin
     * @param player       The player to cast this loop for
     * @param rodItemStack The rod item stack
     * @param startedAt    When the loop was created
     */
    public FishingLoop(@NotNull AfoPlugin afoPlugin, @NotNull Player player, @NotNull ItemStack rodItemStack, long startedAt) {
        this.afoPlugin = afoPlugin;
        this.player = player;
        this.startedAt = startedAt;
        if (rodItemStack.getType() != Material.FISHING_ROD) {
            throw new IllegalArgumentException("Provided ItemStack is of invalid type. Found " + rodItemStack.getType() + ", but expected FISHING_ROD!");
        }
        this.rodItemStack = rodItemStack;
        this.bossBar = BossBar.bossBar(Component.text("Reel in when both bars meet"), 0.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        this.bossBarTwo = BossBar.bossBar(Component.text(""), 1.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    /**
     * Starts a new fishing loop and quicktime animation
     */
    public void startFishingLoop() {
        if (this.isActive) return;

        player.showBossBar(this.bossBar);
        player.showBossBar(this.bossBarTwo);

        this.isActive = true;

        Map<FishingAttribute, Integer> attributes = FishingAttribute.getAttributesFromItem(this.rodItemStack);

        int fishingSpeed = 0;
        if (attributes.containsKey(FishingAttribute.FISHING_SPEED)) {
            fishingSpeed += attributes.get(FishingAttribute.FISHING_SPEED);
        }

        // linear scaling from 10s to 2.5s over 0 - 500 fishing speed
        double timeToFull = 10.0 - (Math.min(fishingSpeed, 500) / 500.0) * 7.5;

        float progressDelta = (float)(0.1 / timeToFull);

        AfoPlugin.sendDebugMessage("Player {} is fishing with timeToFull {} and progressDelta {}.",
                this.player.getName(), timeToFull, progressDelta);

        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    stopFishingLoop();
                    return;
                }

                if (increasing) {
                    progress += progressDelta;
                    if (progress >= 1.0f) {
                        progress = 1.0f;
                        increasing = false;
                    }
                } else {
                    progress -= progressDelta;
                    if (progress <= 0.0f) {
                        progress = 0.0f;
                        increasing = true;
                    }
                }

                if (progress >= 0.45f && progress <= 0.55f) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.2f);
                }

                bossBar.progress(progress);
                bossBarTwo.progress(1.0f - progress);
            }
        };

        this.runnable.runTaskTimer(this.afoPlugin, 0L, 2L); // every 2 ticks = 0.1s
    }

    /**
     * Used for reeling the rod in, triggers the {@link ReelInEvent} for calculating loot
     */
    public void reelRodIn() {
        ReelInEvent reelInEvent = new ReelInEvent(player, player.getInventory().getItemInMainHand(), this.progress, List.of(this.bossBar, this.bossBarTwo));
        this.afoPlugin.getPluginManager().callEvent(reelInEvent);
        stopFishingLoop();
    }

    /**
     * Stops the fishing loop
     */
    public void stopFishingLoop() {
        if (this.runnable != null) runnable.cancel();
        this.player.hideBossBar(this.bossBar);
        this.player.hideBossBar(this.bossBarTwo);
        this.isActive = false;
    }


}
