package t.me.p1azmer.plugin.protectionblocks.integration.holograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.protectionblocks.api.integration.HologramHandler;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.*;

public class HologramDecentHandler implements HologramHandler {
    private Map<String, Set<Hologram>> holoMap;

    @Override
    public void setup() {
        this.holoMap = new HashMap<>();
    }

    @Override
    public void shutdown() {
        if (this.holoMap != null) {
            this.holoMap.values().forEach(set -> set.forEach(Hologram::delete));
            this.holoMap = null;
        }
    }

    @Override
    public void create(@NotNull Region region) {
        Set<Hologram> holograms = this.holoMap.computeIfAbsent(region.getId(), set -> new HashSet<>());
        RegionBlock regionBlock = region.getRegionBlock();

        Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), this.fineLocation(region.getBlockLocation()), regionBlock.getHologramText(region));
        if (regionBlock.isHologramInRegion()) {
            hologram.setDefaultVisibleState(false);
            hologram.hideAll();
        } else
            hologram.showAll();
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

        set.forEach(Hologram::delete);
    }

    @Override
    public void show(@NotNull Region region, @NotNull Player player) {
        Set<Hologram> set = this.holoMap.get(region.getId());
        if (set == null) return;

        set.forEach(hologram -> {
            hologram.removeHidePlayer(player);
            hologram.setShowPlayer(player);
            hologram.update(player);
        });
    }

    @Override
    public void hide(@NotNull Region region, @NotNull Player player) {
        Set<Hologram> set = this.holoMap.get(region.getId());
        if (set == null) return;

        RegionBlock regionBlock = region.getRegionBlock();
        if (!regionBlock.isHologramInRegion()) return;

        set.forEach(hologram -> {
            hologram.removeShowPlayer(player);
            hologram.setHidePlayer(player);
            hologram.update(player);
        });
    }
}