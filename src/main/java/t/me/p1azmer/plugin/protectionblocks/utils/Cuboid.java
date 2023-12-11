package t.me.p1azmer.plugin.protectionblocks.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Cuboid {

    private final Location min;
    private final Location max;
    private final Location center;

    public Cuboid(@NotNull Location loc1, @NotNull Location loc2) {
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        this.min = new Location(loc1.getWorld(), minX, minY, minZ);
        this.max = new Location(loc1.getWorld(), maxX, maxY, maxZ);

        double cx = minX + (maxX - minX) / 2D;
        double cy = minY + (maxY - minY) / 2D;
        double cz = minZ + (maxZ - minZ) / 2D;

        this.center = new Location(loc1.getWorld(), cx, cy, cz);
    }

    public boolean contains(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null || !world.equals(this.min.getWorld())) return false;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= min.getBlockX() && x <= max.getBlockX() &&
                y >= min.getBlockY() && y <= max.getBlockY() &&
                z >= min.getBlockZ() && z <= max.getBlockZ();
    }

    @NotNull
    public List<Block> getBlocks() {
        List<Block> list = new ArrayList<>(this.getSize());
        World world = this.center.getWorld();
        if (world == null) return list;

        for (int x = this.min.getBlockX(); x <= this.max.getBlockX(); ++x) {
            for (int y = this.min.getBlockY(); y <= this.max.getBlockY(); ++y) {
                for (int z = this.min.getBlockZ(); z <= this.max.getBlockZ(); ++z) {
                    Block blockAt = world.getBlockAt(x, y, z);
                    list.add(blockAt);
                }
            }
        }

        return list;
    }

    public int getSize() {
        int dx = max.getBlockX() - min.getBlockX() + 1;
        int dy = max.getBlockY() - min.getBlockY() + 1;
        int dz = max.getBlockZ() - min.getBlockZ() + 1;
        return dx * dy * dz;
    }

    @NotNull
    public Location getMin() {
        return this.min;
    }

    @NotNull
    public Location getMax() {
        return this.max;
    }

    @NotNull
    public Location getCenter() {
        return this.center;
    }
}