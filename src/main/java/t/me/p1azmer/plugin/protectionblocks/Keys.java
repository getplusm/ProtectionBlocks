package t.me.p1azmer.plugin.protectionblocks;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.BlockRecipe;

public class Keys {
    public static final NamespacedKey REGION_BLOCK = new NamespacedKey(ProtectionAPI.PLUGIN, "pb_region_block");

    public static NamespacedKey REGION_BLOCK_RECIPE(@NotNull BlockRecipe recipe) {
        return new NamespacedKey(ProtectionAPI.PLUGIN, "pbrb_recipe_" + recipe.getRegionBlock().getId());
    }
}