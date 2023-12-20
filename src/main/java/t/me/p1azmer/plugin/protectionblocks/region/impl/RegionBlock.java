package t.me.p1azmer.plugin.protectionblocks.region.impl;

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
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;
import t.me.p1azmer.plugin.protectionblocks.region.editor.RGBlockMainEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static t.me.p1azmer.engine.utils.Constants.DEFAULT;

public class RegionBlock extends AbstractConfigHolder<ProtectionPlugin> implements IPlaceholderMap {
    private ItemStack item;
    private String name;
    private int strength;
    private boolean lifeTimeEnabled, placeLimitEnabled, groupSizeEnabled;
    private PlayerRankMap<Integer> groupSize;
    private PlayerRankMap<Integer> lifeTime;
    private PlayerRankMap<Integer> placeLimit;
    private int regionSize;
    private List<RegionBreaker> breakers;
    private boolean hologramEnabled;
    private boolean hologramInRegion;
    private String hologramTemplate;

    private int depositPrice;
    private String currencyId;

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
                .add(Placeholders.REGION_BLOCK_DEPOSIT_PRICE, ()-> String.valueOf(this.getDepositPrice()))
                .add(Placeholders.REGION_BLOCK_DEPOSIT_CURRENCY, this::getCurrencyId)
                .add(Placeholders.REGION_BLOCK_LIFE_TIME_ENABLED, () -> LangManager.getBoolean(this.isLifeTimeEnabled()))
                .add(Placeholders.REGION_BLOCK_PLACE_LIMIT_ENABLED, () -> LangManager.getBoolean(this.isPlaceLimitEnabled()))
                .add(Placeholders.REGION_BLOCK_GROUP_SIZE_ENABLED, () -> LangManager.getBoolean(this.isGroupSizeEnabled()))
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
        this.setLifeTimeEnabled(cfg.getBoolean("Life_Time.Enabled"));
        if (cfg.contains("Life_Time.Parameter") && this.isLifeTimeEnabled())
            this.lifeTime = PlayerRankMap.readInt(cfg, "Life_Time.Parameter");
        this.setPlaceLimitEnabled(cfg.getBoolean("Limits.Place.Enabled", false));
        if (cfg.contains("Limits.Place.Groups")){
            this.placeLimit = PlayerRankMap.readInt(cfg, "Limits.Place.Groups");
        }
        this.item = cfg.getItemEncoded("Item");
        this.setDepositPrice(cfg.getInt("Region.Deposit.Price", 100));
        // old merge
        int oldSize = cfg.getInt("Region.Size", 5);
        if (cfg.getInt("Region.Size", -1) >= 0){
            cfg.remove("Region.Size");
        }

        this.regionSize = cfg.getInt("Region.Size.Default", oldSize);
        this.setGroupSizeEnabled(cfg.getBoolean("Region.Size.Group.Enabled", false));
        if (cfg.contains("Region.Size.Group.List")){
            this.groupSize = PlayerRankMap.readInt(cfg, "Region.Size.Group.List");
        }

        this.strength = cfg.getInt("Region.Strength", 1);

