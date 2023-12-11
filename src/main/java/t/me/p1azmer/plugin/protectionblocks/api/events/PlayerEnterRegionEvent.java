package t.me.p1azmer.plugin.protectionblocks.api.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.protectionblocks.region.Region;


public class PlayerEnterRegionEvent extends RegionEvent {
    private final Player player;

    public PlayerEnterRegionEvent(@NotNull Player player, @NotNull Region region) {
        super(region);
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }
}