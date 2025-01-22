package t.me.p1azmer.plugin.protectionblocks.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

/**
 * Called before deleting a region.
 * If you cancel the event, the region will not be created
 */
@Getter
@Setter
public class AsyncDeletedRegionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Region region;
    private boolean cancelled = false;
    private boolean notifyIfCancelled = true;

    public AsyncDeletedRegionEvent(@NotNull Region region) {
        super(true);
        this.region = region;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}