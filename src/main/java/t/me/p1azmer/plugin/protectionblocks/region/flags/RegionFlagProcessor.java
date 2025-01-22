package t.me.p1azmer.plugin.protectionblocks.region.flags;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RegionFlagProcessor<O, E extends Event> {

    void triggerFlag(@Nullable Player player, @Nullable O object, @NotNull E event);
}
