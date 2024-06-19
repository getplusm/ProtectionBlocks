package t.me.p1azmer.plugin.protectionblocks.api.events;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.DamageType;

@Getter
public class BlockBreakRegionEvent extends RegionEvent {
    private final Player player;
    private final Object blockOrItem;
    private final Block targetBlock;
    private final DamageType damageType;

    public BlockBreakRegionEvent(@Nullable Player player, @Nullable Object blockOrItem, @NotNull Block targetBlock, @NotNull Region region, @NotNull DamageType damageType) {
        super(region);
        this.player = player;
        this.blockOrItem = blockOrItem;
        this.targetBlock = targetBlock;
        this.damageType = damageType;
    }
}