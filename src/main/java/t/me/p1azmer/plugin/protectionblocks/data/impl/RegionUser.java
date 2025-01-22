package t.me.p1azmer.plugin.protectionblocks.data.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.data.AbstractUser;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegionUser extends AbstractUser<ProtectionPlugin> {
    Map<String, String> regions; // region id, region block id

    public RegionUser(@NotNull ProtectionPlugin plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(), new HashMap<>());
    }

    public RegionUser(@NotNull ProtectionPlugin plugin,
                      @NotNull UUID uuid, @NotNull String name,
                      long dateCreated, long lastLogin,
                      @NotNull Map<String, String> regions) {
        super(plugin, uuid, name, dateCreated, lastLogin);
        this.setRegions(regions);
    }

    public void addRegion(@NotNull Region region, @NotNull RegionBlock regionBlock) {
        this.addRegion(region.getId(), regionBlock.getId());
    }

    public void addRegion(@NotNull String regionId, @NotNull String regionBlockId) {
        this.regions.put(regionId.toLowerCase(), regionBlockId.toLowerCase());
    }

    public void removeRegion(@NotNull Region region) {
        this.regions.remove(region.getId(), region.getRegionBlockId());
    }

    public long getAmountOf(@NotNull RegionBlock regionBlock) {
        return this.regions.values().stream().filter(founder -> regionBlock.getId().equals(founder.toLowerCase())).count();
    }

    public long getRegionsAmount() {
        return this.regions.size();
    }
}