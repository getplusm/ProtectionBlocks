package t.me.p1azmer.plugin.protectionblocks.region.impl.block;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.Colors2;
import t.me.p1azmer.engine.utils.RankMap;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.RegionBreaker;
import t.me.p1azmer.plugin.protectionblocks.api.integration.HologramHandler;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;
import t.me.p1azmer.plugin.protectionblocks.region.editor.RGBlockMainEditor;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.menu.block.RecipePreviewMainMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static t.me.p1azmer.engine.utils.Constants.DEFAULT;

@Getter
@Setter
public class RegionBlock extends AbstractConfigHolder<ProtectionPlugin> implements Placeholder {
    private ItemStack item;
    private String name;
    private int strength;
    private boolean lifeTimeEnabled, placeLimitEnabled, groupSizeEnabled, infinityYBlocks;
    private RankMap<Integer> groupSize;
    private RankMap<Integer> lifeTime;
    private RankMap<Integer> placeLimit;
    private int regionSize;
    private List<RegionBreaker> breakers;
    private List<String> worlds;
    private boolean hologramEnabled;
    private boolean hologramInRegion;
    private String hologramTemplate;

    private int depositPrice;
    private String currencyId;

    private BlockRecipe blockRecipe;

    private final PlaceholderMap placeholderMap;

    private RGBlockMainEditor editor;
    private RecipePreviewMainMenu previewMenu;
    private final RegionManager manager;

    public RegionBlock(@NotNull RegionManager manager, @NotNull JYML cfg) {
        super(manager.plugin(), cfg);
        this.manager = manager;

        this.breakers = new ArrayList<>();
        this.worlds = List.of("world");
        this.setItem(new ItemStack(Material.IRON_BLOCK));
        this.setName(Colors2.WHITE + "Small Region Block");
        this.setStrength(1);
        this.setRegionSize(5);
        this.setBlockRecipe(new BlockRecipe(this, Map.of(
          10, new ItemStack(Material.AIR),
          11, new ItemStack(Material.AIR),
          12, new ItemStack(Material.AIR),
          19, new ItemStack(Material.AIR),
          20, new ItemStack(Material.AIR),
          21, new ItemStack(Material.AIR),
          28, new ItemStack(Material.AIR),
          29, new ItemStack(Material.AIR),
          30, new ItemStack(Material.AIR))
          , false));

        this.placeholderMap = new PlaceholderMap()
          .add(Placeholders.REGION_BLOCK_ID, this::getId)
          .add(Placeholders.REGION_BLOCK_NAME, () -> Colorizer.apply(this.getName()))
          .add(Placeholders.REGION_BLOCK_SIZE, () -> String.valueOf(this.getRegionSize()))
          .add(Placeholders.REGION_BLOCK_STRENGTH, () -> String.valueOf(this.getStrength()))
          .add(Placeholders.REGION_BLOCK_DEPOSIT_PRICE, () -> String.valueOf(this.getDepositPrice()))
          .add(Placeholders.REGION_BLOCK_DEPOSIT_CURRENCY, this::getCurrencyId)
          .add(Placeholders.REGION_BLOCK_LIFE_TIME_ENABLED, () -> LangManager.getBoolean(this.isLifeTimeEnabled()))
          .add(Placeholders.REGION_BLOCK_PLACE_LIMIT_ENABLED, () -> LangManager.getBoolean(this.isPlaceLimitEnabled()))
          .add(Placeholders.REGION_BLOCK_GROUP_SIZE_ENABLED, () -> LangManager.getBoolean(this.isGroupSizeEnabled()))
          .add(Placeholders.REGION_BLOCK_HOLOGRAM_ENABLED, () -> LangManager.getBoolean(this.isHologramEnabled()))
          .add(Placeholders.REGION_BLOCK_HOLOGRAM_IN_REGION, () -> LangManager.getBoolean(this.isHologramInRegion()))
          .add(Placeholders.REGION_IS_INFINITY_Y_BLOCKS, () -> LangManager.getBoolean(this.isInfinityYBlocks()))
          .add(Placeholders.REGION_BLOCK_HOLOGRAM_TEMPLATE, this::getHologramTemplate)
          .add(Placeholders.REGION_BLOCK_RECIPE_ENABLED, () -> LangManager.getBoolean(this.getBlockRecipe().isEnabled()))
          .add(Placeholders.REGION_BLOCK_WORLDS, () -> Colorizer.apply(Colors2.LIGHT_PURPLE + String.join(", ", this.getWorlds())))
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
            this.lifeTime = RankMap.readInt(cfg, "Life_Time.Parameter", -1);
        this.setPlaceLimitEnabled(cfg.getBoolean("Limits.Place.Enabled", false));
        if (cfg.contains("Limits.Place.Groups")) {
            this.placeLimit = RankMap.readInt(cfg, "Limits.Place.Groups", -1);
        }
        this.item = cfg.getItemEncoded("Item");
        this.setDepositPrice(cfg.getInt("Region.Deposit.Price", 100));
        // old merge
        int oldSize = cfg.getInt("Region.Size", 5);
        if (cfg.getInt("Region.Size", -1) >= 0) {
            cfg.remove("Region.Size");
        }

        this.regionSize = cfg.getInt("Region.Size.Default", oldSize);
        this.setGroupSizeEnabled(cfg.getBoolean("Region.Size.Group.Enabled", false));
        this.setInfinityYBlocks(cfg.getBoolean("Region.Size.Infinity_Y", false));
        if (cfg.contains("Region.Size.Group.List")) {
            this.groupSize = RankMap.readInt(cfg, "Region.Size.Group.List", -1);
        }

        this.strength = cfg.getInt("Region.Strength", 1);

        for (String sId : cfg.getSection("Region.Breakers.List")) {
            this.getBreakers().add(RegionBreaker.read(cfg, "Region.Breakers.List." + sId));
        }
        this.setCurrencyId(cfg.getString("Region.Deposit.Currency_Id", "Vault"));

        if (!cfg.contains("Worlds") && cfg.getStringList("Worlds").isEmpty())
            cfg.set("Worlds", List.of("world"));
        this.setWorlds(cfg.getStringList("Worlds"));
        if (!cfg.isSet("Region.Recipe")) {
            this.setBlockRecipe(new BlockRecipe(this, Map.of(
              10, new ItemStack(Material.AIR),
              11, new ItemStack(Material.AIR),
              12, new ItemStack(Material.AIR),
              19, new ItemStack(Material.AIR),
              20, new ItemStack(Material.AIR),
              21, new ItemStack(Material.AIR),
              28, new ItemStack(Material.AIR),
              29, new ItemStack(Material.AIR),
              30, new ItemStack(Material.AIR))
              , false));
            this.save();
        } else {
            this.setBlockRecipe(BlockRecipe.read(cfg, "Region", this));
        }
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
        if (this.getPlaceLimit() != null) {
            this.getPlaceLimit().write(cfg, "Limits.Place.Groups");
        }
        cfg.setItemEncoded("Item", this.getItem());
        cfg.set("Name", this.getName());
        cfg.set("Region.Size.Default", this.getRegionSize());
        cfg.set("Region.Size.Group.Enabled", this.isGroupSizeEnabled());
        cfg.set("Region.Size.Infinity_Y", this.isInfinityYBlocks());
        if (this.getGroupSize() != null) {
            this.getGroupSize().write(cfg, "Region.Size.Group.List");
        }
        cfg.set("Region.Deposit.Price", this.getDepositPrice());
        cfg.set("Region.Deposit.Currency_Id", this.getCurrencyId());
        cfg.set("Region.Strength", this.getStrength());
        cfg.set("Region.Breakers", null);
        int i = 0;
        for (RegionBreaker breaker : this.getBreakers()) {
            breaker.write(cfg, "Region.Breakers.List." + (i++));
        }

        cfg.set("Worlds", this.getWorlds());
        this.getBlockRecipe().write(cfg, "Region");
    }

