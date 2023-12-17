package t.me.p1azmer.plugin.protectionblocks.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;

import java.util.Optional;

public class BlockBreakRegionEvent extends RegionEvent {
    private final Player player;
    private final Object blockOrItem;
    private final Block targetBlock;
    private final RegionManager.DamageType damageType;

    public BlockBreakRegionEvent(@Nullable Player player, @Nullable Object blockOrItem,@NotNull Block targetBlock, @NotNull Region region, @NotNull RegionManager.DamageType damageType) {
        super(region);
        this.player = player;
        this.blockOrItem = blockOrItem;
        this.targetBlock = targetBlock;
        this.damageType = damageType;
    }

    @NotNull
    public RegionManager.DamageType getDamageType() {
        return damageType;
    }

    @NotNull
    public Optional<Object> getBlockOrItem() {
        return Optional.ofNullable(blockOrItem);
    }

    @NotNull
    public Block getTargetBlock() {
        return targetBlock;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

}