package t.me.p1azmer.plugin.protectionblocks.region.menu.block;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.menu.MenuItemType;
import t.me.p1azmer.engine.api.menu.click.ClickHandler;
import t.me.p1azmer.engine.api.menu.impl.ConfigMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.api.menu.item.MenuItem;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.LinkedHashMap;

public class RecipePreviewMainMenu extends ConfigMenu<ProtectionPlugin> {

    private final RegionBlock regionBlock;
    public RecipePreviewMainMenu(@NotNull RegionBlock regionBlock) {
        super(regionBlock.plugin(), JYML.loadOrExtract(regionBlock.plugin(), "/menu/recipe_preview.gui.yml"));
        this.regionBlock = regionBlock;
        this.registerHandler(MenuItemType.class)
                .addClick(MenuItemType.RETURN, (viewer, event) -> regionBlock.getManager().getRecipePreviewListMenu().openNextTick(viewer, 1))
                .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
                .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this))
        ;
        this.load();

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                ItemReplacer.replace(item, regionBlock.replacePlaceholders());
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        new LinkedHashMap<>(regionBlock.getRecipe().getSlotItemsMap()).forEach((slot, itemStack) -> {
            this.addItem(new MenuItem(itemStack)
                    .setPriority(101)
                    .setSlots(slot));
        });
        this.addItem(new MenuItem(regionBlock.getItem()).setPriority(101).setSlots(24));
    }
}