    public void clear() {
        if (this.editor != null) this.editor.clear();
        if (this.previewMenu != null) this.previewMenu.clear();
        this.getBlockRecipe().shutdown();

        if (Version.isAbove(Version.V1_20_R1)) Bukkit.updateRecipes();
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

    @NotNull
    public ItemStack getItem() {
        return new ItemStack(item);
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
    public RecipePreviewMainMenu getPreviewMenu() {
        if (this.previewMenu == null)
            this.previewMenu = new RecipePreviewMainMenu(this);
        return this.previewMenu;
    }

    public void setLifeTimeEnabled(boolean lifeTimeEnabled) {
        this.lifeTimeEnabled = lifeTimeEnabled;
        if (lifeTimeEnabled) {
            if (!this.getConfig().contains("Life_Time.Parameter")) {
                new RankMap<>(RankMap.Mode.RANK, "", 604800, Map.of(DEFAULT, 604800))
                  .write(this.getConfig(), "Life_Time.Parameter");
            }
            this.lifeTime = RankMap.readInt(cfg, "Life_Time.Parameter", 604800);
        }
    }

    public void setPlaceLimitEnabled(boolean placeLimitEnabled) {
        this.placeLimitEnabled = placeLimitEnabled;
        if (placeLimitEnabled) {
            if (!this.getConfig().contains("Limits.Place.Groups")) {
                new RankMap<>(RankMap.Mode.RANK, "", -1, Map.of(DEFAULT, 3, "admin", -1))
                  .write(this.getConfig(), "Limits.Place.Groups");
            }
            this.placeLimit = RankMap.readInt(cfg, "Limits.Place.Groups", -1);
        }
    }

    public void setGroupSizeEnabled(boolean groupSizeEnabled) {
        this.groupSizeEnabled = groupSizeEnabled;
        if (groupSizeEnabled) {
            if (!this.getConfig().contains("Region.Size.Group.List")) {
                new RankMap<>(RankMap.Mode.RANK, "", 3, Map.of(DEFAULT, 3))
                  .write(this.getConfig(), "Region.Size.Group.List");
            }
            this.groupSize = RankMap.readInt(cfg, "Region.Size.Group.List", 3);
        }
    }

    public boolean damage(@NotNull DamageType damageType, @Nullable Object blockOrItem) {
        if (this.getBreakers().isEmpty()) return false;

        return this.getBreakers()
                   .stream()
                   .anyMatch(regionBreaker -> regionBreaker.tryBreakRegion(damageType, blockOrItem));
    }
}