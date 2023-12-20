package t.me.p1azmer.plugin.protectionblocks.data;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.data.AbstractUserManager;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.data.impl.RegionUser;

import java.util.UUID;

public class UserManager extends AbstractUserManager<ProtectionPlugin, RegionUser> {

    public UserManager(@NotNull ProtectionPlugin plugin) {
        super(plugin, plugin);
    }

    @Override
    @NotNull
    protected RegionUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new RegionUser(this.plugin, uuid, name);
    }
}