package t.me.p1azmer.plugin.protectionblocks.integration.holograms;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.LocationUtil;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.integration.HologramHandler;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HologramDisplaysHandler implements HologramHandler {
    private final HolographicDisplaysAPI hologramAPI;
    private final Map<String, Set<Hologram>> holoMap;

    public HologramDisplaysHandler(@NotNull ProtectionPlugin plugin) {
        this.hologramAPI = HolographicDisplaysAPI.get(plugin);
        this.holoMap = new HashMap<>();
    }

    @Override
    public void setup() {

    }

    @Override
    public void shutdown() {
        this.holoMap.values().forEach(set -> set.forEach(Hologram::delete));
        this.holoMap.clear();
    }

    @Override
    public void create(@NotNull Region region) {
        Set<Hologram> holograms = this.holoMap.computeIfAbsent(region.getId(), set -> new HashSet<>());
        region.getRegionBlock().ifPresent(regionBlock -> {

            Hologram hologram = this.hologramAPI.createHologram(this.fineLocation(region.getBlockLocation()));
            for (String line : regionBlock.getHologramText(region)) {
                hologram.getLines().appendText(line);
            }
            if (regionBlock.isHologramInRegion()) {
                hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
            } else
                hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.VISIBLE);
            holograms.add(hologram);
        });
    }

    @NotNull
    private Location fineLocation(@NotNull Location location) {
        return LocationUtil.getCenter(location.clone()).add(0D, Config.REGION_HOLOGRAM_Y_OFFSET.get(), 0D);
    }

    @Override
    public void delete(@NotNull Region crate) {
        Set<Hologram> set = this.holoMap.remove(crate.getId());
        if (set == null) return;

        set.forEach(Hologram::delete);
    }

    @Override
    public void show(@NotNull Region region, @NotNull Player player) {
        Set<Hologram> set = new HashSet<>(this.holoMap.getOrDefault(region.getId(), new HashSet<>()));
        if (set.isEmpty()) {
            region.getRegionBlock().ifPresent(regionBlock -> {

                Hologram hologram = this.hologramAPI.createHologram(this.fineLocation(region.getBlockLocation()));
                for (String line : regionBlock.getHologramText(region)) {
                    hologram.getLines().appendText(line);
                }
                set.add(hologram);
            });
        }
        set.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
    }

    @Override
    public void hide(@NotNull Region region, @NotNull Player player) {
        Set<Hologram> set = new HashSet<>(this.holoMap.getOrDefault(region.getId(), new HashSet<>()));
        region.getRegionBlock().ifPresentOrElse(regionBlock -> {
            if (!regionBlock.isHologramInRegion()) return;

            set.forEach(hologram -> hologram.getVisibilitySettings().removeIndividualVisibility(player));
        }, () -> set.forEach(Hologram::delete));
    }

}