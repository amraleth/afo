package dev.amraleth.afr.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A custom event triggered when a rod is reeled in
 *
 * @author amraleth
 */
@RequiredArgsConstructor
@Getter
public class ReelInEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     *  The player that reeled the rod in
     */
    private final @NotNull Player player;

    /**
     * The rod the player used, should be of type {@link org.bukkit.Material#FISHING_ROD}
     */
    private final @NotNull ItemStack rodItem;

    /**
     * The progress of the boss bar quick time event
     */
    private final float reelInProgress;

    /**
     * Contains a list of all fishing related boss bars this player is exposed to
     */
    private final @NotNull List<BossBar> bars;

    private boolean cancelled = false;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
