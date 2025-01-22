package t.me.p1azmer.plugin.protectionblocks.region.flags;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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
import org.bukkit.potion.PotionEffectType;
import t.me.p1azmer.engine.utils.ItemUtil;

import java.util.List;

@UtilityClass
public class Flags {

    public final Flag<BlockBreakEvent, Material> BLOCK_BREAK = Flag.create(
            "block_break", true, EventHelpers.BLOCK_BREAK
    ).setIcon(ItemUtil.getSkinHead("1e1d4bc469d29d22a7ef6d21a61b451291f21bf51fd167e7fd07b719512e87a1"));

    public final Flag<BlockFertilizeEvent, Material> BLOCK_FERTILIZE = Flag.create(
            "block_fertilize", true, EventHelpers.BLOCK_FERTILIZE
    ).setIcon(ItemUtil.getSkinHead("b451845943fd0c07f629711e3401a71a31cd371cc3cb36f3f9637b0e759cc48a"));

    public final Flag<BlockPlaceEvent, Material> BLOCK_PLACE = Flag.create(
            "block_place", true, EventHelpers.BLOCK_PLACE
    ).setIcon(ItemUtil.getSkinHead("c0d20f6033a58626841c80dc25e604fc22f8dba8eadad7de921330ff26e1659c"));

    public final Flag<EntityDamageByEntityEvent, EntityDamageEvent.DamageCause> DAMAGE_INFLICT = Flag.create(
            "inflict_damage", false, EventHelpers.DAMAGE_INFLICT
    ).setIcon(ItemUtil.getSkinHead("ea5fb227a87a953e7225fbe6c1f4fb4884d9c023c1906654b8dca3a9290a54d3"));

    public final Flag<EntityDamageEvent, EntityDamageEvent.DamageCause> DAMAGE_RECEIVE = Flag.create(
            "receive_damage", false, EventHelpers.DAMAGE_RECEIVE
    ).setIcon(ItemUtil.getSkinHead("f25def6560cacfeeaa03886e525324ddddae49eb6eb749dc6df207ca3a135ad5"));

    public final Flag<EntityExplodeEvent, List<Block>> ENTITY_EXPLODE = Flag.create(
            "entity_explode", true, EventHelpers.ENTITY_EXPLODE
    ).setIcon(ItemUtil.getSkinHead("d6f989a7341ba82eb01ccc1e3ba44fcbd49dde1d47ec96d9f4a8f209dd665243"));

    public final Flag<EntityBreedEvent, EntityType> ENTITY_BREED = Flag.create(
            "breed_entity", true, EventHelpers.ENTITY_BREED
    ).setIcon(ItemUtil.getSkinHead("319b7fcd8ab72e293edbdb1c615d658908d2ed354880eb6964634a4657898c60"));

    public final Flag<EntityDeathEvent, EntityType> ENTITY_KILL = Flag.create(
            "kill_entity", true, EventHelpers.ENTITY_KILL
    ).setIcon(ItemUtil.getSkinHead("783aaaee22868cafdaa1f6f4a0e56b0fdb64cd0aeaabd6e83818c312ebe66437"));

    public final Flag<EntityDeathEvent, EntityType> ENTITY_SHOOT = Flag.create(
            "shoot_entity", true, EventHelpers.ENTITY_SHOOT
    ).setIcon(ItemUtil.getSkinHead("c787b7afb5a59953975bba2473749b601d54d6f93ceac7a02ac69aae7f9b8"));

    public final Flag<PlayerShearEntityEvent, EntityType> ENTITY_SHEAR = Flag.create(
            "shear_entity", true, EventHelpers.ENTITY_SHEAR
    ).setIcon(ItemUtil.getSkinHead("a723893df4cfb9c7240fc47b560ccf6ddeb19da9183d33083f2c71f46dad290a"));

    public final Flag<EntityTameEvent, EntityType> ENTITY_TAME = Flag.create(
            "tame_entity", true, EventHelpers.ENTITY_TAME
    ).setIcon(ItemUtil.getSkinHead("28d408842e76a5a454dc1c7e9ac5c1a8ac3f4ad34d6973b5275491dff8c5c251"));

