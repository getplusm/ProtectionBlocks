package t.me.p1azmer.plugin.protectionblocks.api.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.protectionblocks.region.Region;


public class PlayerExitRegionEvent extends RegionEvent {
    private final Player player;

    public PlayerExitRegionEvent(@NotNull Player player, @Nullable Region region) {
        super(region);
        this.player = player;
    }


    @NotNull
    public Player getPlayer() {
        return player;
    }

}