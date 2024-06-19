package t.me.p1azmer.plugin.protectionblocks.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called before creating a region.
 * If you cancel the event, the region will not be created
 */
@Getter
@Setter
public class AsyncCreatedRegionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Block block;
    private boolean notifyIfCancelled = true;

    public AsyncCreatedRegionEvent(@NotNull Block block) {
        super(true);
        this.block = block;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}