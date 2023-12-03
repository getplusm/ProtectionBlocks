package t.me.p1azmer.plugin.protectionstones.region.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.protectionstones.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionstones.region.Region;
import t.me.p1azmer.plugin.protectionstones.region.RegionBlock;
import t.me.p1azmer.plugin.protectionstones.region.RegionManager;

import java.util.concurrent.CompletableFuture;

public class RegionListener extends AbstractListener<ProtectionPlugin> {
    private final RegionManager manager;

    public RegionListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType().isAir() || !event.getBlockPlaced().getType().isBlock()) return;
        Block block = event.getBlockPlaced();
        RegionBlock regionBlock = this.manager.getRegionBlockByMaterial(block.getType());
        if (regionBlock == null) return;
        Region region = this.manager.getRegionByBlock(block);
        if (region != null) return;
        CompletableFuture.runAsync(() -> this.manager.tryCreateRegion(event.getPlayer(), block)); // async create
    }
}

