package t.me.p1azmer.plugin.protectionblocks.region.editor.flags;
//
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.ClickType;
//import org.bukkit.inventory.ItemFlag;
//import org.bukkit.inventory.ItemStack;
//import org.jetbrains.annotations.NotNull;
//import t.me.p1azmer.engine.api.menu.AutoPaged;
//import t.me.p1azmer.engine.api.menu.click.ItemClick;
//import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
//import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
//import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
//import t.me.p1azmer.engine.editor.EditorManager;
//import t.me.p1azmer.engine.utils.ItemReplacer;
//import t.me.p1azmer.engine.utils.ItemUtil;
//import t.me.p1azmer.engine.utils.collections.Lists;
//import t.me.p1azmer.plugin.protectionblocks.Placeholders;
//import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
//import t.me.p1azmer.plugin.protectionblocks.config.Config;
//import t.me.p1azmer.plugin.protectionblocks.config.Lang;
//import t.me.p1azmer.plugin.protectionblocks.editor.EditorLocales;
//import t.me.p1azmer.plugin.protectionblocks.region.flags.RegionFlag;
//import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
//
//import java.util.*;
//import java.util.stream.IntStream;
//
//public class RegionFlagsListEditor extends EditorMenu<ProtectionPlugin, Region> implements AutoPaged<RegionFlag> {
//
//    public RegionFlagsListEditor(@NotNull Region region) {
//        super(region.plugin(), region, Config.FLAG_EDITOR_TITLE.get(), 45);
//
//        this.addReturn(40).setClick((viewer, event) -> region.getRegionMenu().openAsync(viewer.getPlayer(), 1));
//        this.addNextPage(44);
//        this.addPreviousPage(36);
//
//        this.addCreation(EditorLocales.REGION_FLAG_ADD, 42)
//            .setClick((viewer, event) -> {
//                EditorManager.suggestValues(viewer.getPlayer(), Lists.worldNames(), true);
//                this.handleInput(viewer, Lang.Editor_Region_Block_Enter_World, wrapper -> {
//                    //
//                    region.save();
//                    return true;
//                });
//            });
//
//        this.addItem(Material.HOPPER, EditorLocales.REGION_FLAGS_CLEAR, 38)
//            .setClick((viewer, event) -> {
//                //
//                this.save(viewer);
//            });
//
//        this.getItems().forEach(menuItem -> {
//            if (menuItem.getOptions().getDisplayModifier() == null) {
//                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
//                    ItemReplacer.replace(item, region.replacePlaceholders());
//                }));
//            }
//        });
//    }
//
//    private void save(@NotNull MenuViewer viewer) {
//        this.object.save();
//        this.openAsync(viewer.getPlayer(), viewer.getPage());
//    }
//
//    @Override
//    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
//        super.onPrepare(viewer, options);
//        this.getItemsForPage(viewer).forEach(this::addItem);
//    }
//
//    @Override
//    public int[] getObjectSlots() {
//        return IntStream.range(0, 36).toArray();
//    }
//
//    @Override
//    @NotNull
//    public List<RegionFlag> getObjects(@NotNull Player player) {
//        return new ArrayList<>(this.object.getFlags().values());
//    }
//
//    @Override
//    @NotNull
//    public ItemStack getObjectStack(@NotNull Player player, @NotNull RegionFlag flag) {
//        ItemStack item = new ItemStack(Material.PAPER);// test for one flag
//        ItemUtil.editMeta(item, meta -> {
//            meta.setDisplayName(EditorLocales.REGION_BLOCK_BREAKERS_OBJECT.getLocalizedName());
//            meta.setLore(EditorLocales.REGION_BLOCK_BREAKERS_OBJECT.getLocalizedLore());
//            meta.addItemFlags(ItemFlag.values());
//            ItemReplacer.replace(meta, this.object.replacePlaceholders());
//            ItemReplacer.replace(meta, flag.replacePlaceholders());
//        });
//        return item;
//    }
//
//    @Override
//    @NotNull
//    public ItemClick getObjectClick(@NotNull RegionFlag flag) {
//        return (viewer, event) -> {
//            if (event.getClick() == ClickType.DROP) {
//                this.object.getFlags().remove(flagName);
//                this.save(viewer);
//                return;
//            }
//            if (event.isLeftClick()) {
//                EditorManager.prompt(viewer.getPlayer(), "enter text");
//                EditorManager.startEdit(viewer.getPlayer(), wrapper -> {
//                    Set<Object> objects = this.object.getFlags().get(flagName);
//                    objects.add(wrapper.getTextRaw());
//                    this.object.getFlags().put(flagName, objects);
//                    this.save(viewer);
//                    return true;
//                });
//            }
//        };
//    }
//}