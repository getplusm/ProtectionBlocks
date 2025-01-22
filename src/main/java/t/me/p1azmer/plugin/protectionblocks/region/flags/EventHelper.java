package t.me.p1azmer.plugin.protectionblocks.region.flags;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;

public interface EventHelper<E extends Event, O> {

    boolean handle(@NotNull ProtectionPlugin plugin, @NotNull E event, @NotNull RegionFlagProcessor<O, E> processor);
}