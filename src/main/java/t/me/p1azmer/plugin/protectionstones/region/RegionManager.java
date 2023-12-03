package t.me.p1azmer.plugin.protectionstones.region;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.plugin.protectionstones.Keys;
import t.me.p1azmer.plugin.protectionstones.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionstones.config.Config;
import t.me.p1azmer.plugin.protectionstones.config.Lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionManager extends AbstractManager<ProtectionPlugin> {

    private final Map<String, RegionBlock> regionBlockMap;
    private final Map<String, Region> regionMap;
    // cache
    private final Map<Material, RegionBlock> materialRegionBlockMap;

    public RegionManager(@NotNull ProtectionPlugin plugin) {
        super(plugin);
        this.regionBlockMap = new HashMap<>();
        this.regionMap = new HashMap<>();
        this.materialRegionBlockMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.runTaskAsync(async -> this.plugin.runTask(sync -> {
            for (JYML cfg : JYML.loadAll(Config.REGION_BLOCKS_DIR, false)) {
                RegionBlock regionBlock = new RegionBlock(this.plugin, cfg);
                if (regionBlock.load()) {
                    this.regionBlockMap.put(regionBlock.getId(), regionBlock);
                    this.materialRegionBlockMap.put(regionBlock.getMaterial(), regionBlock);
                } else {
                    this.plugin.error("Region Block not loaded '" + cfg.getFile().getName() + "'");
                }
            }
            this.plugin.info("Region Blocks Loaded: " + this.getRegionBlocks().size());

            for (JYML cfg : JYML.loadAll(Config.REGION_DIR, false)) {
                Region region = new Region(this, cfg);
                if (region.load()) {
                    this.regionMap.put(region.getId(), region);
                } else {
                    this.plugin.error("Region not loaded '" + cfg.getFile().getName() + "'");
                }
            }
            this.plugin.info("Regions Loaded: " + this.getRegionMap().size());
        }));
    }

    @Override
    protected void onShutdown() {
        this.getRegions().forEach(region -> {
            region.clear();
            region.save();
        });
        this.getRegionMap().clear();

        this.getRegionBlocks().forEach(AbstractConfigHolder::save);
        this.getRegionBlockMap().clear();
    }

    @NotNull
    public Map<String, RegionBlock> getRegionBlockMap() {
        return regionBlockMap;
    }

    @NotNull
    public Collection<RegionBlock> getRegionBlocks() {
        return this.getRegionBlockMap().values();
    }

    @NotNull
    public Map<Material, RegionBlock> getMaterialRegionBlockMap() {
        return materialRegionBlockMap;
    }

    @Nullable
    public RegionBlock getRegionBlockByMaterial(@NotNull Material material) {
        return this.getMaterialRegionBlockMap().get(material);
    }

    @Nullable
    public RegionBlock getRegionBlockById(@NotNull String id) {
        return this.getRegionBlockMap().get(id);
    }

    @NotNull
    public Map<String, Region> getRegionMap() {
        return regionMap;
    }

    @NotNull
    public Collection<Region> getRegions() {
        return this.getRegionMap().values();
    }

    @Nullable
    public Region getRegionByBlock(@NotNull Block block) {
        return this.getRegions().stream().filter(f -> f.getBlockLocation().equals(block.getLocation())).findFirst().orElse(null);
    }

    public void deleteRegion(@NotNull Region region) {
        if (!region.getFile().delete()) return;
        region.clear();
        this.getRegionMap().remove(region.getId());
    }

    public void tryCreateRegion(@NotNull Player player, @NotNull Block block) {
        RegionBlock regionBlock = this.getRegionBlockByMaterial(block.getType());
        if (regionBlock == null || this.getRegionByBlock(block) != null) return;

        this.createRegion(player, block, regionBlock);
    }

    public void createRegion(@NotNull Player player, @NotNull Block block, @NotNull RegionBlock regionBlock) {
        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.REGION_DIR, (UUID.randomUUID() + "#" + this.getRegions().size() + 1) + ".yml");
        Region region = new Region(this, cfg);

        region.setRegionBlockId(regionBlock.getId());
        region.setOwner(player.getUniqueId());
        region.setCreateTime(System.currentTimeMillis());
        region.setBlockLocation(block.getLocation());
        region.setLastDeposit(System.currentTimeMillis());
        region.save();

        plugin.runTask(sync -> block.setMetadata(Keys.REGION_BLOCK.getKey(), new FixedMetadataValue(plugin, region.getId())));
        plugin.getMessage(Lang.REGION_SUCCESSFULLY_CREATED)
                .replace(region.replacePlaceholders())
                .send(player);
    }
}
