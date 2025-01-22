package t.me.p1azmer.plugin.protectionblocks.api.events;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

import java.util.Optional;

@RequiredArgsConstructor
class RegionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Region region;
    private boolean cancelled = false;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public Optional<Region> getRegion() {
        return Optional.ofNullable(this.region);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}