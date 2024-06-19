package t.me.p1azmer.plugin.protectionblocks.region.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.events.BlockBreakRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.BlockPlaceRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.DamageType;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;
import t.me.p1azmer.plugin.protectionblocks.utils.Cuboid;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegionListener extends AbstractListener<ProtectionPlugin> {
    // simple cache for checking repeated blocks to reduce calculations
    private final AutoRemovalCollection<Location> notAllowedPlaceCache = AutoRemovalCollection.newHashSet(1, TimeUnit.MINUTES);
    private final RegionManager manager;

    public RegionListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Block block = event.getBlockPlaced();
        Location blockLocation = block.getLocation();
        RegionBlock regionBlock = this.manager.getRegionBlockByItem(item);
        Region region = this.manager.getRegionByBlock(block);
        Player player = event.getPlayer();

        if (region != null) {
            if (regionBlock != null) {
                event.setCancelled(true);
                return;
            }
            BlockPlaceRegionEvent calledEvent = new BlockPlaceRegionEvent(player, block, region);
            this.plugin.getPluginManager().callEvent(calledEvent);
            boolean isAllowedToPlace = this.manager.tryBlockPlaceRegion(player, region);
            if (calledEvent.isCancelled() || !isAllowedToPlace) {
                event.setCancelled(true);
            }
            return;
        }
        if (regionBlock == null) return;

        if (notAllowedPlaceCache.contains(blockLocation)) {
            plugin.getMessage(Lang.REGION_ERROR_CREATED_NEARBY_RG).send(player);
            event.setCancelled(true);
            return;
        }
        // Check for nearby regions
        int regionSize = regionBlock.getRegionSize();
        Location firstLocation = blockLocation.add(-regionSize, -regionSize, -regionSize);
        Location secondLocation = blockLocation.add(regionSize, regionSize, regionSize);
        Cuboid preCuboid = new Cuboid(blockLocation, firstLocation, secondLocation, regionBlock.isInfinityYBlocks());

        if (preCuboid.getBlocks().stream().anyMatch(this.manager::isProtectedBlock)) {
            plugin.getMessage(Lang.REGION_ERROR_CREATED_NEARBY_RG).send(player);
            event.setCancelled(true);
            this.notAllowedPlaceCache.add(blockLocation);
            return;
        }
        event.setBuild(this.manager.tryCreateRegion(player, block, item, regionBlock));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Region region = this.manager.getRegionByLocation(block.getLocation());
        if (region == null) return;

        DamageType damageType = DamageType.HAND;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isItem()) {
            damageType = DamageType.TOOLS;
        }

        BlockBreakRegionEvent calledEvent = new BlockBreakRegionEvent(player, item, block, region, damageType);
        this.plugin.getPluginManager().callEvent(calledEvent);
        event.setCancelled(calledEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        Block block;
        if (Version.isBehind(Version.V1_19_R2)) {
            block = event.getBlock();
        } else {
            if (event.getExplodedBlockState() == null) return;

            block = event.getExplodedBlockState().getBlock();
        }

        handleRegionExplode(event.blockList(), block);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        handleRegionExplode(event.blockList(), null);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() == null) return;

        Region region = this.manager.getRegionByBlock(event.getBlock());
        if (region != null && !region.isAllowed(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        boolean result = this.handlePiston(event.getBlocks(), event.getBlock(), event.getDirection());
        if (result)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        boolean result = this.handlePiston(event.getBlocks(), event.getBlock(), event.getDirection());
        if (result)
            event.setCancelled(true);
    }

    private void handleRegionExplode(@NotNull List<Block> blockList, @Nullable Block block) {
        DamageType damageType = DamageType.EXPLODE;
        blockList.removeIf(destroyedBlock -> {
            Region region = this.manager.getRegionByBlock(destroyedBlock);
            if (region == null) return false;

            // the result is inverted because it returns true if it has received damage,
            // and here the result of true is removed from the list
            return !this.manager.tryDamageRegion(block, destroyedBlock, region, damageType, true);
        });
    }

    private boolean handlePiston(@NotNull List<Block> blockList, @NotNull Block pistonBlock, @NotNull BlockFace direction) {
        Location retractLocation = pistonBlock.getRelative(direction, blockList.size()).getLocation();
        Region region = this.manager.getRegionByLocation(retractLocation);
        if (region != null) return true;

        for (Block block : blockList) {
            region = this.manager.getRegionByBlock(block);
            if (region == null) continue;

            return true;
        }
        return false;
    }
}