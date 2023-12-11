package t.me.p1azmer.plugin.protectionblocks.api.events;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class CreateRegionEvent extends RegionEvent {

    private final Block block;
    public CreateRegionEvent(@NotNull Block block) {
        super(null);
        this.block = block;
    }

    @NotNull
    public Block getBlock() {
        return block;
    }
}
