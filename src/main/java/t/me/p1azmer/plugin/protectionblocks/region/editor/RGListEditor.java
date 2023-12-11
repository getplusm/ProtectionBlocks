package t.me.p1azmer.plugin.protectionblocks.region.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.editor.EditorLocales;
import t.me.p1azmer.plugin.protectionblocks.region.RegionBlock;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class RGListEditor extends EditorMenu<ProtectionPlugin, RegionManager> implements AutoPaged<RegionBlock> {

    public RGListEditor(@NotNull RegionManager crateManager) {
        super(crateManager.plugin(), crateManager, "Region Blocks Editor", 45);

        this.addExit(39).setClick((viewer, event) -> {
            this.plugin.runTask(task -> viewer.getPlayer().closeInventory());
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.REGION_BLOCK_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Create, wrapper -> {
                if (!this.object.create(StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()))) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.Editor_Region_Block_Error_Exist).getLocalized());
                    return false;
                }
                return true;
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<RegionBlock> getObjects(@NotNull Player player) {
        return this.object.getRegionBlocks().stream().sorted(Comparator.comparing(RegionBlock::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull RegionBlock regionBlock) {
        ItemStack item = regionBlock.getItem();
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.REGION_BLOCK_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.REGION_BLOCK_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, regionBlock.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull RegionBlock regionBlock) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.deleteRegionBlock(regionBlock);
                this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
                return;
            }
            this.plugin.runTask(task -> regionBlock.getEditor().open(viewer.getPlayer(), 1));
        };
    }
}