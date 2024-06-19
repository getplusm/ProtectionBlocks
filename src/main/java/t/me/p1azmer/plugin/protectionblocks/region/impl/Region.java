package t.me.p1azmer.plugin.protectionblocks.region.impl;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.lang.LangMessage;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.engine.utils.TimeUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionblocks.Keys;
import t.me.p1azmer.plugin.protectionblocks.Perms;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;
import t.me.p1azmer.plugin.protectionblocks.region.menu.RegionMembersMenu;
import t.me.p1azmer.plugin.protectionblocks.region.menu.RegionMenu;
import t.me.p1azmer.plugin.protectionblocks.utils.Cuboid;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Region extends AbstractConfigHolder<ProtectionPlugin> implements Placeholder {
    private Location blockLocation;
    private Cuboid cuboid;
    private UUID ownerUUID;
    private String ownerName;
    private final List<RegionMember> members;
    private long createTime;
    private long lastDeposit;
    private String regionBlockId;
    private final PlaceholderMap placeholders;
    private final RegionManager manager;
    private RegionBlock regionBlock;
    private int blockHealth;
    private int regionSize;

    // cache
    private RegionMenu regionMenu;
    private RegionMembersMenu membersMenu;

    public Region(@NotNull RegionManager manager, @NotNull JYML cfg) {
        super(manager.plugin(), cfg);
        this.manager = manager;

        this.createTime = System.currentTimeMillis();
        this.members = new ArrayList<>();
        this.placeholders = new PlaceholderMap()
          .add(Placeholders.REGION_ID, this::getId)
          .add(Placeholders.REGION_OWNER_NAME, () -> Colorizer.apply(this.getOwnerName()))
          .add(Placeholders.REGION_HEALTH, () -> String.valueOf(this.getBlockHealth()))
          .add(Placeholders.REGION_SIZE, () -> String.valueOf(this.getBlockHealth()))
          .add(Placeholders.REGION_MEMBERS_AMOUNT, () -> NumberUtil.format(this.getMembers().size()))
          .add(Placeholders.REGION_EXPIRE_IN, () -> this.getLastDeposit() == -1 ? Colorizer.apply(Config.UNBREAKABLE.get()) : TimeUtil.formatTimeLeft(this.getLastDeposit()))
          .add(Placeholders.REGION_CREATION_TIME, () -> TimeUtil.formatTime(System.currentTimeMillis() - this.getCreateTime()))
          .add(Placeholders.REGION_LOCATION, () -> Placeholders.forLocation(this.getBlockLocation()).apply((Placeholders.LOCATION_WORLD + ": " + Placeholders.LOCATION_X + ", " + Placeholders.LOCATION_Y + ", " + Placeholders.LOCATION_Z).replace(Placeholders.LOCATION_WORLD, LangManager.getWorld(this.getBlockLocation().getWorld()))));
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


        this.ownerUUID = UUID.fromString(this.cfg.getString("Owner", ""));
        this.ownerName = this.cfg.getString("Owner_Name", "");
        this.createTime = this.cfg.getLong("Time.Create");
        this.lastDeposit = this.cfg.getLong("Time.Last_Deposit");
        this.blockHealth = this.cfg.getInt("Cache.Health", 1);
        this.regionSize = this.cfg.getInt("Cache.Region.Size", 1); // visible settings
        for (String sId : cfg.getSection("Members.List")) {
            this.getMembers().add(RegionMember.read(this.cfg, "Members.List." + sId));
        }
        return true;
    }

    @Override
    protected void onSave() {
        cfg.set("Block.Location", this.getBlockLocation());
        cfg.setComments("Block.Id", "Id for Region Block");
        cfg.set("Block.Id", this.getRegionBlockId());
        cfg.set("Bounds.From", this.getCuboid().getMin());
        cfg.set("Bounds.To", this.getCuboid().getMax());
        cfg.set("Owner", this.getOwnerUUID().toString());
        cfg.set("Time.Create", this.getCreateTime());
        cfg.set("Time.Last_Deposit", this.getLastDeposit());
        cfg.set("Cache.Health", this.getBlockHealth());
        cfg.set("Cache.Region.Size", this.getRegionSize());
        cfg.set("Owner_Name", this.getOwnerName());
        int i = 0;
        for (RegionMember member : this.getMembers()) {
            member.write(cfg, "Members.List." + (i++));
        }
    }

    public void loadLocations() {
        if (!this.getBlockLocation().isChunkLoaded()) {
            this.getBlockLocation().getWorld().loadChunk(this.getBlockLocation().getChunk());
        }
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
        if (this.regionMenu != null)
            this.regionMenu.clear();
        if (this.membersMenu != null) {
            this.membersMenu.clear();
        }
    }

    @NotNull
    public RegionMenu getRegionMenu() {
        if (this.regionMenu == null)
            this.regionMenu = new RegionMenu(this);
        return regionMenu;
    }

    @NotNull
    public RegionMembersMenu getMembersMenu() {
        if (this.membersMenu == null) {
            this.membersMenu = new RegionMembersMenu(this);
        }
        return this.membersMenu;
    }

    public boolean isRegionBlock(@NotNull Block block) {
        MetadataValue metadataValue = block.hasMetadata(Keys.REGION_BLOCK.getKey()) ? block.getMetadata(Keys.REGION_BLOCK.getKey()).get(0) : null;
        return this.getBlockLocation().getBlock().equals(block) || metadataValue != null && metadataValue.asString().equals(this.getId());
    }

    public boolean isOwner(@NotNull UUID uuid) {
        return this.getOwnerUUID().equals(uuid);
    }

    @Nullable
    public RegionMember getMemberByName(@NotNull String name) {
        return this.getMembers().stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
    }

    @Nullable
    public RegionMember getMemberById(@NotNull UUID uuid) {
        return this.getMembers().stream().filter(f -> f.getId().equals(uuid)).findFirst().orElse(null);
    }

    @Nullable
    public RegionMember getMemberByPlayer(@NotNull Player player) {
        return this.getMemberById(player.getUniqueId());
    }

    public boolean isMember(@NotNull UUID id) {
        return this.getMemberById(id) != null;
    }


    @NotNull
    public Collection<Player> getOnlineMembers() {
        return this.getMembers()
                   .stream()
                   .filter(founder -> founder.getPlayer() != null && founder.getPlayer().isOnline())
                   .map(RegionMember::getPlayer)
                   .collect(Collectors.toList());
    }

    public Optional<Player> getOwnerPlayer(){
        return Optional.ofNullable(Bukkit.getPlayer(this.getOwnerUUID()));
    }

    public boolean isExpired() {
        if (this.getLastDeposit() == -1) return false;

        return System.currentTimeMillis() >= this.getLastDeposit();
    }

    public int getBlockHealth() {
        if (this.blockHealth < 0) this.blockHealth = 0;
        return blockHealth;
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

    public void setBlockLocation(@NotNull Location location, @NotNull RegionBlock regionBlock, @NotNull Player player) {
        this.blockLocation = location;


        int size = regionBlock.isGroupSizeEnabled() && regionBlock.getGroupSize() != null ? regionBlock.getGroupSize().getGreatest(player) : regionBlock.getRegionSize();
        Location lowerLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Location upperLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

        lowerLocation.subtract(size, size, size);
        upperLocation.add(size, size, size);

        if (lowerLocation.getY() > upperLocation.getY()) {
            double temp = lowerLocation.getY();
            lowerLocation.setY(upperLocation.getY());
            upperLocation.setY(temp);
        }

        Cuboid cuboid = new Cuboid(lowerLocation, upperLocation);
        this.setCuboid(cuboid);
        this.setRegionSize(size);
    }

    public void addDeposit(long depositTime) {
        this.lastDeposit += depositTime;
        this.save();
    }

    public void setRegionBlockId(@NotNull String regionBlockId) {
        this.regionBlockId = regionBlockId;
    }

    public void addMember(@NotNull Player player) {
        this.getMembers().add(RegionMember.of(player));
        this.save();
    }

    public void removeMember(@NotNull Player player) {
        RegionMember member = this.getMemberByPlayer(player);
        if (member == null) return;
        this.removeMember(member);
    }

    public void removeMember(@NotNull RegionMember member) {
        this.getMembers().remove(member);
        this.save();
    }

    public boolean isAllowed(@NotNull Player player) {
        if (this.isExpired()) {
            this.manager.deleteRegion(this, true);
            return true;
        }
        return player.hasPermission(Perms.BYPASS_REGION_MANIPULATION) || this.isOwner(player.getUniqueId()) || this.isMember(player.getUniqueId());
    }

    public boolean isAllowed(@NotNull UUID uuid) {
        if (this.isExpired()) {
            this.manager.deleteRegion(this, true);
            return true;
        }
        return this.isOwner(uuid) || this.isMember(uuid);
    }

    public void updateDeposit() {
        this.lastDeposit = System.currentTimeMillis();
        this.save();
    }

    public void broadcast(@NotNull LangMessage... message) {
        this.getOnlineMembers().forEach(player -> Arrays.stream(message)
                                                        .toList()
                                                        .forEach(langMessage -> langMessage.send(player)));
    }

    public void updateHologram(@NotNull Player player, boolean show) {
        if (this.plugin.getHologramHandler() == null) return;

        if (show) this.plugin.getHologramHandler().show(this, player);
        else this.plugin.getHologramHandler().hide(this, player);
    }
}