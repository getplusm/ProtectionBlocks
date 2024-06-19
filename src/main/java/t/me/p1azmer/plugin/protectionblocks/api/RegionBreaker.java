package t.me.p1azmer.plugin.protectionblocks.api;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionAPI;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.DamageType;

@Data
public class RegionBreaker implements Placeholder {
    private ItemStack item;
    private DamageType damageType;

    private final PlaceholderMap placeholders;

    public RegionBreaker(@NotNull ItemStack item, @NotNull DamageType damageType) {
        this.item = item;
        this.damageType = damageType;

        this.placeholders = new PlaceholderMap()
          .add(Placeholders.REGION_BLOCK_BREAKER_DMG_TYPE, () -> ProtectionAPI.PLUGIN.getLangManager()
                                                                                     .getEnum(this.getDamageType()));
    }

    @NotNull
    public static DamageType foundDMGType(@NotNull Material material) {
        boolean isTNT = material.equals(Material.TNT) || material.equals(Material.TNT_MINECART);
        if (isTNT) return DamageType.EXPLODE;
        if (material.isItem()) return DamageType.TOOLS;
        return DamageType.HAND;
    }

    @NotNull
    public static RegionBreaker read(@NotNull JYML cfg, @NotNull String path) {
        ItemStack item = cfg.getItemEncoded(path + ".Item");
        if (item == null) item = new ItemStack(Material.BARRIER);

        DamageType damageType = cfg.getEnum(path + ".Damage_Type", DamageType.class, DamageType.HAND);
        return new RegionBreaker(item, damageType);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.setItemEncoded(path + ".Item", this.getItem());
        cfg.set(path + ".Damage_Type", this.getDamageType());
    }

    public boolean tryBreakRegion(@NotNull DamageType damageType, @Nullable Object blockOrItem) {
        Material itemType = this.getItem().getType();
        boolean isTNT = itemType.equals(Material.TNT) || itemType.equals(Material.TNT_MINECART);

        if (damageType.equals(this.getDamageType())) {
            switch (damageType) {
                case HAND -> {
                    return true;
                }
                case TOOLS -> {
                    if (blockOrItem == null) return false;

                    if (blockOrItem instanceof ItemStack breakItem) {
                        Material breakItemType = breakItem.getType();
                        if (breakItemType.isItem()) {
                            return itemType.equals(breakItemType);
                        }
                    }
                    return false;
                }
                case EXPLODE -> {
                    return isTNT || itemType.equals(Material.CREEPER_SPAWN_EGG);
                }
            }
        }
        return false;
    }
}