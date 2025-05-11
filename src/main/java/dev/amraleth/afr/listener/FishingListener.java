package dev.amraleth.afr.listener;

import dev.amraleth.afr.AfrPlugin;
import dev.amraleth.afr.event.ReelInEvent;
import dev.amraleth.afr.fishing.FishingLoop;
import dev.amraleth.afr.fishing.FishingReward;
import dev.amraleth.afr.item.BaseRodItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Main class to listen for events regarding the usage of a fishing rod
 *
 * @author amraleth
 * @since 1.0-alpha
 */
public class FishingListener implements Listener {
    private final Map<UUID, FishingLoop> fishingLoops;

    private final AfrPlugin afrPlugin;

    public FishingListener(@NotNull AfrPlugin afrPlugin) {
        this.afrPlugin = afrPlugin;
        this.fishingLoops = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFish(@NotNull PlayerFishEvent event) {
        PlayerFishEvent.State eventState = event.getState();
        Player caster = event.getPlayer();

        switch (eventState) {
            case FISHING:
                if (caster.getInventory().getItemInMainHand().getType() != Material.FISHING_ROD) return;

                if (this.fishingLoops.containsKey(caster.getUniqueId())) return;

                ItemStack fishingRod = caster.getInventory().getItemInMainHand();
                UUID casterUUID = caster.getUniqueId();

                FishingLoop fishingLoop = new FishingLoop(this.afrPlugin, caster, fishingRod, System.currentTimeMillis());
                this.fishingLoops.put(casterUUID, fishingLoop);

                caster.setCooldown(Material.FISHING_ROD, 20);

                Bukkit.getScheduler().runTaskLater(this.afrPlugin, () -> {
                    if (!this.fishingLoops.containsKey(casterUUID)) return;
                    FishHook hook = event.getHook();
                    Block blockBelow = hook.getLocation().subtract(0, 0.3, 0).getBlock();

                    if (!blockBelow.isLiquid() || blockBelow.getType() != Material.WATER || fishingLoop.isStopped()) {
                        event.setCancelled(true);
                        hook.remove();
                        this.fishingLoops.remove(casterUUID);
                        return;
                    }

                    fishingLoop.startFishingLoop();
                }, 10L);
                break;
            case BITE:
            case CAUGHT_FISH:
            case CAUGHT_ENTITY:
            case REEL_IN:
                break;
        }
    }

    @EventHandler
    public void onRodReel(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack rod = event.getItem();

        if (rod == null || rod.getType() != Material.FISHING_ROD || player.hasCooldown(Material.FISHING_ROD)) return;

        UUID casterUUID = player.getUniqueId();

        if (!this.fishingLoops.containsKey(casterUUID)) return;

        FishingLoop fishingLoop = this.fishingLoops.get(casterUUID);
        if (fishingLoop == null) return;

        this.fishingLoops.remove(casterUUID);
        fishingLoop.reelRodIn();
    }


    @EventHandler
    public void onItemSwitch(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        if (!this.fishingLoops.containsKey(player.getUniqueId())) return;
        FishingLoop fishingLoop = this.fishingLoops.get(player.getUniqueId());
        if (fishingLoop == null || !fishingLoop.isActive()) return;

        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (newItem == null || newItem != fishingLoop.getRodItemStack()) {
            this.fishingLoops.remove(player.getUniqueId());
            fishingLoop.stopFishingLoop();
        }
    }

    @EventHandler
    public void onReelIn(@NotNull ReelInEvent event) {
        float progress = event.getReelInProgress();
        Player player = event.getPlayer();

        // todo: do actual loot calculation
        if (progress >= 0.45f && progress <= 0.55f) {
            FishingReward fishingReward = new FishingReward(event.getRodItem(), player);
            fishingReward.calculateFishingReward();
        } else {
            player.sendMessage(Component.text("Missed catch!", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent playerJoinEvent) {
        playerJoinEvent.getPlayer().getInventory().setItem(1, BaseRodItems.STARTER_ROD());
    }
}
