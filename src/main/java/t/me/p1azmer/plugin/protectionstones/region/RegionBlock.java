package t.me.p1azmer.plugin.protectionstones.region;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.PlayerRankMap;
import t.me.p1azmer.plugin.protectionstones.ProtectionPlugin;

import java.util.Map;

public class RegionBlock extends AbstractConfigHolder<ProtectionPlugin> {
    private Material material;
    private PlayerRankMap<Long> lifeTime;
    private RegionType regionType;
    private int regionSize;

    public RegionBlock(@NotNull ProtectionPlugin plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
    }

    @Override
    public boolean load() {
        this.lifeTime = PlayerRankMap.read(cfg, "Life_Time", Long.class).setNegativeBetter(true);
        this.material = cfg.getEnum("Material", Material.class);
        this.regionType = cfg.getEnum("Region.Type", RegionType.class);
        this.regionSize = cfg.getInt("Region.Size", 5);
        return true;
    }

    @Override
    protected void onSave() {
        this.getLifeTime().write(cfg, "Life_Time");
        cfg.set("Material", this.getMaterial().name());
        cfg.set("Region.Type", this.getRegionType().name());
        cfg.set("Region.Size", this.getRegionSize());
    }

    @NotNull
    public Material getMaterial() {
        return material;
    }

    @NotNull
    public PlayerRankMap<Long> getLifeTime() {
        return lifeTime;
    }

    public int getRegionSize() {
        return regionSize;
    }

    @NotNull
    public RegionType getRegionType() {
        return regionType;
    }
}
