package t.me.p1azmer.plugin.protectionblocks.region.menu;

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
import t.me.p1azmer.engine.editor.EditorManager;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.ItemUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.RegionMember;

import java.util.ArrayList;
import java.util.List;

public class RegionMembersMenu extends ConfigMenu<ProtectionPlugin> implements AutoPaged<RegionMember> {
    private final int[] slots;
    private final String previewName;
    private final List<String> previewLore;
    private final Region region;

    public RegionMembersMenu(@NotNull Region region) {
        super(region.plugin(), JYML.loadOrExtract(region.plugin(), "/menu/region.members.gui.yml"));
        this.region = region;
        this.slots = cfg.getMenuSlots("Slots");
        this.previewName = cfg.getString("Preview.Name");
        this.previewLore = cfg.getStringList("Preview.Lore");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, ClickHandler.forClose(this))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this))
            .addClick(MenuItemType.RETURN, (viewer, event) -> region.getRegionMenu().openAsync(viewer, 1));

        this.registerHandler(Special.class)
            .addClick(Special.ADD_MEMBER, (viewer, event) -> {
                EditorManager.prompt(viewer.getPlayer(), plugin().getMessage(Lang.Editor_Region_Enter_Player_name).getLocalized());
                EditorManager.startEdit(viewer.getPlayer(), wrapper -> {
                    String playerName = wrapper.getTextRaw();
                    Player player = PlayerUtil.getPlayer(playerName);
                    if (player == null) {
                        EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.Editor_Region_Members_Player_NF).getLocalized());
                        return false;
                    }
                    if (region.isAllowed(player)) {
                        EditorManager.error(viewer.getPlayer(), plugin().getMessage(Lang.Editor_Region_Members_Player_Already).getLocalized());
                        return false;
                    }
                    region.addMember(player);
                    region.save();
                    this.openAsync(viewer, 1);
                    return true;
                });
                plugin.runTask(task -> viewer.getPlayer().closeInventory());
            });

        this.load();
    }

    @Override
    public int[] getObjectSlots() {
        return slots;
    }

    @Override
    public @NotNull List<RegionMember> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.region.getMembers());
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull RegionMember member) {
        ItemStack item = ItemUtil.getSkinHead(member.getUuid().toString());
        ItemReplacer.create(item)
                    .setDisplayName(this.previewName)
                    .setLore(this.previewLore)
                    .replace(member.replacePlaceholders())
                    .replace(this.region.replacePlaceholders())
                    .replace(Colorizer::apply)
                    .writeMeta();
        return item;
    }

    @Override
    public @NotNull ItemClick getObjectClick(@NotNull RegionMember member) {
        return (viewer, event) -> {
            this.region.removeMember(member);
            this.region.save();

            plugin.getMessage(Lang.MENU_MEMBERS_KICK_SUCCESS)
                  .replace(Placeholders.MEMBER_NAME, member.getName())
                  .send(viewer.getPlayer());

            this.openAsync(viewer, 1);
        };
    }

    enum Special {
        ADD_MEMBER
    }
}