package t.me.p1azmer.plugin.protectionblocks.api.events;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

@Getter
public class BlockPlaceRegionEvent extends RegionEvent {
    private final Player player;
    private final Block block;

    public BlockPlaceRegionEvent(@Nullable Player player, @NotNull Block block, @NotNull Region region) {
        super(region);
        this.player = player;
        this.block = block;
    }
}