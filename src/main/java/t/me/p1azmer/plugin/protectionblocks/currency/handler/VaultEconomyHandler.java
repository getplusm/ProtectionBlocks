package t.me.p1azmer.plugin.protectionblocks.currency.handler;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.integration.external.VaultHook;
import t.me.p1azmer.plugin.protectionblocks.api.currency.CurrencyHandler;
import t.me.p1azmer.plugin.protectionblocks.api.currency.CurrencyOfflineHandler;

import java.util.UUID;

public class VaultEconomyHandler implements CurrencyHandler, CurrencyOfflineHandler {

    @Override
    public double getBalance(@NotNull Player player) {
        return VaultHook.getBalance(player);
    }

    @Override
    public void give(@NotNull Player player, double amount) {
        VaultHook.addMoney(player, amount);
    }

    @Override
    public void take(@NotNull Player player, double amount) {
        VaultHook.takeMoney(player, amount);
    }

    @Override
    public double getBalance(@NotNull UUID playerId) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);

        return VaultHook.getBalance(offlinePlayer);
    }

    @Override
    public void give(@NotNull UUID playerId, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);

        VaultHook.addMoney(offlinePlayer, amount);
    }

    @Override
    public void take(@NotNull UUID playerId, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);

        VaultHook.takeMoney(offlinePlayer, amount);
    }
}