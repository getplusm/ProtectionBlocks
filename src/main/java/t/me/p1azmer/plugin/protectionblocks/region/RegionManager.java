package t.me.p1azmer.plugin.protectionblocks.region;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.Colors;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.values.UniParticle;
import t.me.p1azmer.plugin.protectionblocks.Keys;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.editor.RGListEditor;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.RegionBlock;
import t.me.p1azmer.plugin.protectionblocks.region.listener.PlayerListener;
import t.me.p1azmer.plugin.protectionblocks.region.listener.RegionListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class RegionManager extends AbstractManager<ProtectionPlugin> {
    private final Map<String, Region> regionMap;
    private final Map<String, RegionBlock> regionBlockMap;

    private RGListEditor editor;

    public RegionManager(@NotNull ProtectionPlugin plugin) {
        super(plugin);
        this.regionMap = new ConcurrentHashMap<>();
        this.regionBlockMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.runTaskAsync(async -> this.plugin.runTask(sync -> {
            for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.REGION_BLOCKS_DIR, true)) {
                RegionBlock regionBlock = new RegionBlock(this, cfg);
                if (!regionBlock.load())
                    this.plugin.error("Region Block not loaded '" + cfg.getFile().getName() + "'");
                else
                    this.regionBlockMap.put(regionBlock.getId(), regionBlock);
            }
            this.plugin.info("Region Blocks Loaded: " + this.getRegionBlocks().size());

            for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.REGION_DIR, true)) {
                Region region = new Region(this, cfg);
                if (region.load()) {
                    this.regionMap.put(region.getId(), region);
                } else {
                    this.plugin.error("Region not loaded '" + cfg.getFile().getName() + "'");
                }
            }
            this.plugin.info("Regions Loaded: " + this.getRegionMap().size());


            this.editor = new RGListEditor(this);
        }));
        this.plugin.runTaskLater(task -> {
            this.getRegions().forEach(Region::loadLocations);
        }, 20L);
        this.addListener(new RegionListener(this));
        this.addListener(new PlayerListener(this));
    }

    @Override
    protected void onShutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getRegions().forEach(Region::clear);
        this.getRegionMap().clear();

        this.getRegionBlocks().forEach(RegionBlock::clear);
        this.getRegionBlocks().clear();
    }

    @NotNull
    public RGListEditor getEditor() {
        if (this.editor == null)
            this.editor = new RGListEditor(this);
        return this.editor;
    }

    @NotNull
    public Map<String, RegionBlock> getRegionBlockMap() {
        return regionBlockMap;
    }

    @NotNull
    public Collection<RegionBlock> getRegionBlocks() {
        return this.getRegionBlockMap().values();
    }

    @Nullable
    public RegionBlock getRegionBlockByItem(@NotNull ItemStack item) {
        return this.getRegionBlocks().stream().filter(f -> f.getItem().isSimilar(item)).findFirst().orElse(null);
    }

    @Nullable
    public RegionBlock getRegionBlockById(@NotNull String id) {
        return getRegionBlockMap().get(id);
    }

    @NotNull
    public Map<String, Region> getRegionMap() {
        return regionMap;
    }

    @NotNull
    public Collection<Region> getRegions() {
        return this.getRegionMap().values();
    }

    @NotNull
    public Collection<Region> getRegionsWithBlocks(@NotNull RegionBlock block) {
        return this.getRegions().stream().filter(f -> f.getRegionBlockId().equals(block.getId())).collect(Collectors.toList());
    }

    @Nullable
    public Region getRegionByLocation(@NotNull Location location) {
        return this.getRegions()
                .stream()
                .filter(region -> region.getBlockLocation().equals(location) || region.isRegionBlock(location.getBlock()) || region.getCuboid().contains(location))
                .findFirst().orElse(null);
    }

    @Nullable
    public Region getRegionByBlock(@NotNull Block block) {
        return this.getRegionByLocation(block.getLocation());
    }

    public boolean isProtectedBlock(@NotNull Block block) {
        return this.getRegionByBlock(block) != null;
    }

    public boolean isProtectedLocation(@NotNull Location location) {
        return this.getRegionByLocation(location) != null;
    }

    public boolean create(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getRegionBlockById(id) != null) {
            return false;
        }

        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.REGION_BLOCKS_DIR, id + ".yml");
        RegionBlock regionBlock = new RegionBlock(this, cfg);
        regionBlock.setItem(new ItemStack(Material.IRON_BLOCK));
        regionBlock.setName(Colors.WHITE + "Small Region Block");
        regionBlock.setStrength(1);
        regionBlock.setRegionSize(5);
        regionBlock.save();
        regionBlock.load();

        this.getRegionBlockMap().put(regionBlock.getId(), regionBlock);
        return true;
    }

    public void tryCreateRegion(@NotNull Player player, @NotNull Block block, @NotNull ItemStack item) {
        RegionBlock regionBlock = this.getRegionBlockByItem(item);
        if (regionBlock == null) return;
        Region region = this.getRegionByBlock(block);
        if (region != null && !region.isAllowed(player)) return;

        this.createRegion(player, block, regionBlock);
    }

    public void createRegion(@NotNull Player player, @NotNull Block block, @NotNull RegionBlock regionBlock) {
        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.REGION_DIR, UUID.randomUUID().toString().substring(0, 7).replace("-", "") + "-" + (this.getRegions().size() + 1) + ".yml");
        Region region = new Region(this, cfg);

        plugin.runTask(sync -> {
            region.setRegionBlockId(regionBlock.getId());
            region.setOwner(player.getUniqueId());
            region.setOwnerName(player.getName());
            if (regionBlock.getLifeTime() != null && regionBlock.isLifeTimeEnabled())
                region.setLastDeposit(System.currentTimeMillis() + regionBlock.getLifeTime().getBestValue(player, 1000) * 1000L);
            else
                region.setLastDeposit(-1);
            region.setBlockLocation(block.getLocation());
            region.setBlockHealth(regionBlock.getStrength());
            region.save();

            this.getRegionMap().put(region.getId(), region);

            block.setMetadata(Keys.REGION_BLOCK.getKey(), new FixedMetadataValue(plugin, region.getId()));
            // small & pretty effect
            Location loc = block.getLocation().clone().add(0.5, -0.8D, 0.5);
            for (int step = 0; step < 170 / 5; ++step) {
                for (int boost = 0; boost < 3; boost++) {
                    for (int strand = 1; strand <= 2; ++strand) {
                        float progress = step / (float) (170 / 5);
                        double point = 2.0F * progress * 2.0f * Math.PI / 2 + 6.283185307179586 * strand / 2 + 0.7853981633974483D;
                        double addX = Math.cos(point) * progress * 1.5F;
                        double addZ = Math.sin(point) * progress * 1.5F;
                        double addY = 3.5D - 0.02 * 5 * step;
                        Location location = loc.clone().add(addX, addY, addZ);
                        UniParticle.redstone(Color.LIME, 1).play(location, 0.1f, 0.0f, 1);
                    }
                }
            }
            regionBlock.updateHologram(region);

            plugin.getMessage(Lang.REGION_SUCCESS_CREATED)
                    .replace(regionBlock.replacePlaceholders())
                    .replace(region.replacePlaceholders())
                    .send(player);
        });
    }

    public boolean deleteRegionBlock(@NotNull RegionBlock regionBlock) {
        if (regionBlock.getFile().delete()) {
            regionBlock.clear();
            this.getRegionBlockMap().remove(regionBlock.getId());
            return true;
        }
        return false;
    }

    public void deleteRegion(@NotNull Region region, boolean notify) {
        if (!region.getFile().delete()) return;
        region.clear();
        if (notify) {
            region.broadcast(plugin.getMessage(Lang.REGION_DESTROY_NOTIFY)
                    .replace(region.replacePlaceholders())
                    .replace(Objects.requireNonNull(region.getRegionBlock().orElse(null)).replacePlaceholders()));
        }
        for (int step = 0; step < 36; ++step) {
            Location loc = region.getBlockLocation().clone().add(0.5, -0.8D, 0.5);
            double n2 = (0.5 + step * 0.15) % 3.0;
            for (int n3 = 0; n3 < n2 * 10.0; ++n3) {
                double n4 = 6.283185307179586 / (n2 * 10.0) * n3;
                UniParticle.redstone(Color.RED, 1).play(getPointOnCircle(loc.clone(), false, n4, n2, 1.0), 0.1f, 0.0f, 2);
            }
        }
        this.getRegionMap().remove(region.getId());
    }

    public boolean tryDamageRegion(@Nullable Player player, @Nullable Object blockOrItem, @NotNull Block targetBlock, @NotNull Region region, @NotNull DamageType damageType, boolean notify) {
        if (region.isExpired()) {
            this.deleteRegion(region, true);
            return true;
        }
        AtomicBoolean result = new AtomicBoolean(false);

        region.getRegionBlock().ifPresent(regionBlock -> {
            result.set(regionBlock.damage(damageType, blockOrItem));
            if (region.isRegionBlock(targetBlock)) {

                region.takeBlockHealth(damageType);
                if (region.getBlockHealth() == 0) {
                    region.getBlockLocation().getBlock().breakNaturally(new ItemStack(Material.AIR), true, true);
                    this.deleteRegion(region, true);
                    if (notify) {
                        if (player != null) {
                            plugin.getMessage(Lang.REGION_SUCCESS_DESTROY_TARGET)
                                    .replace(region.replacePlaceholders())
                                    .replace(regionBlock.replacePlaceholders())
                                    .send(player);
                        }
                        region.broadcast(plugin.getMessage(Lang.REGION_SUCCESS_DESTROY_SELF)
                                .replace(region.replacePlaceholders())
                                .replace(regionBlock.replacePlaceholders()));
                    }
                    result.set(true);
                } else {
                    if (notify) {
                        region.broadcast(plugin.getMessage(Lang.REGION_SUCCESS_DAMAGED_SELF)
                                .replace(region.replacePlaceholders()));
                        if (player != null)
                            plugin.getMessage(Lang.REGION_SUCCESS_DAMAGED_TARGET)
                                    .replace(region.replacePlaceholders())
                                    .send(player);
                    }
                }
            }
        });
        return result.get();
    }

    public boolean tryDestroyRegion(@Nullable Player player, @Nullable Object blockOrItem, @NotNull Block targetBlock, @NotNull DamageType damageType, @NotNull Region region) {
        boolean isMember = player != null && region.isAllowed(player);

        if (!isMember) {
            boolean allowed = this.tryDamageRegion(player, blockOrItem, targetBlock, region, damageType, true);
            if (!allowed) {
                assert player != null;
                this.plugin.getMessage(Lang.REGION_ERROR_BREAK)
                        .replace(region.replacePlaceholders())
                        .send(player);
            }
            return allowed;
        } else if (region.isRegionBlock(targetBlock) && (!Config.REGION_BLOCK_BREAK_OWNER_ONLY.get() || region.getOwner().equals(player.getUniqueId()))) {
            this.deleteRegion(region, false);
            region.broadcast(plugin.getMessage(Lang.REGION_SUCCESS_DESTROY_SELF)
                    .replace(region.replacePlaceholders()));
        }
        return true;
    }

    public boolean tryBlockPlaceRegion(@NotNull Player player, @NotNull Region region) {
        return region.isAllowed(player);
    }

    public boolean isPlayerInsideRegion(@NotNull Player player) {
        Location playerLocation = player.getLocation();
        return this.getRegionByLocation(playerLocation) != null;
    }

    @NotNull
    private Location getPointOnCircle(@NotNull Location loc, boolean doCopy, double x, double z, double y) {
        return (doCopy ? loc.clone() : loc).add(Math.cos(x) * z, y, Math.sin(x) * z);
    }

    public enum DamageType {
        EXPLODE,
        HAND,
        TOOLS,
        FALLING_BLOCK,
        BLOCK_PLACE
    }
}
