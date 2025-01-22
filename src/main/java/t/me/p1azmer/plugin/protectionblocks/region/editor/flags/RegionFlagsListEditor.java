package t.me.p1azmer.plugin.protectionblocks.region.editor.flags;

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
import t.me.p1azmer.engine.lang.LangManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.editor.EditorLocales;
import t.me.p1azmer.plugin.protectionblocks.region.flags.self.RegionFlagSettings;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.members.MemberRole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegionFlagsListEditor extends EditorMenu<ProtectionPlugin, Region> implements AutoPaged<RegionFlagSettings> {

    public RegionFlagsListEditor(@NotNull Region region) {
        super(region.plugin(), region, Config.FLAG_EDITOR_TITLE.get(), 45);

        this.addReturn(40).setClick((viewer, event) -> region.getRegionMenu().openAsync(viewer.getPlayer(), 1));
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> {
                    ItemReplacer.replace(item, region.replacePlaceholders());
                }));
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openAsync(viewer.getPlayer(), viewer.getPage());
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
    public List<RegionFlagSettings> getObjects(@NotNull Player player) {
        return new ArrayList<>(object.getFlags());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull RegionFlagSettings flagSettings) {
        ItemStack item = flagSettings.getFlag().getIcon();
        ItemUtil.editMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.REGION_FLAG_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.REGION_FLAG_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemReplacer.replace(meta, flagSettings.getFlag().replacePlaceholders());
            ItemReplacer.replace(meta, s -> {
                return s.replace(Placeholders.FLAG_ENABLED, LangManager.getBoolean(flagSettings.isEnabled()))
                        .replace(Placeholders.FLAG_TRIGGER_BY_NON_MEMBERS, LangManager.getBoolean(flagSettings.isTriggerByNonMembers()))
                        .replace(Placeholders.FLAG_TRIGGER_ROLES, flagSettings.getTriggerRoles().stream()
                                .map(MemberRole::getDisplayName)
                                .map(Colorizer::apply)
                                .collect(Collectors.joining("\n")));
            });
            ItemReplacer.replace(meta, this.object.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull RegionFlagSettings flagSettings) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.isLeftClick()) {
                if (event.isShiftClick()){
                    EditorManager.suggestValues(player, Config.MEMBER_ROLES.get().stream()
                            .map(MemberRole::getId).collect(Collectors.toSet()), true);
                    EditorManager.startEdit(player, inputWrapper -> {
                        String text = inputWrapper.getText();
                        Set<MemberRole> roles = new HashSet<>(flagSettings.getTriggerRoles());
                        Config.MEMBER_ROLES.get().stream()
                                .filter(f -> f.getId().equals(text))
                                .findFirst().ifPresent(roles::add);
                        flagSettings.setTriggerRoles(roles);
                        save(viewer);
                        return true;
                    });
                    plugin.runTask(task -> viewer.getPlayer().closeInventory());
                    return;
                }

                flagSettings.setEnabled(!flagSettings.isEnabled());
                save(viewer);
                return;
            }
            if (event.isRightClick()) {
                if (event.isShiftClick()) {
                    flagSettings.setTriggerRoles(new HashSet<>());
                    save(viewer);
                    return;
                }

                flagSettings.setTriggerByNonMembers(!flagSettings.isTriggerByNonMembers());
                save(viewer);
            }
        };
    }
}