    public final Flag<PlayerItemConsumeEvent, Material> ITEM_CONSUME = Flag.create(
            "consume_item", false, EventHelpers.ITEM_CONSUME
    ).setIcon(ItemUtil.getSkinHead("10d71fe099e8bee600213ade9e38bc3b9217dd1dc3b44aab414635bcb68ac548"));

    public final Flag<CraftItemEvent, Material> ITEM_CRAFT = Flag.create(
            "craft_item", false, EventHelpers.ITEM_CRAFT
    ).setIcon(ItemUtil.getSkinHead("4c36045208f9b5ddcf8c4433e424b1ca17b94f6b96202fb1e5270ee8d53881b1"));

    public final Flag<InventoryClickEvent, Material> ITEM_DISENCHANT = Flag.create(
            "disenchant_item", true, EventHelpers.ITEM_DISENCHANT
    ).setIcon(ItemUtil.getSkinHead("8e99bfa61fe552f1e6636b03fbe40f4e470c3b3cb14f70e9012813790ead568f"));

    public final Flag<EnchantItemEvent, Material> ITEM_ENCHANT = Flag.create(
            "enchant_item", true, EventHelpers.ITEM_ENCHANT
    ).setIcon(ItemUtil.getSkinHead("b2f79016cad84d1ae21609c4813782598e387961be13c15682752f126dce7a"));

    public final Flag<PlayerFishEvent, Material> ITEM_FISH = Flag.create(
            "fish_item", false, EventHelpers.ITEM_FISH
    ).setIcon(ItemUtil.getSkinHead("1352df85a02d7fac5dca72dfbc6ba8ac0a7f96208bc8048247791ef2216f5c94"));

    public final Flag<FurnaceExtractEvent, Material> ITEM_FURNACE = Flag.create(
            "smelt_item", true, EventHelpers.ITEM_FURNACE
    ).setIcon(ItemUtil.getSkinHead("53bf0b8859a1e57f3abd629c0c736e644e81651d4de034feea49f883f00e82b0"));

    public final Flag<InventoryClickEvent, Material> ITEM_TRADE = Flag.create(
            "trade_item", true, EventHelpers.ITEM_TRADE
    ).setIcon(ItemUtil.getSkinHead("7e5995106d080f10b2052de08e355f34a2213904d9d32f6dc2d1b27bec753b74"));

    public final Flag<BrewEvent, PotionEffectType> POTION_BREW = Flag.create(
            "brew_potion", true, EventHelpers.POTION_BREW
    ).setIcon(ItemUtil.getSkinHead("93a728ad8d31486a7f9aad200edb373ea803d1fc5fd4321b2e2a971348234443"));

    public final Flag<PlayerItemConsumeEvent, PotionEffectType> POTION_DRINK = Flag.create(
            "drink_potion", false, EventHelpers.POTION_DRINK
    ).setIcon(ItemUtil.getSkinHead("5522185926708535dc2dd6024cdde8317ec350e74c3ca3645e9902b2c7870ba5"));

    public final Flag<ProjectileLaunchEvent, EntityType> PROJECTILE_LAUNCH = Flag.create(
            "launch_projectile", true, EventHelpers.PROJECTILE_LAUNCH
    ).setIcon(ItemUtil.getSkinHead("1dfd7724c69a024dcfc60b16e00334ab5738f4a92bafb8fbc76cf15322ea0293"));

    public final Flag<InventoryClickEvent, Enchantment> ENCHANT_REMOVE = Flag.create(
            "remove_enchant", true, EventHelpers.ENCHANT_REMOVE
    ).setIcon(ItemUtil.getSkinHead("8e99bfa61fe552f1e6636b03fbe40f4e470c3b3cb14f70e9012813790ead568f"));

    public final Flag<EnchantItemEvent, Enchantment> ENCHANT_GET = Flag.create(
            "get_enchant", true, EventHelpers.ENCHANT_GET
    ).setIcon(ItemUtil.getSkinHead("b62651879d870499da50e34036800ddffd52f3e4e1993c5fc0fc825d03446d8b"));
}
