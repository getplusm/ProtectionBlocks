package t.me.p1azmer.plugin.protectionblocks.region.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.menu.MenuItemType;
import t.me.p1azmer.engine.api.menu.click.ClickHandler;
import t.me.p1azmer.engine.api.menu.impl.ConfigMenu;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.currency.Currency;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.editor.flags.RegionFlagsListEditor;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;
import t.me.p1azmer.plugin.protectionblocks.region.members.RegionMember;

import java.util.Optional;
import java.util.UUID;

public class RegionMenu extends ConfigMenu<ProtectionPlugin> {
    private final Region region;
    private final RegionFlagsListEditor regionFlagsListEditor;

    public RegionMenu(@NotNull Region region) {
        super(region.plugin(), JYML.loadOrExtract(region.plugin(), "/menu/region.gui.yml"));
        this.region = region;
        this.regionFlagsListEditor = new RegionFlagsListEditor(this.region);

        this.registerHandler(MenuItemType.class).addClick(MenuItemType.CLOSE, ClickHandler.forClose(this));
        RegionBlock regionBlock = region.getRegionBlock();
        this.registerHandler(Special.class)
                .addClick(Special.DEPOSIT, (viewer, event) -> {
                    if (regionBlock.getLifeTime() == null) return;

                    int price = regionBlock.getDepositPrice();
                    String currencyId = regionBlock.getCurrencyId();
                    Currency currency = this.plugin().getCurrencyManager().getCurrency(currencyId);
                    if (currency == null) {
                        this.plugin.error("Unable to deposit to region '" + region.getId() + "' Region block '" + regionBlock.getId() + "', because region currency is not found!");
                        return;
                    }
                    double balance = currency.getHandler().getBalance(viewer.getPlayer());
                    if (balance < price) {
                        plugin.getMessage(Lang.MENU_REGION_DEPOSIT_NO_ENOUGH_MONEY).send(viewer.getPlayer());
                        return;
                    }
                    currency.getHandler().take(viewer.getPlayer(), price);
                    region.addDeposit(regionBlock.getLifeTime().getGreatest(viewer.getPlayer()) * 1000L);
                    plugin.getMessage(Lang.MENU_REGION_DEPOSIT_SUCCESS).send(viewer.getPlayer());

                })
                .addClick(Special.FLAGS, (viewer, event) -> {
                    Player player = viewer.getPlayer();
                    UUID playerUuid = player.getUniqueId();
                    RegionMember regionMember = region.getMemberByPlayer(player);
                    if (region.isOwner(playerUuid) || (regionMember != null && regionMember.getRole().getPriority() <= 0)) {
                        this.regionFlagsListEditor.openAsync(viewer.getPlayer(), 1);
                    } else {
                        plugin.getMessage(Lang.MENU_REGION_NO_ACCESS).send(player);
                    }
                })
                .addClick(Special.MEMBERS, (viewer, event) -> {
                    Player player = viewer.getPlayer();
                    UUID playerUuid = player.getUniqueId();
                    RegionMember regionMember = region.getMemberByPlayer(player);
                    if (region.isOwner(playerUuid) || (regionMember != null && regionMember.getRole().getPriority() <= 0)) {
                        region.getMembersMenu().openAsync(viewer, 1);
                    } else {
                        plugin.getMessage(Lang.MENU_REGION_NO_ACCESS).send(player);
                    }
                })
        ;

        this.load();

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                if (menuItem.getType() instanceof Special special && special.equals(Special.DEPOSIT) && region.getLastDeposit() == -1) {
                    item.setType(Material.AIR);
                    return;
                }
                ItemReplacer.replace(item, region.replacePlaceholders());
                ItemReplacer.replace(item, regionBlock.replacePlaceholders());
                Optional.ofNullable(plugin.getCurrencyManager().getCurrency(regionBlock.getCurrencyId())).ifPresent(currency -> {
                    ItemReplacer.replace(item, s -> s.replace(Placeholders.GENERIC_PRICE, currency.format(regionBlock.getDepositPrice())));
                });

            });
        });
    }

    @Override
    public void update() {
        if (this.region.isExpired()) {
            this.clear();
            this.region.getManager().deleteRegion(this.region, true);
            return;
        }
        super.update();
    }

    enum Special {
        MEMBERS,
        DEPOSIT,
        FLAGS
    }
}