package t.me.p1azmer.plugin.protectionblocks.currency.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.coins.api.CoinsEngineAPI;
import t.me.p1azmer.engine.coins.data.impl.CoinsUser;
import t.me.p1azmer.plugin.protectionblocks.api.currency.Currency;
import t.me.p1azmer.plugin.protectionblocks.api.currency.CurrencyHandler;
import t.me.p1azmer.plugin.protectionblocks.api.currency.CurrencyOfflineHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// It's my personal plugin, don't mind me =/
// but, you can find this plugin by @nulli0n
public class CoinsEngineCurrency implements Currency, CurrencyHandler, CurrencyOfflineHandler {

    private final t.me.p1azmer.engine.coins.api.currency.Currency currency;

    public CoinsEngineCurrency(@NotNull t.me.p1azmer.engine.coins.api.currency.Currency currency) {
        this.currency = currency;
    }

    @NotNull
    public static Set<CoinsEngineCurrency> getCurrencies() {
        Set<CoinsEngineCurrency> currencies = new HashSet<>();
        CoinsEngineAPI.getCurrencyManager().getCurrencies().forEach(cura -> {
            if (!cura.isVaultEconomy()) {
                currencies.add(new CoinsEngineCurrency(cura));
            }
        });
        return currencies;
    }

    @Override
    @NotNull
    public String formatValue(double price) {
        return this.currency.formatValue(price);
    }

    @Override
    @NotNull
    public CurrencyHandler getHandler() {
        return this;
    }

    @Override
    @NotNull
    public String getId() {
        return "coinsengine_" + this.currency.getId();
    }

    @Override
    @NotNull
    public String getName() {
        return this.currency.getName();
    }

    @Override
    @NotNull
    public String getFormat() {
        return this.currency.getFormat();
    }

    @Override
    @NotNull
    public ItemStack getIcon() {
        return this.currency.getIcon();
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.currency.getPlaceholders();
    }

    @Override
    public double getBalance(@NotNull Player player) {
        return CoinsEngineAPI.getBalance(player, this.currency);
    }

    @Override
    public double getBalance(@NotNull UUID playerId) {
        CoinsUser user = CoinsEngineAPI.getUserData(playerId);
        if (user == null) return 0D;

        return user.getCurrencyData(this.currency).getBalance();
    }

    @Override
    public void give(@NotNull Player player, double amount) {
        CoinsEngineAPI.addBalance(player, this.currency, amount);
    }

    @Override
    public void give(@NotNull UUID playerId, double amount) {
        CoinsEngineAPI.getUserDataAsync(playerId).thenAccept(user -> {
            if (user == null) return;

            user.getCurrencyData(this.currency).addBalance(amount);
            CoinsEngineAPI.getUserManager().saveUser(user);
        });
    }

    @Override
    public void take(@NotNull Player player, double amount) {
        CoinsEngineAPI.removeBalance(player, this.currency, amount);
    }

    @Override
    public void take(@NotNull UUID playerId, double amount) {
        CoinsEngineAPI.getUserDataAsync(playerId).thenAccept(user -> {
            if (user == null) return;

            user.getCurrencyData(this.currency).removeBalance(amount);
            CoinsEngineAPI.getUserManager().saveUser(user);
        });
    }
}