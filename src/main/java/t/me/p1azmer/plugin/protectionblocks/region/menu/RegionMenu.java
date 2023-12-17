package t.me.p1azmer.plugin.protectionblocks.region.menu;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.menu.MenuItemType;
import t.me.p1azmer.engine.api.menu.click.ClickHandler;
import t.me.p1azmer.engine.api.menu.impl.ConfigMenu;
import t.me.p1azmer.engine.utils.ItemReplacer;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.currency.Currency;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

import java.util.Optional;

public class RegionMenu extends ConfigMenu<ProtectionPlugin> {
    private final Region region;

    public RegionMenu(@NotNull Region region) {
        super(region.plugin(), JYML.loadOrExtract(region.plugin(), "/menu/region.gui.yml"));
        this.region =region;

        this.registerHandler(MenuItemType.class)
                .addClick(MenuItemType.CLOSE, ClickHandler.forClose(this))
        ;
        this.registerHandler(Special.class)
                .addClick(Special.DEPOSIT, (viewer, event) -> {
                    region.getRegionBlock().ifPresent(regionBlock -> {
                        if (regionBlock.getLifeTime() == null) return;

                        int price = regionBlock.getDepositPrice();
                        String currencyId = regionBlock.getCurrencyId();
                        Currency currency = this.plugin().getCurrencyManager().getCurrency(currencyId);
                        if (currency == null){
                            this.plugin.error("Unable to deposit to region '"+region.getId()+"' Region block '"+regionBlock.getId()+"', because region currency is not found!");
                            return;
                        }
                        double balance = currency.getHandler().getBalance(viewer.getPlayer());
                        if (balance < price){
                            plugin.getMessage(Lang.MENU_REGION_DEPOSIT_NO_ENOUGH_MONEY).send(viewer.getPlayer());
                            return;
                        }
                        currency.getHandler().take(viewer.getPlayer(), price);
                        region.addDeposit(regionBlock.getLifeTime().getBestValue(viewer.getPlayer(), 1000) * 1000L);
                        plugin.getMessage(Lang.MENU_REGION_DEPOSIT_SUCCESS).send(viewer.getPlayer());
                    });
                })
                .addClick(Special.MEMBERS, (viewer, event) -> {
                    if (region.isOwner(viewer.getPlayer().getUniqueId())) {
                        region.getMembersMenu().openNextTick(viewer, 1);
                    }else{
                        plugin.getMessage(Lang.MENU_REGION_NO_ACCESS).send(viewer.getPlayer());
                    }
                })
        ;

        this.load();

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                if (menuItem.getType() instanceof Special special && special.equals(Special.DEPOSIT) && region.getLastDeposit() == -1){
                    item.setType(Material.AIR);
                    return;
                }
                ItemReplacer.replace(item, region.replacePlaceholders());
                region.getRegionBlock().ifPresent(regionBlock -> {
                    ItemReplacer.replace(item, regionBlock.replacePlaceholders());
                    Optional.ofNullable(plugin.getCurrencyManager().getCurrency(regionBlock.getCurrencyId())).ifPresent(currency -> {
                        ItemReplacer.replace(item, s->s.replace(Placeholders.GENERIC_PRICE,currency.format(regionBlock.getDepositPrice())));
                    });
                });

            });
        });
    }

    @Override
    public void update() {
        if (this.region.isExpired()){
            this.clear();
            this.region.getManager().deleteRegion(this.region, true);
            return;
        }
        super.update();
    }

    enum Special {
        MEMBERS,
        DEPOSIT
    }
}
