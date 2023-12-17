package t.me.p1azmer.plugin.protectionblocks.region.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.manager.EventListener;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.*;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.currency.Currency;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.editor.EditorLocales;
import t.me.p1azmer.plugin.protectionblocks.region.editor.breakers.RGBlockBreakersListEditor;
import t.me.p1azmer.plugin.protectionblocks.region.impl.RegionBlock;

import java.util.List;

public class RGBlockMainEditor extends EditorMenu<ProtectionPlugin, RegionBlock> implements EventListener {

    private RGBlockBreakersListEditor breakersListEditor;

    public RGBlockMainEditor(@NotNull RegionBlock regionBlock) {
        super(regionBlock.plugin(), regionBlock, "Region Block Settings", 54);

        this.addReturn(49).setClick((viewer, event) -> this.plugin.runTask(task -> this.plugin.getRegionManager().getEditor().open(viewer.getPlayer(), 1)));

        this.addItem(Material.STONE, EditorLocales.REGION_BLOCK_ITEM, 4).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                PlayerUtil.addItem(viewer.getPlayer(), regionBlock.getItem());
                return;
            }

            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                regionBlock.setItem(cursor);
                PlayerUtil.addItem(viewer.getPlayer(), cursor);
                event.getView().setCursor(null);
                this.save(viewer);
            }
        }).getOptions().setDisplayModifier(((viewer, item) -> {
            item.setType(regionBlock.getItem().getType());
            item.setItemMeta(regionBlock.getItem().getItemMeta());
            item.setAmount(regionBlock.getItem().getAmount());

            item.setItemMeta(regionBlock.getItem().getItemMeta());

            List<String> lore = ItemUtil.getLore(regionBlock.getItem());
            lore.addAll(EditorLocales.REGION_BLOCK_ITEM.getLocalizedLore());

            ItemUtil.mapMeta(item, meta -> {
                meta.setDisplayName(Colorizer.apply(Colors2.GRAY + "(&r" + ItemUtil.getItemName(item) + Colors2.GRAY + ") " + EditorLocales.REGION_BLOCK_ITEM.getLocalizedName()));
                meta.setLore(lore);

                //meta.addItemFlags(ItemFlag.values());
            });
        }));

        this.addItem(Material.NAME_TAG, EditorLocales.REGION_BLOCK_NAME, 11).setClick((viewer, event) ->
                this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Name, wrapper -> {
                    regionBlock.setName(wrapper.getText());
                    regionBlock.save();
                    return true;
                }));
        this.addItem(Material.MOSS_BLOCK, EditorLocales.REGION_BLOCK_SIZE, 13).setClick((viewer, event) ->
                this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Value, wrapper -> {
                    regionBlock.setRegionSize(wrapper.asInt(1));
                    regionBlock.save();
                    return true;
                }));
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQzNmMzMjkxZmUwMmQxNDJjNGFmMjhkZjJmNTViYjAzOTdlMTk4NTU0ZTgzNDU5OTBkYmJjZDRjMTQwMzE2YiJ9fX0="), EditorLocales.REGION_BLOCK_STRENGTH, 15).setClick((viewer, event) ->
                this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Value, wrapper -> {
                    regionBlock.setStrength(wrapper.asInt(1));
                    regionBlock.save();
                    return true;
                }));
        this.addItem(Material.IRON_PICKAXE, EditorLocales.REGION_BLOCK_BREAKERS_ICON, 21).setClick((viewer, event) -> this.plugin.runTask(task -> this.getEditorRewards().open(viewer.getPlayer(), 1)));

        this.addItem(Material.ARMOR_STAND, EditorLocales.REGION_HOLOGRAM, 23).setClick((viewer, event) -> {
            if (event.getClick().equals(ClickType.DROP)) {
                regionBlock.setHologramInRegion(!regionBlock.isHologramInRegion());
                regionBlock.getManager().getRegionsWithBlocks(regionBlock).forEach(regionBlock::updateHologram);
                this.save(viewer);
            }
            if (event.isLeftClick()) {
                regionBlock.setHologramEnabled(!regionBlock.isHologramEnabled());
                regionBlock.getManager().getRegionsWithBlocks(regionBlock).forEach(regionBlock::updateHologram);
                this.save(viewer);
            } else if (event.isRightClick()) {
                this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Hologram_Template, wrapper -> {
                    regionBlock.setHologramTemplate(wrapper.getTextRaw());
                    regionBlock.getManager().getRegionsWithBlocks(regionBlock).forEach(regionBlock::updateHologram);
                    regionBlock.save();
                    return true;
                });
                EditorManager.suggestValues(viewer.getPlayer(), Config.REGION_HOLOGRAM_TEMPLATES.get().keySet(), true);
            }
        });
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGY3NjljMzY2OWY1ZjM0YWY4MGViODVlNzJlMTQwYjg4MTdiYzQyNzc1Njc1NGUzZDY5NDFlMGEwNTAzM2Y3ZCJ9fX0="),
                EditorLocales.REGION_BLOCK_LIFE_TIME, 31)
                .setClick((viewer, event) -> {
                    regionBlock.setLifeTimeEnabled(!regionBlock.isLifeTimeEnabled());
                    this.save(viewer);
                });
        this.addItem(ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg4OWNmY2JhY2JlNTk4ZThhMWNkODYxMGI0OWZjYjYyNjQ0ZThjYmE5ZDQ5MTFkMTIxMTM0NTA2ZDhlYTFiNyJ9fX0="),
                        EditorLocales.REGION_BLOCK_DEPOSIT, 32)
                .setClick((viewer, event) -> {
                    if (event.isLeftClick()){
                        this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Value, wrapper -> {
                            regionBlock.setDepositPrice(wrapper.asInt(1));
                            regionBlock.save();
                            return true;
                        });
                    }else if (event.isRightClick()){
                        this.handleInput(viewer, Lang.Editor_Region_Block_Enter_Currency, wrapper -> {
                            Currency currency = this.plugin().getCurrencyManager().getCurrency(wrapper.getTextRaw());
                            if (currency == null){
                                EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.Editor_Region_Block_Error_Currency_NF).getLocalized());
                                return false;
                            }
                            regionBlock.setCurrencyId(currency.getId());
                            regionBlock.save();
                            return true;
                        });
                        EditorManager.suggestValues(viewer.getPlayer(), plugin().getCurrencyManager().getCurrencyIds(), true);
                    }
                });

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> ItemReplacer.replace(item, regionBlock.replacePlaceholders())));
            }
        });

        this.registerListeners();
    }

    @Override
    public void registerListeners() {
        this.plugin.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void clear() {
        super.clear();
        this.unregisterListeners();
        if (this.breakersListEditor != null) {
            this.breakersListEditor.clear();
            this.breakersListEditor = null;
        }
    }

    @NotNull
    public RGBlockBreakersListEditor getEditorRewards() {
        if (this.breakersListEditor == null) {
            this.breakersListEditor = new RGBlockBreakersListEditor(this.object);
        }
        return this.breakersListEditor;
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.plugin.runTaskAsync(async-> this.plugin.getRegionManager().getRegions().forEach(this.object::updateHologram));
        this.openNextTick(viewer.getPlayer(), viewer.getPage());
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}