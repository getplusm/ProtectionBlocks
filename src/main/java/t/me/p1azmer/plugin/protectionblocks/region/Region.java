package t.me.p1azmer.plugin.protectionblocks.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.plugin.protectionblocks.Keys;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.utils.Cuboid;

import java.util.*;
import java.util.stream.Collectors;

public class Region extends AbstractConfigHolder<ProtectionPlugin> implements IPlaceholderMap {
    private Location blockLocation;
    private Cuboid cuboid;
    private UUID owner;
    private String ownerName;
    private List<UUID> members;
    private long createTime;
    private long lastDeposit;
    private String regionBlockId;
    private final PlaceholderMap placeholderMap;
    private final RegionManager manager;
    private RegionBlock regionBlock;
    private int blockHealth;

    public Region(@NotNull RegionManager manager, @NotNull JYML cfg) {
        super(manager.plugin(), cfg);
        this.manager = manager;

        this.members = new ArrayList<>();
        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.REGION_ID, this::getId)
                .add(Placeholders.REGION_OWNER_NAME, () -> Colorizer.apply(this.getOwnerName()))
                .add(Placeholders.REGION_HEALTH, () -> String.valueOf(this.getBlockHealth()))
                .add(Placeholders.REGION_LOCATION, () -> Placeholders.forLocation(this.getBlockLocation()).apply(Placeholders.LOCATION_WORLD + ": " + Placeholders.LOCATION_X + ", " + Placeholders.LOCATION_Y + ", " + Placeholders.LOCATION_Z))
        ;
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
        this.ownerName = this.cfg.getString("Owner_Name", "");
        this.members = this.cfg.getStringList("Members").stream().map(UUID::fromString).collect(Collectors.toList());
        this.createTime = this.cfg.getLong("Time.Create");
        this.lastDeposit = this.cfg.getLong("Time.Last_Deposit");
        this.blockHealth = this.cfg.getInt("Cache.Health", 1);
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
        cfg.set("Cache.Health", this.getBlockHealth());
        cfg.set("Owner_Name", this.getOwnerName());
    }

    public void loadLocations() {
        Block block = this.getBlockLocation().getBlock();
        this.getRegionBlock().ifPresent(regionBlock -> {
            if (!block.getType().equals(regionBlock.getItem().getType()))
                block.setType(regionBlock.getItem().getType());
            regionBlock.updateHologram(this);
        });
        block.setMetadata(Keys.REGION_BLOCK.getKey(), new FixedMetadataValue(this.plugin, this.getId()));
    }

    public void clear() {
        this.getRegionBlock().ifPresent(regionBlock -> regionBlock.removeHologram(this));
    }

    @NotNull
    public Location getBlockLocation() {
        return blockLocation;
    }

    public boolean isRegionBlock(@NotNull Block block) {
        MetadataValue metadataValue = block.hasMetadata(Keys.REGION_BLOCK.getKey()) ? block.getMetadata(Keys.REGION_BLOCK.getKey()).get(0) : null;
        return this.getBlockLocation().getBlock().equals(block) || metadataValue != null && metadataValue.asString().equals(this.getId());
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
    public String getOwnerName() {
        return ownerName;
    }

    @NotNull
    public List<UUID> getMembers() {
        return members;
    }

    @NotNull
    public Collection<Player> getOnlineMembers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers().stream().filter(f -> this.getMembers().contains(f.getUniqueId()) || f.getUniqueId().equals(this.getOwner())).collect(Collectors.toList()));
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getLastDeposit() {
        return lastDeposit;
    }

    public int getBlockHealth() {
        if (this.blockHealth < 0) this.blockHealth = 0;
        return blockHealth;
    }

    public void setBlockHealth(int blockHealth) {
        this.blockHealth = blockHealth;
    }

    public void takeBlockHealth(@NotNull RegionManager.DamageType damageType) {

        this.blockHealth -= 1;
        if (this.blockHealth < 0) this.blockHealth = 0;
    }

    @NotNull
    public Optional<RegionBlock> getRegionBlock() {
        if (this.regionBlock == null)
            this.regionBlock = this.manager.getRegionBlockById(this.getRegionBlockId());
        return Optional.ofNullable(regionBlock);
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void setBlockLocation(@NotNull Location location) {
        this.blockLocation = location;
        this.getRegionBlock().ifPresent(regionBlock -> {

            Location lowerLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Location upperLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

            lowerLocation.subtract(regionBlock.getRegionSize(), regionBlock.getRegionSize(), regionBlock.getRegionSize());
            upperLocation.add(regionBlock.getRegionSize(), regionBlock.getRegionSize(), regionBlock.getRegionSize());

            if (lowerLocation.getY() > upperLocation.getY()) {
                double temp = lowerLocation.getY();
                lowerLocation.setY(upperLocation.getY());
                upperLocation.setY(temp);
            }

            Cuboid cuboid = new Cuboid(lowerLocation, upperLocation);
            this.setCuboid(cuboid);
        });
    }

    public void setCuboid(@NotNull Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    public void setOwner(@NotNull UUID owner) {
        this.owner = owner;
    }

    public void setOwnerName(@NotNull String ownerName) {
        this.ownerName = ownerName;
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
        return this.getOwner().equals(player.getUniqueId()) || this.getMembers().contains(player.getUniqueId());
    }

    public void updateDeposit() {
        this.lastDeposit = System.currentTimeMillis();
        this.save();
    }

    public void broadcast(@NotNull String... message) {
        this.getOnlineMembers().forEach(f -> f.sendMessage(Colorizer.apply(Arrays.asList(message)).toArray(String[]::new)));
    }

    public void updateHologram(@NotNull Player player, boolean show) {
        if (this.plugin.getHologramHandler() == null) return;

        if (show) {
            this.plugin.getHologramHandler().show(this, player);
        } else
            this.plugin.getHologramHandler().hide(this, player);

    }

}