package t.me.p1azmer.plugin.protectionblocks.region.menu.block;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.MenuItemType;
import t.me.p1azmer.engine.api.menu.click.ClickHandler;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.ConfigMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.List;
import java.util.stream.Collectors;

public class RecipePreviewListMenu extends ConfigMenu<ProtectionPlugin> implements AutoPaged<RegionBlock> {

    private final RegionManager manager;
    private final int[] slots;
    private final String previewName;
    private final List<String> previewLore;

    public RecipePreviewListMenu(@NotNull RegionManager manager) {
        super(manager.plugin(), JYML.loadOrExtract(manager.plugin(), "/menu/recipe_list.gui.yml"));
        this.manager = manager;

        this.previewName = cfg.getString("Preview.Name");
        this.previewLore = cfg.getStringList("Preview.Lore.Default");
        this.slots = cfg.getMenuSlots("Preview.Slots");

        this.registerHandler(MenuItemType.class).addClick(MenuItemType.CLOSE, ClickHandler.forClose(this));
        this.load();
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return this.slots;
    }

    @Override
    public @NotNull List<RegionBlock> getObjects(@NotNull Player player) {
        return this.manager.getRegionBlocks()
                .stream()
                .filter(f -> f.getBlockRecipe().isEnabled())
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull RegionBlock regionBlock) {
        ItemStack item = regionBlock.getItem();
        ItemReplacer.create(item)
                .setLore(this.previewLore)
                .setDisplayName(this.previewName)
                .replaceLoreExact("%item_lore%", ItemUtil.getLore(item))
                .replace(regionBlock.replacePlaceholders())
                .replace(Colorizer::apply)
                .writeMeta();
        return item;
    }

    @Override
    public @NotNull ItemClick getObjectClick(@NotNull RegionBlock regionBlock) {
        return (viewer, event) -> {
            regionBlock.getPreviewMenu().openAsync(viewer, 1);
        };
    }
}