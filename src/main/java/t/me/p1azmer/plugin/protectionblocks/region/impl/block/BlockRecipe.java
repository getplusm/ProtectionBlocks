package t.me.p1azmer.plugin.protectionblocks.region.impl.block;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.Version;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.protectionblocks.Keys;

import java.util.LinkedHashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlockRecipe {
    RegionBlock regionBlock;
    @NonFinal
    Map<Integer, ItemStack> slotItemsMap;
    @NonFinal
    boolean enabled;

    public static BlockRecipe read(@NotNull JYML cfg, @NotNull String path, @NotNull RegionBlock regionBlock) {
        Map<Integer, ItemStack> slotItems = new LinkedHashMap<>();
        for (String sId : cfg.getSection(path + ".Recipe.Items")) {
            int slot = NumberUtil.getInteger(sId, 0);
            ItemStack item = cfg.getItemEncoded(path + ".Recipe.Items." + sId);
            if (item == null)
                item = new ItemStack(Material.AIR);
            slotItems.put(slot, item);
        }
        boolean enabled = cfg.getBoolean(path + ".Recipe.Enabled");
        return new BlockRecipe(regionBlock, slotItems, enabled);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        this.getSlotItemsMap().forEach((slot, itemStack) -> cfg.setItemEncoded(path + ".Recipe.Items." + slot, itemStack));
        cfg.set(path + ".Recipe.Enabled", this.isEnabled());
    }

    public void setup() {
        if (!this.isEnabled()) return;

        if (this.getSlotItemsMap().values().stream().filter(f -> f.getType().isAir()).count() >= 8) {
            return;
        }

        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(Keys.REGION_BLOCK_RECIPE(this), this.getRegionBlock().getItem());
        this.getSlotItemsMap().entrySet().stream()
                .filter(e -> !e.getValue().getType().isAir())
                .forEach(entry -> shapelessRecipe.addIngredient(entry.getValue()));

        if (Version.isBehind(Version.V1_20_R1)) {
            Bukkit.addRecipe(shapelessRecipe);
        } else {
            Bukkit.addRecipe(shapelessRecipe, true);
        }
    }

    public void reload() {
        this.shutdown();
        this.setup();
    }

    public void shutdown() {
        if (Version.isBehind(Version.V1_18_R2)) {
            Bukkit.removeRecipe(Keys.REGION_BLOCK_RECIPE(this));
        } else {
            Bukkit.removeRecipe(Keys.REGION_BLOCK_RECIPE(this), true);
        }
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.reload();
    }

    @NotNull
    public ItemStack getItemBySlot(int slot) {
        return new ItemStack(this.slotItemsMap.getOrDefault(slot, new ItemStack(Material.AIR)));
    }

    public void setItem(int slot, @NotNull ItemStack item) {
        this.slotItemsMap.put(slot, item);
    }
}
