package t.me.p1azmer.plugin.protectionblocks.region.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;

public class FlagListener extends AbstractListener<ProtectionPlugin> {
    private final RegionManager manager;

    public FlagListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler
    public void onCommandFlag(PlayerCommandPreprocessEvent event) {
    }
}