        for (String sId : cfg.getSection("Region.Breakers.List")) {
            this.getBreakers().add(RegionBreaker.read(cfg, "Region.Breakers.List." + sId));
        }
        this.setCurrencyId(cfg.getString("Region.Deposit.Currency_Id", "Vault"));
        return true;
    }

    @Override
    protected void onSave() {
        cfg.set("Block.Hologram.Enabled", this.isHologramEnabled());
        cfg.set("Block.Hologram.In_Region", this.isHologramInRegion());
        cfg.set("Block.Hologram.Template", this.getHologramTemplate());

        if (this.getLifeTime() != null)
            this.getLifeTime().write(cfg, "Life_Time.Parameter");
        cfg.set("Life_Time.Enabled", this.isLifeTimeEnabled());
        cfg.set("Limits.Place.Enabled", this.isPlaceLimitEnabled());
        if (this.getPlaceLimit() != null){
            this.getPlaceLimit().write(cfg, "Limits.Place.Groups");
        }
        cfg.setItemEncoded("Item", this.getItem());
        cfg.set("Name", this.getName());
        cfg.set("Region.Size.Default", this.getRegionSize());
        cfg.set("Region.Size.Group.Enabled", this.isGroupSizeEnabled());
        if (this.getGroupSize() != null){
            this.getGroupSize().write(cfg, "Region.Size.Group.List");
        }
        cfg.set("Region.Deposit.Price", this.getDepositPrice());
        cfg.set("Region.Deposit.Currency_Id", this.getCurrencyId());
        cfg.set("Region.Strength", this.getStrength());
        cfg.set("Region.Breakers", null);
        int i = 0;
        for (RegionBreaker breaker : this.getBreakers()){
            breaker.write(cfg, "Region.Breakers.List."+ (i++));
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

    public boolean isHologramEnabled() {
        return this.hologramEnabled;
    }

    @NotNull
    public String getHologramTemplate() {
        return hologramTemplate;
    }

    public boolean isLifeTimeEnabled() {
        return lifeTimeEnabled;
    }

    public boolean isPlaceLimitEnabled() {
        return placeLimitEnabled;
    }

    public boolean isGroupSizeEnabled() {
        return groupSizeEnabled;
    }

    public int getDepositPrice() {
        return depositPrice;
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
    public PlayerRankMap<Integer> getLifeTime() {
        return lifeTime;
    }

    @Nullable
    public PlayerRankMap<Integer> getPlaceLimit() {
        return placeLimit;
    }

    @Nullable
    public PlayerRankMap<Integer> getGroupSize() {
        return groupSize;
    }

    @NotNull
    public List<RegionBreaker> getBreakers() {
        return breakers;
    }

    @NotNull
    public String getCurrencyId() {
        return currencyId;
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

    public void setHologramInRegion(boolean hologramInRegion) {
        this.hologramInRegion = hologramInRegion;
    }

    public void setHologramEnabled(boolean hologramEnabled) {
        this.hologramEnabled = hologramEnabled;
    }

    public void setHologramTemplate(@NotNull String hologramTemplate) {
        this.hologramTemplate = hologramTemplate.toLowerCase();
    }

    public void setLifeTimeEnabled(boolean lifeTimeEnabled) {
        this.lifeTimeEnabled = lifeTimeEnabled;
        if (lifeTimeEnabled) {
            if (!this.getConfig().contains("Life_Time.Parameter")) {
                new PlayerRankMap<>(Map.of(DEFAULT, 604800L))
                        .write(this.getConfig(), "Life_Time.Parameter");
            }
            this.lifeTime = PlayerRankMap.readInt(cfg, "Life_Time.Parameter");
        }
    }

    public void setPlaceLimitEnabled(boolean placeLimitEnabled) {
        this.placeLimitEnabled = placeLimitEnabled;
        if (placeLimitEnabled) {
            if (!this.getConfig().contains("Limits.Place.Groups")) {
                new PlayerRankMap<>(Map.of(DEFAULT, 3))
                        .write(this.getConfig(), "Limits.Place.Groups");
            }
            this.placeLimit = PlayerRankMap.readInt(cfg, "Limits.Place.Groups");
        }
    }

    public void setGroupSizeEnabled(boolean groupSizeEnabled) {
        this.groupSizeEnabled = groupSizeEnabled;
        if (groupSizeEnabled) {
            if (!this.getConfig().contains("Region.Size.Group.List")) {
                new PlayerRankMap<>(Map.of(DEFAULT, 3))
                        .write(this.getConfig(), "Region.Size.Group.List");
            }
            this.groupSize = PlayerRankMap.readInt(cfg, "Region.Size.Group.List");
        }
    }

    public void setDepositPrice(int depositPrice) {
        this.depositPrice = depositPrice;
    }

    public void setCurrencyId(@NotNull String currencyId) {
        this.currencyId = currencyId;
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
