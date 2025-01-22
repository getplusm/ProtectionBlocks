package t.me.p1azmer.plugin.protectionblocks.integration.holograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.plugin.protectionblocks.api.integration.HologramHandler;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HologramFancyHandler implements HologramHandler {
    private Map<String, Set<Hologram>> holoMap;

    @Override
    public void setup() {
        this.holoMap = new HashMap<>();
    }

    @Override
    public void shutdown() {
        if (this.holoMap != null) {
            this.holoMap.values().forEach(set -> set.forEach(Hologram::deleteHologram));
            this.holoMap = null;
        }
    }

    @Override
    public void create(@NotNull Region region) {
        Set<Hologram> holograms = this.holoMap.computeIfAbsent(region.getId(), set -> new HashSet<>());
        RegionBlock regionBlock = region.getRegionBlock();
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        Location location = this.fineLocation(region.getBlockLocation());
        TextHologramData textData = new TextHologramData("", location);

        textData.setText(regionBlock.getHologramText(region));
        textData.setPersistent(true);

        Hologram hologram = manager.create(textData);
        manager.addHologram(hologram);

        hologram.createHologram();
        if (!regionBlock.isHologramInRegion()) {
            hologram.showHologram(Bukkit.getOnlinePlayers());
        }
        holograms.add(hologram);
    }

    @NotNull
    private Location fineLocation(@NotNull Location location) {
        return location.toCenterLocation().add(0D, Config.REGION_HOLOGRAM_Y_OFFSET.get(), 0D);
    }

    @Override
    public void delete(@NotNull Region region) {
        Set<Hologram> set = this.holoMap.remove(region.getId());
        if (set == null) return;

        set.forEach(Hologram::deleteHologram);
    }

    @Override
    public void show(@NotNull Region region, @NotNull Player player) {
        Set<Hologram> set = this.holoMap.get(region.getId());
        if (set == null) return;

        set.forEach(hologram -> hologram.showHologram(player));
    }

    @Override
    public void hide(@NotNull Region region, @NotNull Player player) {
        Set<Hologram> set = this.holoMap.get(region.getId());
        if (set == null) return;

        RegionBlock regionBlock = region.getRegionBlock();
        if (!regionBlock.isHologramInRegion()) return;

        set.forEach(hologram -> hologram.hideHologram(player));
    }
}