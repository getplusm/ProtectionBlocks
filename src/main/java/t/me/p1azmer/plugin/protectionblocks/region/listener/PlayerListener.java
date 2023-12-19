package t.me.p1azmer.plugin.protectionblocks.region.listener;

import io.papermc.paper.event.block.PlayerShearBlockEvent;
import io.papermc.paper.event.player.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.events.BlockBreakRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.PlayerEnterRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.PlayerExitRegionEvent;
import t.me.p1azmer.plugin.protectionblocks.api.events.PlayerInteractRegionBlockEvent;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
    TODO: add support for this https://github.com/IzzelAliz/Arclight
 */
public class PlayerListener extends AbstractListener<ProtectionPlugin> {
    private final RegionManager manager;
    private final Map<Player, Region> playerRegionMap;

    public PlayerListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
        this.playerRegionMap = new ConcurrentHashMap<>();
    }


    @EventHandler(priority = EventPriority.LOW)
    public void updateRegionBlock(PlayerEnterRegionEvent event) {
        event.getRegion().ifPresent(region -> region.updateHologram(event.getPlayer(), true));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void updateRegionBlock(PlayerExitRegionEvent event) {
        event.getRegion().ifPresent(region -> region.updateHologram(event.getPlayer(), false));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void detectRegions(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Region from = this.playerRegionMap.get(player);
        Region to = this.manager.getRegionByLocation(event.getTo());

        if (to != null && to.isExpired()) {
            this.manager.deleteRegion(to, true);
            return;
        }

        if (this.playerRegionMap.containsKey(player) && (to == null || !to.equals(from))) {
            PlayerExitRegionEvent calledEvent = new PlayerExitRegionEvent(player, from);
            this.plugin.getPluginManager().callEvent(calledEvent);
            if (!event.isCancelled()) {
                this.playerRegionMap.remove(player);
            }
        }
        if (to != null && !this.playerRegionMap.containsKey(player)) {
            PlayerEnterRegionEvent calledEvent = new PlayerEnterRegionEvent(player, to);
            this.plugin.getPluginManager().callEvent(calledEvent);
            if (!event.isCancelled()) {
                this.playerRegionMap.put(player, to);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getInteractionPoint() == null) return;

        Block block = event.getInteractionPoint().getBlock();
        Block clickedBlock = event.getClickedBlock();
        Region region = this.manager.getRegionByBlock(block);
        if (region == null) return;
        if (region.isAllowed(event.getPlayer())) {
            if (clickedBlock != null && region.getBlockLocation().equals(clickedBlock.getLocation())) {
                PlayerInteractRegionBlockEvent calledEvent = new PlayerInteractRegionBlockEvent(event.getPlayer(), region);
                this.plugin.getPluginManager().callEvent(calledEvent);
                if (calledEvent.isCancelled()) return;

                region.getRegionMenu().openNextTick(event.getPlayer(), 1);
            }
        } else {
            plugin.getMessage(Lang.REGION_ERROR_INTERACT).send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onRegionBreak(BlockBreakRegionEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        Object tool = event.getBlockOrItem();
        Block block = event.getTargetBlock();

        if (block.getType().isAir()) {
            return;
        }
        Region region = this.manager.getRegionByBlock(block);
        if (region == null) {
            return;
        }

        RegionManager.DamageType damageType = RegionManager.DamageType.HAND;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().isAir() && item.getType().isItem()) {
            damageType = RegionManager.DamageType.TOOLS;
        }


        event.setCancelled(!this.manager.tryDestroyRegion(player, tool, block, damageType, region));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onRegionEnter(PlayerEnterRegionEvent event) {
        event.getRegion().ifPresent(region ->
                plugin.getMessage(Lang.REGION_ENTER_NOTIFY)
                        .replace(region.replacePlaceholders())
                        .send(event.getPlayer())
        );
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onRegionLeave(PlayerExitRegionEvent event) {
        event.getRegion().ifPresent(region ->
                plugin.getMessage(Lang.REGION_EXIT_NOTIFY)
                        .replace(region.replacePlaceholders())
                        .send(event.getPlayer())
        );
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerBucketFill(PlayerBucketEmptyEvent event) {
        Block clicked = event.getBlockClicked();
        BlockFace bf = event.getBlockFace();
        Block check = clicked.getWorld().getBlockAt(clicked.getX() + event.getBlockFace().getModX(), clicked.getY() + bf.getModY(), clicked.getZ() + event.getBlockFace().getModZ());
        Region region = this.manager.getRegionByBlock(check);
        if (region != null && !region.isAllowed(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() == null) return;

        Region region = this.manager.getRegionByBlock(event.getBlock());
        if (region != null && !region.isAllowed(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerOpenSignEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getSign().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerBucketFillEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerBucketEmptyEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerBucketEntityEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getEntity().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerLoomPatternSelectEvent event) {
        if (event.getLoomInventory().getLocation() == null) return;
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getLoomInventory().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerItemFrameChangeEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getItemFrame().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerChangeBeaconEffectEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getBeacon().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerFlowerPotManipulateEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getFlowerpot().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getRightClicked().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerShearBlockEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerInteractAtEntityEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getRightClicked().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInteractBlocked(PlayerHarvestBlockEvent event) {
        event.setCancelled(this.handleInteract(event.getPlayer(), event.getHarvestedBlock().getLocation()));
    }

    public boolean handleInteract(@NotNull LivingEntity entity, @NotNull Location location) {
        Region region = this.manager.getRegionByLocation(location);
        if (region == null) return false;
        return !region.isAllowed(entity.getUniqueId());
    }
}
