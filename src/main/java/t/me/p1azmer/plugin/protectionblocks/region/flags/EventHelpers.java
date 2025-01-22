package t.me.p1azmer.plugin.protectionblocks.region.flags;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.utils.PDCUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.playerBlockTracker.PlayerBlockTracker;
import t.me.p1azmer.plugin.protectionblocks.Keys;
import t.me.p1azmer.plugin.protectionblocks.config.Config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class EventHelpers {

    public final EventHelper<BlockBreakEvent, Material> BLOCK_BREAK = (plugin, event, processor) -> {
        Block block = event.getBlock();
        if (Config.FLAGS_ANTI_GLITCH_TRACK_BLOCKS.get() && PlayerBlockTracker.isTracked(block)) return false;

        Player player = event.getPlayer();
        processor.triggerFlag(player, block.getType(), event);
        return true;
    };

    public final EventHelper<BlockFertilizeEvent, Material> BLOCK_FERTILIZE = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        if (player == null) return false;

        processor.triggerFlag(player, event.getBlock().getType(), event);

        event.getBlocks().forEach(blockState -> {
            processor.triggerFlag(player, blockState.getType(), event);
        });
        return true;
    };

    public final EventHelper<BlockPlaceEvent, Material> BLOCK_PLACE = (plugin, event, processor) -> {
        Block block = event.getBlockPlaced();

        processor.triggerFlag(event.getPlayer(), block.getType(), event);
        return false;
    };


    public final EventHelper<EntityDamageByEntityEvent, EntityDamageEvent.DamageCause> DAMAGE_INFLICT = (plugin, event, processor) -> {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player player)) return false;

        EntityDamageEvent.DamageCause cause = event.getCause();
        double damage = event.getDamage();
        processor.triggerFlag(player, cause, event);
        return true;
    };

    public final EventHelper<EntityDamageEvent, EntityDamageEvent.DamageCause> DAMAGE_RECEIVE = (plugin, event, processor) -> {
        Entity victim = event.getEntity();
        if (!(victim instanceof Player player)) return false;

        EntityDamageEvent.DamageCause cause = event.getCause();
        double damage = event.getDamage();
        processor.triggerFlag(player, cause, event);
        return true;
    };


    public final EventHelper<EntityExplodeEvent, List<Block>> ENTITY_EXPLODE = (plugin, event, processor) -> {
        event.blockList().forEach(block -> plugin.getRegionManager().getOptionalRegionByBlock(block).flatMap(region -> {
            return region.getFlagSetting("entity_explode");
        }).ifPresent(flagSettings -> {
            if (flagSettings.isEnabled()) {
                event.blockList().remove(block);
            }
        }));
        return true;
    };

    public final EventHelper<EntityBreedEvent, EntityType> ENTITY_BREED = (plugin, event, processor) -> {
        LivingEntity breeder = event.getBreeder();
        if (!(breeder instanceof Player player)) return false;

        processor.triggerFlag(player, event.getEntity().getType(), event);
        return true;
    };

    public final EventHelper<EntityDeathEvent, EntityType> ENTITY_KILL = (plugin, event, processor) -> {
        LivingEntity entity = event.getEntity();
        if (PDCUtil.getBoolean(entity, Keys.entityTracked).orElse(false)) return false;

        Player killer = entity.getKiller();
        if (killer == null) return false;

        processor.triggerFlag(killer, entity.getType(), event);
        return true;
    };

    public final EventHelper<EntityDeathEvent, EntityType> ENTITY_SHOOT = (plugin, event, processor) -> {
        LivingEntity entity = event.getEntity();
        if (PDCUtil.getBoolean(entity, Keys.entityTracked).orElse(false)) return false;

        Player killer = entity.getKiller();
        if (killer == null) return false;

        if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent ede)) return false;
        if (!(ede.getDamager() instanceof Projectile)) return false;

        processor.triggerFlag(killer, entity.getType(), event);
        return true;
    };

    public final EventHelper<PlayerShearEntityEvent, EntityType> ENTITY_SHEAR = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();

        processor.triggerFlag(player, entity.getType(), event);
        return true;
    };

    public final EventHelper<EntityTameEvent, EntityType> ENTITY_TAME = (plugin, event, processor) -> {
        Player player = (Player) event.getOwner();
        LivingEntity entity = event.getEntity();

        processor.triggerFlag(player, entity.getType(), event);
        return true;
    };


    public final EventHelper<PlayerItemConsumeEvent, Material> ITEM_CONSUME = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        processor.triggerFlag(player, item.getType(), event);
        return true;
    };

    public final EventHelper<CraftItemEvent, Material> ITEM_CRAFT = (plugin, event, processor) -> {
        if (event.getClick() == ClickType.MIDDLE) return false;

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir()) return false;

        Player player = (Player) event.getWhoClicked();
        ItemStack craft = new ItemStack(item);
        Material type = craft.getType();

        boolean numberKey = event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD;

        if (event.isShiftClick() || numberKey) {
            int has = PlayerUtil.countItem(player, craft);
            plugin.runTask(task -> {
                int now = PlayerUtil.countItem(player, craft);
                int crafted = now - has;
                processor.triggerFlag(player, type, event);
            });
        } else {
            plugin.runTask(task -> {
                ItemStack cursor = event.getCursor();
                if (cursor == null || cursor.getType().isAir()) return;
                if (!cursor.isSimilar(craft) || cursor.getAmount() >= cursor.getMaxStackSize()) return;

                processor.triggerFlag(player, type, event);
            });
        }
        return true;
    };

    public final EventHelper<InventoryClickEvent, Material> ITEM_DISENCHANT = (plugin, event, processor) -> {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.GRINDSTONE) return false;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return false;

        ItemStack result = inventory.getItem(2);
        if (result == null || result.getType().isAir()) return false;

        ItemStack source = inventory.getItem(0);
        if (source == null || result.getType().isAir()) return false;

        if (source.getEnchantments().size() == result.getEnchantments().size()) return false;

        Player player = (Player) event.getWhoClicked();
        processor.triggerFlag(player, result.getType(), event);
        return true;
    };

    public final EventHelper<EnchantItemEvent, Material> ITEM_ENCHANT = (plugin, event, processor) -> {
        Player player = event.getEnchanter();
        ItemStack item = event.getItem();

        processor.triggerFlag(player, item.getType(), event);
        return true;
    };

    public final EventHelper<PlayerFishEvent, Material> ITEM_FISH = (plugin, event, processor) -> {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return false;

        Entity caught = event.getCaught();
        if (!(caught instanceof Item item)) return false;

        Player player = event.getPlayer();
        ItemStack stack = item.getItemStack();
        processor.triggerFlag(player, stack.getType(), event);
        return true;
    };

    public final EventHelper<FurnaceExtractEvent, Material> ITEM_FURNACE = (plugin, event, processor) -> {
        Player player = event.getPlayer();

        Material material = event.getItemType();
        int amount = event.getItemAmount();

        processor.triggerFlag(player, material, event);
        return true;
    };

    public final EventHelper<InventoryClickEvent, Material> ITEM_TRADE = (plugin, event, processor) -> {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.MERCHANT) return false;

        MerchantInventory merchant = (MerchantInventory) inventory;
        MerchantRecipe recipe = merchant.getSelectedRecipe();
        if (recipe == null) return false;

        Player player = (Player) event.getWhoClicked();
        ItemStack result = recipe.getResult();
        int uses = recipe.getUses();
        int userHas = PlayerUtil.countItem(player, result);

        plugin.runTask(task -> {
            int uses2 = recipe.getUses();
            if (uses2 <= uses) return;

            int amount = 1;
            if (event.isShiftClick()) {
                int resultSize = result.getAmount();
                int userNow = PlayerUtil.countItem(player, result);
                int diff = userNow - userHas;
                amount = (int) ((double) diff / (double) resultSize);
            }

            processor.triggerFlag(player, result.getType(), event);
        });
        return true;
    };


    public final EventHelper<BrewEvent, PotionEffectType> POTION_BREW = (plugin, event, processor) -> {
        BrewerInventory inventory = event.getContents();

        BrewingStand stand = inventory.getHolder();
        if (stand == null) return false;

        String uuidRaw = PDCUtil.getString(stand, Keys.brewingHolder).orElse(null);
        UUID uuid = uuidRaw == null ? null : UUID.fromString(uuidRaw);
        if (uuid == null) return false;

        Player player = plugin.getServer().getPlayer(uuid);
        if (player == null) return false;

        int[] slots = new int[]{0, 1, 2};

        plugin.runTask(task -> {
            for (int slot : slots) {
                ItemStack item = inventory.getItem(slot);
                if (item == null || item.getType().isAir()) continue;

                ItemMeta meta = item.getItemMeta();
                if (!(meta instanceof PotionMeta potionMeta)) continue;

                PotionType potionType;
                if (Version.isAtLeast(Version.V1_20_R2)) {
                    for (PotionEffect effect : potionMeta.getBasePotionType().getPotionEffects()) {
                        processor.triggerFlag(player, effect.getType(), event);
                    }
                } else {
                    potionType = potionMeta.getBasePotionData().getType();
                    if (potionType.getEffectType() != null) {
                        processor.triggerFlag(player, potionType.getEffectType(), event);
                    }
                }

                potionMeta.getCustomEffects().forEach(effect -> {
                    processor.triggerFlag(player, effect.getType(), event);
                });
            }
        });
        return true;
    };

    public final EventHelper<PlayerItemConsumeEvent, PotionEffectType> POTION_DRINK = (plugin, event, processor) -> {
        Player player = event.getPlayer();
        processor.triggerFlag(player, null, event);
        return true;
    };


    public final EventHelper<ProjectileLaunchEvent, EntityType> PROJECTILE_LAUNCH = (plugin, event, processor) -> {
        Projectile projectile = event.getEntity();
        ProjectileSource source = projectile.getShooter();
        if (!(source instanceof Player player)) return false;

        processor.triggerFlag(player, projectile.getType(), event);
        return true;
    };


    public final EventHelper<InventoryClickEvent, Enchantment> ENCHANT_REMOVE = (plugin, event, processor) -> {
        Inventory inventory = event.getInventory();
        if (inventory.getType() != InventoryType.GRINDSTONE) return false;
        if (event.getRawSlot() != 2 || event.getClick() == ClickType.MIDDLE) return false;

        ItemStack result = inventory.getItem(2);
        if (result == null || result.getType().isAir()) return false;

        ItemStack source = inventory.getItem(0);
        if (source == null || result.getType().isAir()) return false;

        var sourceEnchants = new HashSet<>(source.getEnchantments().keySet());
        var resultEnchants = new HashSet<>(result.getEnchantments().keySet());
        if (sourceEnchants.size() == resultEnchants.size()) return false;

        sourceEnchants.removeAll(resultEnchants);

        Player player = (Player) event.getWhoClicked();
        sourceEnchants.forEach(enchantment -> {
            processor.triggerFlag(player, enchantment, event);
        });
        return true;
    };

    public final EventHelper<EnchantItemEvent, Enchantment> ENCHANT_GET = (plugin, event, processor) -> {
        Player player = event.getEnchanter();

        event.getEnchantsToAdd().keySet().forEach(enchantment -> {
            processor.triggerFlag(player, enchantment, event);
        });
        return true;
    };
}