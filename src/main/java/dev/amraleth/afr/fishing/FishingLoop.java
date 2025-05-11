package dev.amraleth.afr.fishing;

import dev.amraleth.afr.AfrPlugin;
import dev.amraleth.afr.event.ReelInEvent;
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

@Getter
public class FishingLoop {
    private final Player player;
    private final ItemStack rodItemStack;
    private final AfrPlugin afrPlugin;
    private final long startedAt;

    private final BossBar bossBar;
    private final BossBar bossBarTwo;

    private BukkitRunnable runnable;
    private boolean increasing = true;
    private float progress = 0.0f;
    private boolean isActive = false;

    @Setter
    private boolean stopped = false;

    public FishingLoop(@NotNull AfrPlugin afrPlugin, @NotNull Player player, @NotNull ItemStack rodItemStack, long startedAt) {
        this.afrPlugin = afrPlugin;
        this.player = player;
        this.startedAt = startedAt;
        if (rodItemStack.getType() != Material.FISHING_ROD) {
            throw new IllegalArgumentException("Provided ItemStack is of invalid type. Found " + rodItemStack.getType() + ", but expected FISHING_ROD!");
        }
        this.rodItemStack = rodItemStack;
        this.bossBar = BossBar.bossBar(Component.text("Reel in when both bars meet"), 0.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        this.bossBarTwo = BossBar.bossBar(Component.text(""), 1.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }

    public void startFishingLoop() {
        if (this.isActive) return;

        AfrPlugin.sendDebugMessage("Starting fishing loop for player {}.", player.getName());
        player.showBossBar(this.bossBar);
        player.showBossBar(this.bossBarTwo);

        this.isActive = true;

        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    stopFishingLoop();
                    return;
                }
                if (increasing) {
                    progress += 0.02f;
                    if (progress >= 1.0f) {
                        progress = 1.0f;
                        increasing = false;
                    }
                } else {
                    progress -= 0.02f;
                    if (progress <= 0.0f) {
                        progress = 0.0f;
                        increasing = true;
                    }
                }
                if (progress >= 0.45f && progress <= 0.55f) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.2f);
                }
                bossBar.progress(progress);
                bossBarTwo.progress(1 - progress);
            }
        };

        this.runnable.runTaskTimer(this.afrPlugin, 0L, 2L);
    }

    public void reelRodIn() {
        ReelInEvent reelInEvent = new ReelInEvent(player, player.getActiveItem(), this.progress, List.of(this.bossBar, this.bossBarTwo));
        this.afrPlugin.getPluginManager().callEvent(reelInEvent);
        stopFishingLoop();
    }

    public void stopFishingLoop() {
        if (this.runnable != null) runnable.cancel();
        this.player.hideBossBar(this.bossBar);
        this.player.hideBossBar(this.bossBarTwo);
        this.isActive = false;
    }


}
