package t.me.p1azmer.plugin.protectionstones.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionstones.Keys;
import t.me.p1azmer.plugin.protectionstones.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionstones.utils.Cuboid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Region extends AbstractConfigHolder<ProtectionPlugin> implements IPlaceholderMap {
    private Location blockLocation;
    private Cuboid cuboid;
    private UUID owner;
    private List<UUID> members;
    private long createTime;
    private long lastDeposit;
    private String regionBlockId;
    private final PlaceholderMap placeholderMap;
    private final RegionManager manager;

    private RegionBlock regionBlock;

    public Region(@NotNull RegionManager manager, @NotNull JYML cfg) {
        super(manager.plugin(), cfg);
        this.manager = manager;

        this.members = new ArrayList<>();
        this.placeholderMap = new PlaceholderMap();
    }

    @Override
    public boolean load() {
        Location from = cfg.getLocation("Bounds.From");
        Location to = cfg.getLocation("Bounds.To");
        if (from != null && to != null) {
            this.cuboid = new Cuboid(from, to);
        }

        this.blockLocation = this.cfg.getLocation("Block.Location");
        this.regionBlockId = this.cfg.getString("Block.Id", "");
        if (this.regionBlockId.isEmpty() || this.manager.getRegionBlockById(this.regionBlockId) == null) {
            this.plugin.error("Cannot load the Region but Region Block '" + this.regionBlockId + "' not Created or Loaded!");
            return false;
        }


        this.owner = UUID.fromString(this.cfg.getString("Owner", ""));
        this.members = this.cfg.getStringList("Members").stream().map(UUID::fromString).collect(Collectors.toList());
        this.createTime = this.cfg.getLong("Time.Create");
        this.lastDeposit = this.cfg.getLong("Time.Last_Deposit");
        return true;
    }

    @Override
    protected void onSave() {
        cfg.set("Block.Location", this.getBlockLocation());
        cfg.setComments("Block.Id", "Id for Region Block");
        cfg.set("Block.Id", this.getRegionBlockId());
        cfg.set("Bounds.From", this.getCuboid().getMin());
        cfg.set("Bounds.To", this.getCuboid().getMax());
        cfg.set("Owner", this.getOwner().toString());
        cfg.set("Members", this.getMembers().stream().map(UUID::toString).collect(Collectors.toList()));
        cfg.set("Time.Create", this.getCreateTime());
        cfg.set("Time.Last_Deposit", this.getLastDeposit());
    }

    public void clear() {
        Block block = this.getBlockLocation().getBlock();
        block.removeMetadata(Keys.REGION_BLOCK.getKey(), this.plugin);
    }

    @NotNull
    public Location getBlockLocation() {
        return blockLocation;
    }

    @NotNull
    public Cuboid getCuboid() {
        return cuboid;
    }

    @NotNull
    public String getRegionBlockId() {
        return regionBlockId;
    }

    @NotNull
    public UUID getOwner() {
        return owner;
    }

    @NotNull
    public List<UUID> getMembers() {
        return members;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastDeposit() {
        return lastDeposit;
    }

    @NotNull
    public RegionBlock getRegionBlock() {
        if (this.regionBlock == null)
            this.regionBlock = this.manager.getRegionBlockById(this.getRegionBlockId());
        assert regionBlock != null; // cannot be null but we loaded it without errors
        return regionBlock;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void setBlockLocation(@NotNull Location location) {
        this.blockLocation = location;
        Location lowerLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Location upperLocation = new Location(location.getWorld(),  location.getBlockX(), location.getBlockY(), location.getBlockZ());

        lowerLocation.subtract(this.getRegionBlock().getRegionSize(), this.getRegionBlock().getRegionSize(), this.getRegionBlock().getRegionSize());
        upperLocation.add(this.getRegionBlock().getRegionSize(), this.getRegionBlock().getRegionSize(), this.getRegionBlock().getRegionSize());

        // Добавьте код для убедительности в том, что верхняя точка действительно выше нижней
        if (lowerLocation.getY() > upperLocation.getY()) {
            double temp = lowerLocation.getY();
            lowerLocation.setY(upperLocation.getY());
            upperLocation.setY(temp);
        }
        Cuboid cuboid = new Cuboid(lowerLocation, upperLocation);
        // debug
        this.plugin.error("Created new cuboid. Points: " + upperLocation + ": upper\n"+ lowerLocation + ": lower");
        this.setCuboid(cuboid);
    }

    public void setCuboid(@NotNull Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public void setOwner(@NotNull UUID owner) {
        this.owner = owner;
    }

    public void setMembers(@NotNull List<UUID> members) {
        this.members = members;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public void setLastDeposit(long lastDeposit) {
        this.lastDeposit = lastDeposit;
    }

    public void setRegionBlockId(@NotNull String regionBlockId) {
        this.regionBlockId = regionBlockId;
    }

    public void addMember(@NotNull UUID user) {
        this.getMembers().add(user);
        this.save();
    }

    public void removeMember(@NotNull UUID user) {
        this.getMembers().remove(user);
        this.save();
    }

    public boolean isAllowed(@NotNull Player player) {
        return this.getMembers().contains(player.getUniqueId());
    }

    public void updateDeposit() {
        this.lastDeposit = System.currentTimeMillis();
        this.save();
    }
}
