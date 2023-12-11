package t.me.p1azmer.plugin.protectionblocks.region.listener;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.events.BlockBreakRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.PlayerEnterRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.PlayerExitRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.Region;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerListener extends AbstractListener<ProtectionPlugin> {
    private final RegionManager manager;
    private final Map<Player, Region> playerRegionMap;

    public PlayerListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
        this.playerRegionMap = new ConcurrentHashMap<>();
    }


    @EventHandler
    public void updateRegionBlock(PlayerEnterRegionEvent event) {
        event.getRegion().ifPresent(region -> region.updateHologram(event.getPlayer(), true));
    }
    @EventHandler
    public void updateRegionBlock(PlayerExitRegionEvent event) {
        event.getRegion().ifPresent(region -> region.updateHologram(event.getPlayer(), false));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void detectRegions(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Region region = this.manager.getRegionByLocation(event.getTo());
        Region currentRegion = this.playerRegionMap.get(player);
        if (this.playerRegionMap.containsKey(player) && (region == null || !region.equals(currentRegion))) {
            PlayerExitRegionEvent calledEvent = new PlayerExitRegionEvent(player, currentRegion);
            this.plugin.getPluginManager().callEvent(calledEvent);
            if (!event.isCancelled()) {
                this.playerRegionMap.remove(player);
            }
        }
        if (region != null && !this.playerRegionMap.containsKey(player)) {
            PlayerEnterRegionEvent calledEvent = new PlayerEnterRegionEvent(player, region);
            this.plugin.getPluginManager().callEvent(calledEvent);
            if (!event.isCancelled()) {
                this.playerRegionMap.put(player, region);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getInteractionPoint() == null) return;

        Block block = event.getInteractionPoint().getBlock();
        Region region = this.manager.getRegionByBlock(block);
        if (region == null) return;
        event.setCancelled(!region.isAllowed(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegionBreak(BlockBreakRegionEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        Object tool = event.getBlockOrItem();
        Block block = event.getTargetBlock();

        if (block.getType().isAir() || !block.getType().isBlock()){
            return;
        }
        Region region = this.manager.getRegionByBlock(block);
        if (region == null) {
            return;
        }

        RegionManager.DamageType damageType = RegionManager.DamageType.HAND;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().isAir() && item.getType().isItem()) {
            damageType = RegionManager.DamageType.TOOLS;
        }


        event.setCancelled(!this.manager.tryDestroyRegion(player, tool, block, damageType, region));
        if (event.isCancelled()) {
            this.plugin.getMessage(Lang.REGION_ERROR_BREAK)
                    .replace(region.replacePlaceholders())
                    .send(player);
        }
    }

    @EventHandler
    public void onRegionEnter(PlayerEnterRegionEvent event) {
        event.getRegion().ifPresent(region ->
                plugin.getMessage(Lang.REGION_ENTER_NOTIFY)
                        .replace(region.replacePlaceholders())
                        .send(event.getPlayer())
        );
    }

    @EventHandler
    public void onRegionLeave(PlayerExitRegionEvent event) {
        event.getRegion().ifPresent(region ->
                plugin.getMessage(Lang.REGION_EXIT_NOTIFY)
                        .replace(region.replacePlaceholders())
                        .send(event.getPlayer())
        );
    }

    // -=-=-=- prevent smelting protection blocks -=-=-=-

//    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
//    public void onFurnaceSmelt(FurnaceSmeltEvent e) {
//        // prevent protect block item to b  e smelt
//        Region region = this.manager.getRegionByBlock(e.getBlock());
//        if (region != null && region.isAllowed(e.get)) {
//            e.setCancelled(true);
//        }
//    }

//    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
//    public void onFurnaceBurnItem(FurnaceBurnEvent e) {
//        // prevent protect block item to be smelt
//        Furnace f = (Furnace) e.getBlock().getState();
//        if (f.getInventory().getSmelting() != null) {
//            PSProtectBlock options = ProtectionStones.getBlockOptions(f.getInventory().getSmelting());
//            PSProtectBlock fuelOptions = ProtectionStones.getBlockOptions(f.getInventory().getFuel());
//            if ((options != null && !options.allowSmeltItem) || (fuelOptions != null && !fuelOptions.allowSmeltItem)) {
//                e.setCancelled(true);
//            }
//        }
//    }


    // -=-=-=- block changes to protection block related events -=-=-=-

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketEmptyEvent event) {
        Block clicked = event.getBlockClicked();
        BlockFace bf = event.getBlockFace();
        Block check = clicked.getWorld().getBlockAt(clicked.getX() + event.getBlockFace().getModX(), clicked.getY() + bf.getModY(), clicked.getZ() + event.getBlockFace().getModZ());
        Region region = this.manager.getRegionByBlock(check);
        if (region != null && !region.isAllowed(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() == null) return;

        Region region = this.manager.getRegionByBlock(event.getBlock());
        if (region != null && !region.isAllowed(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
