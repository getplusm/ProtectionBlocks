package t.me.p1azmer.plugin.protectionblocks.utils;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Cuboid {

    private final Location min;
    private final Location max;
    private final Location center;
    private final boolean infinityY;

    public Cuboid(@NotNull Location center, @NotNull Location loc1, @NotNull Location loc2, boolean infinityY) {
        this.infinityY = infinityY;
        World world = loc1.getWorld();
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = infinityY ?
                world.getMinHeight() :
                Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = infinityY ?
                world.getMaxHeight() :
                Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        this.min = new Location(loc1.getWorld(), minX, minY, minZ);
        this.max = new Location(loc1.getWorld(), maxX, maxY, maxZ);
        this.center = center;
    }

    public boolean contains(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null || !world.equals(this.min.getWorld())) return false;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        int minY = infinityY ? world.getMinHeight() : this.min.getBlockY();
        int maxY = infinityY ? world.getMaxHeight() : this.max.getBlockY();
        return x >= min.getBlockX() && x <= max.getBlockX() &&
                y >= minY && y <= maxY &&
                z >= min.getBlockZ() && z <= max.getBlockZ();
    }

    @NotNull
    public Set<Block> getBlocks() {
        Set<Block> list = new HashSet<>(this.getSize());
        World world = this.center.getWorld();
        if (world == null) return list;

        int minY = infinityY ? world.getMinHeight() : this.min.getBlockY();
        int maxY = infinityY ? world.getMaxHeight() : this.max.getBlockY();

        for (int x = this.min.getBlockX(); x <= this.max.getBlockX(); ++x) {
            for (int y = minY; y <= maxY; ++y) {
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
}