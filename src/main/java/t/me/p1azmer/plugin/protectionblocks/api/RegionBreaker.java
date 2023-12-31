package t.me.p1azmer.plugin.protectionblocks.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionAPI;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;

public class RegionBreaker implements IPlaceholderMap {
    private ItemStack item;
    private RegionManager.DamageType damageType;

    private final PlaceholderMap placeholderMap;

    public RegionBreaker(@NotNull ItemStack item, @NotNull RegionManager.DamageType damageType) {
        this.item = item;
        this.damageType = damageType;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.REGION_BLOCK_BREAKER_DMG_TYPE, () -> ProtectionAPI.PLUGIN.getLangManager().getEnum(this.getDamageType()))
        ;
    }

    public static RegionManager.DamageType foundDMGType(@NotNull Material material) {
        if (material.equals(Material.TNT) || material.equals(Material.TNT_MINECART))
            return RegionManager.DamageType.EXPLODE;
        if (material.isItem())
            return RegionManager.DamageType.TOOLS;
        return RegionManager.DamageType.HAND;
    }

    public static RegionBreaker read(@NotNull JYML cfg, @NotNull String path) {
        ItemStack item = cfg.getItemEncoded(path + ".Item");
        if (item == null)
            item = new ItemStack(Material.BARRIER);
        RegionManager.DamageType damageType = cfg.getEnum(path + ".Damage_Type", RegionManager.DamageType.class, RegionManager.DamageType.HAND);
        return new RegionBreaker(item, damageType);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.setItemEncoded(path + ".Item", this.getItem());
        cfg.set(path + ".Damage_Type", this.getDamageType());
    }

    public boolean tryBreak(@NotNull RegionManager.DamageType damageType, @Nullable Object blockOrItem) {
        if (damageType.equals(this.getDamageType())) {
            switch (damageType) {
                case HAND -> {
                    return true;
                }
                case TOOLS -> {
                    if (blockOrItem == null) return false;
                    if (blockOrItem instanceof ItemStack breakItem && breakItem.getType().isItem()) {
                        return this.getItem().getType().equals(breakItem.getType());
                    }
                    return false;
                }
                case EXPLODE -> {
                    return this.getItem().getType().equals(Material.TNT) || this.getItem().getType().equals(Material.TNT_MINECART) || this.getItem().getType().equals(Material.CREEPER_SPAWN_EGG);
                }
            }
        }
        return false;
    }

    @NotNull
    public ItemStack getItem() {
        return new ItemStack(item);
    }

    @NotNull
    public RegionManager.DamageType getDamageType() {
        return damageType;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public void setItem(@NotNull ItemStack item) {
        this.item = item;
    }

    public void setDamageType(@NotNull RegionManager.DamageType damageType) {
        this.damageType = damageType;
    }
}
