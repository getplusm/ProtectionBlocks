package t.me.p1azmer.plugin.protectionblocks.region.editor.recipe;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.impl.Menu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.api.menu.item.MenuItem;
import t.me.p1azmer.engine.utils.*;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.IntStream;

import static t.me.p1azmer.plugin.protectionblocks.editor.EditorLocales.*;

public class RGBlockRecipeEditor extends Menu<ProtectionPlugin> {

    private final RegionBlock regionBlock;
    private final int[] craftingSlots;

    public RGBlockRecipeEditor(@NotNull RegionBlock regionBlock) {
        super(regionBlock.plugin(), "Region Block Recipe", 54);
        this.regionBlock = regionBlock;
        this.craftingSlots = new int[]{
                10, 11, 12,
                19, 20, 21,
                28, 29, 30
        };
        int[] emptySlots = IntStream.range(0, 54).toArray();
        this.addItem(new MenuItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                .setSlots(emptySlots)
        ).getOptions().setDisplayModifier((viewer, itemStack) -> ItemReplacer.create(itemStack).setDisplayName(" ").writeMeta());

        ItemStack blockItem = regionBlock.getItem();
        ItemReplacer.create(blockItem).readLocale(REGION_BLOCK_RECIPE_BLOCK_ITEM).hideFlags().writeMeta();
        this.addItem(new MenuItem(blockItem).setPriority(100).setSlots(24));

        ItemStack returnItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM4NTJiZjYxNmYzMWVkNjdjMzdkZTRiMGJhYTJjNWY4ZDhmY2E4MmU3MmRiY2FmY2JhNjY5NTZhODFjNCJ9fX0=");
        ItemReplacer.create(returnItem).readLocale(RETURN).hideFlags().writeMeta();
        this.addItem(returnItem).setSlots(49).setPriority(100).setClick((viewer, inventoryClickEvent) -> this.plugin.runTask(task -> regionBlock.getEditor().open(viewer.getPlayer(), 1)));

        ItemStack clearItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2UxNzkzMmUwMTQyMjUzMTY1MzczOWQzYmVjZmJiNWNmNzhmNWY3NmEzZDdiNzY5ZTJmNjYxZjUyYzJhZjJkYSJ9fX0=");
        ItemReplacer.create(clearItem).readLocale(REGION_BLOCK_RECIPE_CLEAR).hideFlags().writeMeta();
        this.addItem(clearItem).setSlots(47).setPriority(100).setClick((viewer, inventoryClickEvent) -> {
            regionBlock.getRecipe().setSlotItemsMap(new LinkedHashMap<>());
            this.save(viewer);
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.regionBlock.save();
        this.plugin.runTaskLater(task -> this.open(viewer.getPlayer(), viewer.getPage()), 20);
    }


    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        Arrays.stream(this.craftingSlots).forEach(slot->this.addItem(new ItemStack(Material.AIR, slot)));

        new LinkedHashMap<>(regionBlock.getRecipe().getSlotItemsMap()).forEach((slot, itemStack) -> {
            MenuItem menuItem = new MenuItem(itemStack)
                    .setPriority(101)
                    .setSlots(slot)
                    .setClick((menuViewer, inventoryClickEvent) -> {
                        if (inventoryClickEvent.getClick().equals(ClickType.RIGHT)) {
                            PlayerUtil.addItem(menuViewer.getPlayer(), itemStack);
                        }
                    });
            this.addItem(menuItem).getOptions().setDisplayModifier((menuViewer, itemStack1) -> {
                if (itemStack1.getType().isAir()) {
                    itemStack1.setType(Material.GRAY_STAINED_GLASS_PANE);
                    ItemReplacer.create(itemStack1)
                            .readLocale(REGION_BLOCK_RECIPE_ITEM)
                            .setDisplayName(Colors2.WHITE + "AIR").replace(Colorizer::apply).writeMeta();
                    menuItem.setPriority(100);
                } else
                    ItemReplacer.create(itemStack1)
                            .readLocale(REGION_BLOCK_RECIPE_ITEM).writeMeta();
            });
        });
    }
    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack itemOnSlot, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, itemOnSlot, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
            return;
        }
        Player player = viewer.getPlayer();
        ItemStack itemOnCursor = player.getItemOnCursor();
        if (itemOnSlot == null || itemOnSlot.getType().isAir()) {
            return;
        }

        if (slotType == SlotType.MENU) {
            if (Arrays.stream(this.craftingSlots).anyMatch(s -> s == slot)) {
                if (event.isLeftClick()) {
                    this.regionBlock.getRecipe().setItem(slot, new ItemStack(itemOnCursor));
                    player.setItemOnCursor(null);
                    this.save(viewer);
                }
            }
        }
    }

    @Override
    public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
        this.regionBlock.getRecipe().reload();
        super.onClose(viewer, event);
    }
}