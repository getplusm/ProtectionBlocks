package t.me.p1azmer.plugin.protectionblocks.data.impl;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.data.AbstractUser;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.RegionBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionUser extends AbstractUser<ProtectionPlugin> {
    private Map<String, String> regions; // region id, region block id

    public RegionUser(@NotNull ProtectionPlugin plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(),
                new HashMap<>() // Regions
        );
    }

    public RegionUser(
            @NotNull ProtectionPlugin plugin,
            @NotNull UUID uuid,
            @NotNull String name,
            long dateCreated,
            long lastLogin,

            @NotNull Map<String, String> regions
    ) {
        super(plugin, uuid, name, dateCreated, lastLogin);
        this.setRegions(regions);
    }

    @NotNull
    public Map<String, String> getRegions() {
        return this.regions;
    }

    public void setRegions(@NotNull Map<String, String> regions) {
        this.regions = regions;
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
}