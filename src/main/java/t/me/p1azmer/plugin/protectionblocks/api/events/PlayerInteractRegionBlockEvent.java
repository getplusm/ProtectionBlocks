package t.me.p1azmer.plugin.protectionblocks.api.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

@Getter
public class PlayerInteractRegionBlockEvent extends RegionEvent {
    private final Player player;

    public PlayerInteractRegionBlockEvent(@NotNull Player player, @Nullable Region region) {
        super(region);
        this.player = player;
    }
}