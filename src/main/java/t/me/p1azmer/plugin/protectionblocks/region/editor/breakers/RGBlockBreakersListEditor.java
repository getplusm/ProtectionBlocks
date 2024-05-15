package t.me.p1azmer.plugin.protectionblocks.region.editor.breakers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.menu.AutoPaged;
import t.me.p1azmer.engine.api.menu.click.ItemClick;
import t.me.p1azmer.engine.api.menu.impl.EditorMenu;
import t.me.p1azmer.engine.api.menu.impl.MenuOptions;
import t.me.p1azmer.engine.api.menu.impl.MenuViewer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.engine.utils.collections.Lists;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.RegionBreaker;
import t.me.p1azmer.plugin.protectionblocks.editor.EditorLocales;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RGBlockBreakersListEditor extends EditorMenu<ProtectionPlugin, RegionBlock> implements AutoPaged<RegionBreaker> {

  public RGBlockBreakersListEditor(@NotNull RegionBlock regionBlock) {
    super(regionBlock.plugin(), regionBlock, "Region Block Breakers", 45);

    this.addReturn(40).setClick((viewer, event) -> {
      this.plugin.runTask(task -> regionBlock.getEditor().open(viewer.getPlayer(), 1));
    });
    this.addNextPage(44);
    this.addPreviousPage(36);

    this.addCreation(EditorLocales.REGION_BLOCK_BREAKERS_CREATE, 42).setClick((viewer, event) -> {
      ItemStack cursor = event.getCursor();
      if (!cursor.getType().isAir()) {
        List<RegionBreaker> breakers = new ArrayList<>(regionBlock.getBreakers());
        breakers.add(new RegionBreaker(cursor, RegionBreaker.foundDMGType(cursor.getType())));
        regionBlock.setBreakers(breakers);
        PlayerUtil.addItem(viewer.getPlayer(), cursor);
        event.getView().setCursor(null);
        this.save(viewer);
      }
    });

    this.addItem(Material.HOPPER, EditorLocales.REGION_BLOCK_BREAKERS_CLEAR, 38).setClick((viewer, event) -> {
      regionBlock.getBreakers().clear();
      this.save(viewer);
    });

    this.getItems().forEach(menuItem -> {
      if (menuItem.getOptions().getDisplayModifier() == null) {
        menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
          ItemReplacer.replace(item, regionBlock.replacePlaceholders());
        }));
      }
    });
  }

  private void save(@NotNull MenuViewer viewer) {
    this.object.save();
    this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
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
  public List<RegionBreaker> getObjects(@NotNull Player player) {
    return new ArrayList<>(this.object.getBreakers());
  }

  @Override
  @NotNull
  public ItemStack getObjectStack(@NotNull Player player, @NotNull RegionBreaker breaker) {
    ItemStack item = breaker.getItem();
    ItemUtil.editMeta(item, meta -> {
      meta.setDisplayName(EditorLocales.REGION_BLOCK_BREAKERS_OBJECT.getLocalizedName());
      meta.setLore(EditorLocales.REGION_BLOCK_BREAKERS_OBJECT.getLocalizedLore());
      meta.addItemFlags(ItemFlag.values());
      ItemReplacer.replace(meta, this.object.replacePlaceholders());
      ItemReplacer.replace(meta, breaker.replacePlaceholders());
    });
    return item;
  }

  @Override
  @NotNull
  public ItemClick getObjectClick(@NotNull RegionBreaker breaker) {
    return (viewer, event) -> {
      if (event.getClick() == ClickType.DROP) {
        this.object.getBreakers().remove(breaker);
        this.save(viewer);
        return;
      }
      if (event.isLeftClick()) {
        breaker.setDamageType(Lists.next(breaker.getDamageType()));
        this.save(viewer);
        return;
      }
      if (event.isShiftClick()) {
        // Reward position move.
        List<RegionBreaker> all = new ArrayList<>(this.object.getBreakers());
        int index = all.indexOf(breaker);
        int allSize = all.size();

        if (event.isLeftClick()) {
          if (index + 1 >= allSize) return;

          all.remove(index);
          all.add(index + 1, breaker);
        } else if (event.isRightClick()) {
          if (index == 0) return;

          all.remove(index);
          all.add(index - 1, breaker);
        }
        this.object.setBreakers(all);
        this.save(viewer);
      }
    };
  }

  @Override
  public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
    super.onClick(viewer, item, slotType, slot, event);
    if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
      event.setCancelled(false);
    }
  }
}