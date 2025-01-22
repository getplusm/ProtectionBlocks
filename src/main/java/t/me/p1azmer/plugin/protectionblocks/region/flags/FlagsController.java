package t.me.p1azmer.plugin.protectionblocks.region.flags;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.region.flags.self.RegionFlagSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Big thanks nulli0n (NightExpress)
 * https://github.com/nulli0n
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlagsController extends AbstractManager<ProtectionPlugin> {

    Map<String, Flag<?, ?>> flagsMap = new HashMap<>();

    public FlagsController(@NotNull ProtectionPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        // Block Material related
        //registerAction(BlockBreakEvent.class, EventPriority.HIGHEST, Flags.BLOCK_BREAK);
        registerAction(BlockFertilizeEvent.class, EventPriority.MONITOR, Flags.BLOCK_FERTILIZE);
        //registerAction(BlockPlaceEvent.class, EventPriority.MONITOR, Flags.BLOCK_PLACE);

        // Damage Cause related
        registerAction(EntityDamageByEntityEvent.class, EventPriority.MONITOR, Flags.DAMAGE_INFLICT);
        registerAction(EntityDamageEvent.class, EventPriority.MONITOR, Flags.DAMAGE_RECEIVE);

        // Entity Type related
        //registerAction(EntityExplodeEvent.class, EventPriority.MONITOR, Flags.ENTITY_EXPLODE);
        registerAction(EntityBreedEvent.class, EventPriority.MONITOR, Flags.ENTITY_BREED);
        registerAction(EntityDeathEvent.class, EventPriority.MONITOR, Flags.ENTITY_KILL);
        registerAction(EntityDeathEvent.class, EventPriority.MONITOR, Flags.ENTITY_SHOOT);
        registerAction(PlayerShearEntityEvent.class, EventPriority.MONITOR, Flags.ENTITY_SHEAR);
        registerAction(EntityTameEvent.class, EventPriority.MONITOR, Flags.ENTITY_TAME);
        registerAction(ProjectileLaunchEvent.class, EventPriority.MONITOR, Flags.PROJECTILE_LAUNCH);

        // Item Material related
        registerAction(PlayerItemConsumeEvent.class, EventPriority.MONITOR, Flags.ITEM_CONSUME);
        registerAction(CraftItemEvent.class, EventPriority.MONITOR, Flags.ITEM_CRAFT);
        registerAction(InventoryClickEvent.class, EventPriority.MONITOR, Flags.ITEM_DISENCHANT);
        registerAction(EnchantItemEvent.class, EventPriority.MONITOR, Flags.ITEM_ENCHANT);
        registerAction(PlayerFishEvent.class, EventPriority.MONITOR, Flags.ITEM_FISH);
        registerAction(FurnaceExtractEvent.class, EventPriority.MONITOR, Flags.ITEM_FURNACE);
        registerAction(InventoryClickEvent.class, EventPriority.MONITOR, Flags.ITEM_TRADE);

        // PotionEffectType related
        registerAction(BrewEvent.class, EventPriority.MONITOR, Flags.POTION_BREW);
        registerAction(PlayerItemConsumeEvent.class, EventPriority.MONITOR, Flags.POTION_DRINK);

        // Enchantment related
        registerAction(EnchantItemEvent.class, EventPriority.MONITOR, Flags.ENCHANT_GET);
        registerAction(InventoryClickEvent.class, EventPriority.MONITOR, Flags.ENCHANT_REMOVE);
    }


    @Override
    protected void onShutdown() {
        allFlagsSaveSettings();
        flagsMap.clear();
    }

    public <E extends Event, O> void registerAction(@NotNull Class<E> eventClass,
                                                    @NotNull EventPriority priority,
                                                    @NotNull Flag<E, O> flag) {

        if (!flag.loadSettings(this.plugin)) return;

        WrappedEvent<E, O> event = new WrappedEvent<>(plugin, eventClass, flag);
        plugin.getPluginManager().registerEvent(eventClass, event, priority, event, plugin, true);
        flagsMap.put(flag.getName(), flag);
    }

    @Nullable
    public Flag<?, ?> getActionType(@NotNull String name) {
        return flagsMap.get(name.toLowerCase());
    }

    public @NotNull Set<RegionFlagSettings> setupDefaultRegionFlags(@NotNull Set<RegionFlagSettings> regionFlags) {
        flagsMap.values().forEach(actionType -> {
            regionFlags.add(new RegionFlagSettings(actionType, Set.of(Config.getDefaultMemberRole()), actionType.isEnabledDefault(), true));
        });
        return regionFlags;
    }

    public void allFlagsSaveSettings() {
        flagsMap.values().forEach(actionType -> actionType.saveSettings(this.plugin));
    }
}
