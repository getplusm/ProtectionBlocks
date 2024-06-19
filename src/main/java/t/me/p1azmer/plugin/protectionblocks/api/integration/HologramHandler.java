package t.me.p1azmer.plugin.protectionblocks.api.integration;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.Loadable;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

public interface HologramHandler extends Loadable {
    void create(@NotNull Region region);

    void delete(@NotNull Region region);

    void show(@NotNull Region region, @NotNull Player player);

    void hide(@NotNull Region region, @NotNull Player player);
}