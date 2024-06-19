package t.me.p1azmer.plugin.protectionblocks.region;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.wrapper.UniParticle;
import t.me.p1azmer.plugin.protectionblocks.Keys;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.events.AsyncCreatedRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.AsyncDeletedRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.data.impl.RegionUser;
import t.me.p1azmer.plugin.protectionblocks.region.editor.RGListEditor;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.DamageType;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;
import t.me.p1azmer.plugin.protectionblocks.region.listener.PlayerListener;
import t.me.p1azmer.plugin.protectionblocks.region.listener.RegionListener;
import t.me.p1azmer.plugin.protectionblocks.region.menu.block.RecipePreviewListMenu;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RegionManager extends AbstractManager<ProtectionPlugin> {
    private final Map<String, Region> regionMap = new ConcurrentHashMap<>();
    private final Map<String, RegionBlock> regionBlockMap = new ConcurrentHashMap<>();
    private final AsyncLoadingCache<Location, Region> locationCache;

    private RGListEditor editor;
    private RecipePreviewListMenu recipePreviewListMenu;

    public RegionManager(@NotNull ProtectionPlugin plugin) {
        super(plugin);
        this.locationCache = Caffeine.newBuilder()
                                     .refreshAfterWrite(30, TimeUnit.SECONDS)
                                     .buildAsync((location, executor) ->
                                       CompletableFuture.completedFuture(
                                         regionMap.values()
                                                  .stream()
                                                  .filter(f ->
                                                    f.getBlockLocation().equals(location)
                                                      || f.isRegionBlock(location.getBlock())
                                                      || f.getCuboid().contains(location)
                                                  )
                                                  .findFirst()
                                                  .orElse(null)
                                       ).exceptionally(throwable -> {
                                           throwable.printStackTrace();
                                           return null;
                                       }));
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    protected void onLoad() {
        CompletableFuture.runAsync(() -> {
            for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.REGION_BLOCKS_DIR, true)) {
                RegionBlock regionBlock = new RegionBlock(this, cfg);
                if (regionBlock.load()) {
                    this.regionBlockMap.put(regionBlock.getId(), regionBlock);
                    regionBlock.getBlockRecipe().setup();

                    if (Version.isAbove(Version.V1_20_R1)) Bukkit.updateRecipes();

                } else {
                    this.plugin.error("Region Block not loaded '" + cfg.getFile().getName() + "'");
                }
            }
            this.plugin.info("Region Blocks Loaded: " + this.getRegionBlocks().size());
        }).thenAccept(__ -> {
            // TODO: rewrite to database
            for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.REGION_DIR, true)) {
                Region region = new Region(this, cfg);
                if (region.load()) {
                    this.regionMap.put(region.getId(), region);
                    this.plugin.runTask(sync -> region.loadLocations());
                } else {
                    this.plugin.error("Region not loaded '" + cfg.getFile().getName() + "'");
                }
            }
            this.plugin.info("Regions Loaded: " + this.getRegionMap().size());
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        this.editor = new RGListEditor(this);
        this.addListener(new RegionListener(this));
        this.addListener(new PlayerListener(this));
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    protected void onShutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.recipePreviewListMenu != null) {
            this.recipePreviewListMenu.clear();
        }
        CompletableFuture.runAsync(() -> {
            this.getRegions().forEach(Region::clear);
            this.getRegionMap().clear();

            this.getRegionBlocks().forEach(RegionBlock::clear);
            this.getRegionBlocks().clear();
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    @NotNull
    public RGListEditor getEditor() {
        if (this.editor == null)
            this.editor = new RGListEditor(this);
        return this.editor;
    }

    @NotNull
    public RecipePreviewListMenu getRecipePreviewListMenu() {
        if (this.recipePreviewListMenu == null)
            this.recipePreviewListMenu = new RecipePreviewListMenu(this);
        return this.recipePreviewListMenu;
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
        return this.getRegionBlocks()
                   .stream()
                   .filter(f -> f.getItem().isSimilar(item))
                   .findFirst()
                   .orElse(null);
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
        return this.getRegions()
                   .stream()
                   .filter(f -> f.getRegionBlockId().equals(block.getId()))
                   .collect(Collectors.toList());
    }

    @Nullable
    public Region getRegionByLocation(@NotNull Location location) {
        return this.locationCache.synchronous().get(location);
    }

    @Nullable
    public Region getRegionByBlock(@NotNull Block block) {
        return this.getRegionByLocation(block.getLocation());
    }

    @Nullable
    public Region getRegionById(@NotNull String id) {
        return this.getRegions()
                   .stream()
                   .filter(region -> region.getId().equalsIgnoreCase(id))
                   .findFirst()
                   .orElse(null);
    }

    public boolean isProtectedBlock(@NotNull Block block) {
        return this.getRegionByBlock(block) != null;
    }

    public boolean isProtectedLocation(@NotNull Location location) {
        return this.getRegionByLocation(location) != null;
    }

    public boolean createRegionBlock(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getRegionBlockById(id) != null) {
            return false;
        }

        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.REGION_BLOCKS_DIR, id + ".yml");
        RegionBlock regionBlock = new RegionBlock(this, cfg);
        regionBlock.save();
        regionBlock.load();

        this.getRegionBlockMap().put(regionBlock.getId(), regionBlock);
        return true;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean tryCreateRegion(
      @NotNull Player player,
      @NotNull Block block,
      @NotNull ItemStack item,
      @NotNull RegionBlock regionBlock
    ) {
        RegionUser regionUser = plugin.getUserManager().getUserData(player);
        Region region = this.getRegionByBlock(block);
        if (region != null && !region.isAllowed(player) || !regionBlock.getWorlds().contains(block.getWorld().getName())) {
            return false;
        }

        int totalLimit = Config.TOTAL_REGION_LIMIT.get();
        long userAmountOfRegion = regionUser.getAmountOf(regionBlock);
        long userRegions = regionUser.getRegionsAmount();

        if (totalLimit > 0) {
            if (userRegions >= totalLimit) {
                plugin.getMessage(Lang.REGION_ERROR_CREATED_LIMIT).send(player);
                return false;
            }
        }
        if (regionBlock.isPlaceLimitEnabled() && regionBlock.getPlaceLimit() != null) {
            int limit = regionBlock.getPlaceLimit().getGreatest(player);
            if (userAmountOfRegion >= limit) {
                plugin.getMessage(Lang.REGION_ERROR_CREATED_LIMIT).send(player);
                return false;
            }
        }

        CompletableFuture.runAsync(() ->
                           this.createRegion(player, block, regionBlock)
                         )
                         .exceptionally(throwable -> {
                             throwable.printStackTrace();
                             return null;
                         });
        ;
        return true;
    }

    public void createRegion(@NotNull Player player, @NotNull Block block, @NotNull RegionBlock regionBlock) {
        AsyncCreatedRegionEvent calledEvent = new AsyncCreatedRegionEvent(block);
        Bukkit.getPluginManager().callEvent(calledEvent);

        boolean eventCancelled = calledEvent.isCancelled();
        if (eventCancelled) {
            if (calledEvent.isNotifyIfCancelled())
                this.plugin.getMessage(Lang.REGION_CREATE_CANCELLED_VIA_EVENT).send(player);
            return;
        }

        String fileName = UUID.randomUUID()
                              .toString()
                              .substring(0, 7)
                              .replace("-", "") + "-" + (this.getRegions().size() + 1) + ".yml";
        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.REGION_DIR, fileName);
        Region region = new Region(this, cfg);

        region.setRegionBlockAndId(regionBlock);
        region.setOwner(player);
        if (regionBlock.getLifeTime() != null && regionBlock.isLifeTimeEnabled())
            region.setLastDeposit(System.currentTimeMillis() + regionBlock.getLifeTime().getGreatest(player) * 1000L);
        else
            region.setLastDeposit(-1);
        region.setBlockLocation(block.getLocation(), regionBlock, player);
        region.setBlockHealth(regionBlock.getStrength());
        region.save();
        this.locationCache.synchronous().put(block.getLocation(), region);

        this.getRegionMap().put(region.getId(), region);

        block.setMetadata(Keys.REGION_BLOCK.getKey(), new FixedMetadataValue(plugin, region.getId()));

        if (Config.ENABLE_PARTICLE_WHEN_CREATE.get()) {
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
        }

        regionBlock.updateHologram(region);
        if (regionBlock.isLifeTimeEnabled()) {
            plugin.getMessage(Lang.REGION_SUCCESS_CREATED_WITH_LIFE_TIME)
                  .replace(regionBlock.replacePlaceholders())
                  .replace(region.replacePlaceholders())
                  .send(player);
        } else {
            plugin.getMessage(Lang.REGION_SUCCESS_CREATED)
                  .replace(regionBlock.replacePlaceholders())
                  .replace(region.replacePlaceholders())
                  .send(player);
        }

        plugin.getUserManager().getUserDataAndPerform(player.getUniqueId(), regionUser -> {
            regionUser.addRegion(region, regionBlock);
            plugin.getUserManager().saveUser(regionUser);
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
        CompletableFuture.runAsync(() -> {
            if (!region.getFile().delete()) return;
            AsyncDeletedRegionEvent calledEvent = new AsyncDeletedRegionEvent(region);
            Bukkit.getPluginManager().callEvent(calledEvent);
            if (calledEvent.isCancelled()) {
                if (calledEvent.isNotifyIfCancelled()) {
                    region.getOwnerPlayer()
                          .ifPresent(player -> this.plugin.getMessage(Lang.REGION_CREATE_CANCELLED_VIA_EVENT)
                                                          .send(player));
                }
                return;
            }
            this.getRegionMap().remove(region.getId(), region);
            region.clear();
            this.locationCache.synchronous().invalidate(region.getBlockLocation());
            if (notify) {
                region.broadcast(plugin.getMessage(Lang.REGION_DESTROY_NOTIFY)
                                       .replace(region.replacePlaceholders())
                                       .replace(region.getRegionBlock().replacePlaceholders()));
            }
            for (int step = 0; step < 36; ++step) {
                Location loc = region.getBlockLocation().clone().add(0.5, -0.8D, 0.5);
                double n2 = (0.5 + step * 0.15) % 3.0;
                for (int n3 = 0; n3 < n2 * 10.0; ++n3) {
                    double n4 = 6.283185307179586 / (n2 * 10.0) * n3;
                    UniParticle.redstone(Color.RED, 1).play(getPointOnCircle(loc.clone(), n4, n2, 1.0), 0.1f, 0.0f, 2);
                }
            }
            plugin.getUserManager().getUserDataAndPerform(region.getOwnerUUID(), regionUser -> {
                regionUser.removeRegion(region);
                plugin.getUserManager().saveUser(regionUser);
            });
        });
    }

    public boolean tryDamageRegion(@Nullable Player player, @Nullable Object blockOrItem, @NotNull Block targetBlock, @NotNull Region region, @NotNull DamageType damageType, boolean notify) {
        if (region.isExpired()) {
            this.deleteRegion(region, true);
            return true;
        }

        RegionBlock regionBlock = region.getRegionBlock();

        if (!regionBlock.damage(damageType, blockOrItem)) {
            return false;
        }

        if (region.isRegionBlock(targetBlock)) {
            region.takeBlockHealth(damageType);
            if (region.getBlockHealth() == 0) {
                region.getBlockLocation().getBlock().breakNaturally(new ItemStack(Material.AIR));
                this.deleteRegion(region, false);

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
            }
        }
        if (notify) {
            region.broadcast(plugin.getMessage(Lang.REGION_SUCCESS_DAMAGED_SELF)
                                   .replace(region.replacePlaceholders()));
            if (player != null)
                plugin.getMessage(Lang.REGION_SUCCESS_DAMAGED_TARGET)
                      .replace(region.replacePlaceholders())
                      .send(player);
        }
        return true;
    }

    public boolean tryDamageRegion(@Nullable Object blockOrItem, @NotNull Block targetBlock, @NotNull Region region, @NotNull DamageType damageType, boolean notify) {
        return this.tryDamageRegion(null, blockOrItem, targetBlock, region, damageType, notify);
    }

    public boolean tryDestroyRegion(@NotNull Player player, @Nullable Object blockOrItem, @NotNull Block targetBlock, @NotNull DamageType damageType, @NotNull Region region) {
        boolean isMember = region.isAllowed(player);

        if (!isMember) {
            boolean allowed = this.tryDamageRegion(player, blockOrItem, targetBlock, region, damageType, true);
            if (!allowed) {
                this.plugin.getMessage(Lang.REGION_ERROR_BREAK)
                           .replace(region.replacePlaceholders())
                           .send(player);
            }
            return allowed;
        }
        if (region.isRegionBlock(targetBlock) && (!Config.REGION_BLOCK_BREAK_OWNER_ONLY.get() || region.isOwner(player.getUniqueId()))) {
            this.deleteRegion(region, false);

            region.broadcast(plugin.getMessage(Lang.REGION_SUCCESS_DESTROY_SELF)
                                   .replace(region.replacePlaceholders()));
            targetBlock.breakNaturally(new ItemStack(Material.AIR));
            RegionBlock regionBlock = region.getRegionBlock();
            targetBlock.getWorld().dropItem(targetBlock.getLocation(), regionBlock.getItem());
            return true;
        }
        return !region.isRegionBlock(targetBlock);
    }

    public boolean tryBlockPlaceRegion(@NotNull Player player, @NotNull Region region) {
        return region.isAllowed(player);
    }

    public boolean isPlayerInsideRegion(@NotNull Player player) {
        Location playerLocation = player.getLocation();
        return this.getRegionByLocation(playerLocation) != null;
    }

    @NotNull
    private Location getPointOnCircle(@NotNull Location loc, double x, double z, double y) {
        return loc.add(Math.cos(x) * z, y, Math.sin(x) * z);
    }

}