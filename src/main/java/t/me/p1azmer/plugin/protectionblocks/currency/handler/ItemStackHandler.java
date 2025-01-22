package t.me.p1azmer.plugin.protectionblocks.currency.handler;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.plugin.protectionblocks.api.currency.CurrencyHandler;

public class ItemStackHandler implements CurrencyHandler {

    private ItemStack item;

    public ItemStackHandler(@NotNull ItemStack item) {
        this.setItem(item);
    }

    @NotNull
    public ItemStack getItem() {
        return item;
    }

    public void setItem(@NotNull ItemStack item) {
        this.item = new ItemStack(item);
    }

    @Override
    public double getBalance(@NotNull Player player) {
        return PlayerUtil.countItem(player, this.getItem());
    }

    @Override
    public void give(@NotNull Player player, double amount) {
        PlayerUtil.addItem(player, this.getItem(), (int) amount);
    }

    @Override
    public void take(@NotNull Player player, double amount) {
        PlayerUtil.takeItem(player, this.getItem(), (int) amount);
    }
}