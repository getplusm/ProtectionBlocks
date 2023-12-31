package t.me.p1azmer.plugin.protectionblocks.region.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.events.BlockBreakRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.BlockPlaceRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.RegionBlock;
import t.me.p1azmer.plugin.protectionblocks.utils.Cuboid;

import java.util.concurrent.CompletableFuture;

public class RegionListener extends AbstractListener<ProtectionPlugin> {
    private final RegionManager manager;

    public RegionListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Block block = event.getBlockPlaced();
        RegionBlock regionBlock = this.manager.getRegionBlockByItem(item);
        Region region = this.manager.getRegionByBlock(block);

        if (region != null) {
            if (regionBlock != null){
                event.setCancelled(true);
                return;
            }
            BlockPlaceRegionEvent calledEvent = new BlockPlaceRegionEvent(event.getPlayer(), block, region);
            this.plugin.getPluginManager().callEvent(calledEvent);
            event.setCancelled(calledEvent.isCancelled() || !this.manager.tryBlockPlaceRegion(event.getPlayer(), region));
            return;
        }
        if (regionBlock == null) return;

        // Check for nearby regions
        Cuboid preCuboid = new Cuboid(block.getLocation().clone().add(-regionBlock.getRegionSize(), -regionBlock.getRegionSize(), -regionBlock.getRegionSize()), block.getLocation().clone().add(regionBlock.getRegionSize(), regionBlock.getRegionSize(), regionBlock.getRegionSize()));
        if (preCuboid.getBlocks().stream().anyMatch(this.manager::isProtectedBlock)) {
            plugin.getMessage(Lang.REGION_ERROR_CREATED_NEARBY_RG).send(event.getPlayer());
            event.setCancelled(true);
            return;
        }
            event.setBuild(this.manager.tryCreateRegion(event.getPlayer(), block, item, regionBlock));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Region region = this.manager.getRegionByLocation(block.getLocation());
        if (region == null) return;

        RegionManager.DamageType damageType = RegionManager.DamageType.HAND;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isItem()) {
            damageType = RegionManager.DamageType.TOOLS;
        }

        BlockBreakRegionEvent calledEvent = new BlockBreakRegionEvent(player, item, block, region, damageType);
        this.plugin.getPluginManager().callEvent(calledEvent);
        event.setCancelled(calledEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        Block block = event.getBlock();

        event.blockList().removeIf(targetBlock -> {
            Region region = this.manager.getRegionByBlock(targetBlock);
            if (region == null) return false;
            return this.manager.tryDamageRegion(null, block, targetBlock, region, RegionManager.DamageType.EXPLODE, true);
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        Block block = event.getLocation().getBlock();

        event.blockList().removeIf(targetBlock -> {
            Region region = this.manager.getRegionByBlock(targetBlock);
            if (region == null) return false;
            return this.manager.tryDamageRegion(null, block, targetBlock, region, RegionManager.DamageType.EXPLODE, true);
        });
    }
}

