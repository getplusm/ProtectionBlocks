package t.me.p1azmer.plugin.protectionblocks.region.listener;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.events.BlockBreakRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.BlockPlaceRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.region.Region;
import t.me.p1azmer.plugin.protectionblocks.region.RegionBlock;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RegionListener extends AbstractListener<ProtectionPlugin> {
    private final RegionManager manager;

    public RegionListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (event.getBlockPlaced().getType().isAir() || !event.getBlockPlaced().getType().isBlock() || item.getType().isAir())
            return;
        Block block = event.getBlockPlaced();
        RegionBlock regionBlock = this.manager.getRegionBlockByItem(item);
        if (regionBlock == null) return;

        Region region = this.manager.getRegionByBlock(block);
        if (region != null) {
            BlockPlaceRegionEvent calledEvent = new BlockPlaceRegionEvent(event.getPlayer(), block, region);
            this.plugin.getPluginManager().callEvent(calledEvent);
            event.setCancelled(calledEvent.isCancelled());
            if (!event.isCancelled()) {
                event.setCancelled(!this.manager.tryBlockPlaceRegion(event.getPlayer(), block, region));
            }
            return;
        }
        CompletableFuture.runAsync(() -> this.manager.tryCreateRegion(event.getPlayer(), block, item)); // async create
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Region region = this.manager.getRegionByBlock(block);
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

    @EventHandler
    public void onExplode(BlockExplodeEvent event) {
        if (event.getExplodedBlockState() == null) return;

        if (event.blockList().stream().anyMatch(block -> this.manager.getRegionByBlock(block) != null)) {
            Block block = event.blockList().stream()
                    .filter(f -> this.manager.getRegionByBlock(f) != null)
                    .findFirst().orElse(null);
            if (block == null) return;

            Region region = this.manager.getRegionByBlock(block);
            if (region == null) return;

            event.setCancelled(!this.manager.tryDestroyRegion(null, event.getExplodedBlockState().getBlock(), block, RegionManager.DamageType.EXPLODE, region));
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Block explodeBlock = event.getLocation().getBlock();
        if (explodeBlock.getType().isAir()) return;

        if (event.blockList().stream().anyMatch(block -> this.manager.getRegionByBlock(block) != null)) {
            Block block = event.blockList().stream()
                    .filter(f -> this.manager.getRegionByBlock(f) != null)
                    .findFirst().orElse(null);
            if (block == null) return;

            Region region = this.manager.getRegionByBlock(block);
            if (region == null) return;

            event.setCancelled(!this.manager.tryDestroyRegion(null, explodeBlock, block, RegionManager.DamageType.EXPLODE, region));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Region region = this.manager.getRegionByBlock(event.getBlock());
        if (region != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Region region = this.manager.getRegionByBlock(event.getToBlock());
        if (region != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        Region region = this.manager.getRegionByBlock(event.getBlock());
        if (region != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        Region region = this.manager.getRegionByBlock(event.getBlock());
        if (region != null) {
            event.setCancelled(true);
        }
    }

    // -=-=- prevent protection block piston effects -=-=-

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        pistonUtil(event.getBlocks(), event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {

        pistonUtil(event.getBlocks(), event);
    }

    private void pistonUtil(@NotNull List<Block> pushedBlocks, @NotNull BlockPistonEvent event) {
        for (Block block : pushedBlocks) {
            Region region = this.manager.getRegionByBlock(block);
            if (region != null) {
                event.setCancelled(true);
            }
        }
    }

    // -=-=- prevent protection blocks from exploding -=-=-

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        explodeUtil(event.blockList(), event.getBlock().getLocation().getWorld());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        explodeUtil(event.blockList(), event.getLocation().getWorld());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (!this.manager.isProtectedBlock(event.getBlock())) return;

        // events like ender dragon block break, wither running into block break, etc.
        if (!blockExplodeUtil(event.getBlock().getWorld(), event.getBlock())) {
            // if block shouldn't be exploded, cancel the event
            event.setCancelled(true);
        }
    }

    private void explodeUtil(@NotNull List<Block> blockList, @NotNull World world) {
        // loop through exploded blocks
        for (int i = 0; i < blockList.size(); i++) {
            Block b = blockList.get(i);

            if (this.manager.isProtectedBlock(b)) {
                // always remove protection block from exploded list
                blockList.remove(i);
                i--;
            }

            blockExplodeUtil(world, b);
        }
    }

    // returns whether the block is exploded
    private boolean blockExplodeUtil(@NotNull World world, @NotNull Block block) {
        if (this.manager.isProtectedBlock(block)) {
            Region region = this.manager.getRegionByBlock(block);
            if (region == null) return false;

            this.manager.tryDamageRegion(null, block, block, region, RegionManager.DamageType.EXPLODE, true);

            // manually add drop
//            if (!blockOptions.noDrop) {
//                block.getWorld().dropItem(block.getLocation(), blockOptions.createItem());
//            }
        }
        return true;
    }
}

