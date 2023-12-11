package t.me.p1azmer.plugin.protectionblocks.region;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.PlayerRankMap;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.RegionBreaker;
import t.me.p1azmer.plugin.protectionblocks.api.integration.HologramHandler;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.region.editor.RGBlockMainEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegionBlock extends AbstractConfigHolder<ProtectionPlugin> implements IPlaceholderMap {
    private ItemStack item;
    private String name;
    private int strength;
    private PlayerRankMap<Long> lifeTime;
    private int regionSize;
    private List<RegionBreaker> breakers;
    private boolean hologramEnabled;
    private boolean hologramInRegion;
    private String hologramTemplate;

    private final PlaceholderMap placeholderMap;

    private RGBlockMainEditor editor;
    private final RegionManager manager;

    public RegionBlock(@NotNull RegionManager manager, @NotNull JYML cfg) {
        super(manager.plugin(), cfg);
        this.manager = manager;

        this.breakers = new ArrayList<>();

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.REGION_BLOCK_ID, this::getId)
                .add(Placeholders.REGION_BLOCK_NAME, () -> Colorizer.apply(this.getName()))
                .add(Placeholders.REGION_BLOCK_SIZE, () -> String.valueOf(this.getRegionSize()))
                .add(Placeholders.REGION_BLOCK_STRENGTH, () -> String.valueOf(this.getStrength()))
                .add(Placeholders.REGION_BLOCK_HOLOGRAM_ENABLED, () -> LangManager.getBoolean(this.isHologramEnabled()))
                .add(Placeholders.REGION_BLOCK_HOLOGRAM_IN_REGION, () -> LangManager.getBoolean(this.isHologramInRegion()))
                .add(Placeholders.REGION_BLOCK_HOLOGRAM_TEMPLATE, this::getHologramTemplate)
        ;
    }

    @Override
    public boolean load() {
        this.setHologramEnabled(cfg.getBoolean("Block.Hologram.Enabled"));
        this.setHologramInRegion(cfg.getBoolean("Block.Hologram.In_Region"));
        this.setHologramTemplate(cfg.getString("Block.Hologram.Template", Placeholders.DEFAULT));
        this.name = cfg.getString("Name", this.getName());
        if (cfg.contains("Life_Time"))
            this.lifeTime = PlayerRankMap.readLong(cfg, "Life_Time").setNegativeBetter(true);
        this.item = cfg.getItemEncoded("Item");
        this.regionSize = cfg.getInt("Region.Size", 5);
        this.strength = cfg.getInt("Region.Strength", 1);

        for (String id : cfg.getSection("Region.Breakers")){
            String path = "Region.Breakers."+id;
            this.breakers.add(RegionBreaker.read(cfg, path, id));
        }
        return true;
    }

    @Override
    protected void onSave() {
        cfg.set("Block.Hologram.Enabled", this.isHologramEnabled());
        cfg.set("Block.Hologram.In_Region", this.isHologramInRegion());
        cfg.set("Block.Hologram.Template", this.getHologramTemplate());

        if (this.getLifeTime() != null)
            this.getLifeTime().write(cfg, "Life_Time");
        cfg.setItemEncoded("Item", this.getItem());
        cfg.set("Name", this.getName());
        cfg.set("Region.Size", this.getRegionSize());
        cfg.set("Region.Strength", this.getStrength());
        cfg.set("Region.Breakers", null);
        for (RegionBreaker breaker : this.getBreakers()){
            breaker.write(cfg, "Region.Breakers");
        }
    }

    public void clear() {
        if (this.editor != null) this.editor.clear();
    }

    public void createHologram(@NotNull Region region) {
        if (!this.isHologramEnabled()) return;

        HologramHandler hologramHandler = plugin.getHologramHandler();
        if (hologramHandler == null) return;

        hologramHandler.create(region);
    }

    public void removeHologram(@NotNull Region region) {
        HologramHandler hologramHandler = plugin.getHologramHandler();
        if (hologramHandler == null) return;

        hologramHandler.delete(region);
    }

    public void updateHologram(@NotNull Region region) {
        this.removeHologram(region);
        this.createHologram(region);
    }


    @NotNull
    public List<String> getHologramText(@NotNull Region region) {
        List<String> text = new ArrayList<>(Config.REGION_HOLOGRAM_TEMPLATES.get().getOrDefault(this.getHologramTemplate(), Collections.emptyList()));
        text.replaceAll(this.replacePlaceholders());
        text.replaceAll(region.replacePlaceholders());
        return text;
    }

    public boolean isHologramInRegion() {
        return hologramInRegion;
    }

    public void setHologramInRegion(boolean hologramInRegion) {
        this.hologramInRegion = hologramInRegion;
    }

    public boolean isHologramEnabled() {
        return this.hologramEnabled;
    }

    public void setHologramEnabled(boolean hologramEnabled) {
        this.hologramEnabled = hologramEnabled;
    }

    @NotNull
    public String getHologramTemplate() {
        return hologramTemplate;
    }

    public void setHologramTemplate(@NotNull String hologramTemplate) {
        this.hologramTemplate = hologramTemplate.toLowerCase();
    }

    @NotNull
    public ItemStack getItem() {
        return new ItemStack(item);
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public PlayerRankMap<Long> getLifeTime() {
        return lifeTime;
    }

    @NotNull
    public List<RegionBreaker> getBreakers() {
        return breakers;
    }

    public int getRegionSize() {
        return regionSize;
    }

    public int getStrength() {
        return strength;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public RGBlockMainEditor getEditor() {
        if (this.editor == null)
            this.editor = new RGBlockMainEditor(this);
        return this.editor;
    }

    @NotNull
    public RegionManager getManager() {
        return manager;
    }

    public void setItem(@NotNull ItemStack item) {
        this.item = item;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setLifeTime(@NotNull PlayerRankMap<Long> lifeTime) {
        this.lifeTime = lifeTime;
    }

    public void setRegionSize(int regionSize) {
        this.regionSize = regionSize;
    }

    public void setBreakers(@NotNull List<RegionBreaker> breakers) {
        this.breakers = breakers;
    }

    public boolean damage(@NotNull RegionManager.DamageType damageType, @Nullable Object blockOrItem) {
        return this.getBreakers().stream().anyMatch(regionBreaker -> regionBreaker.tryBreak(damageType, blockOrItem));
    }
}
