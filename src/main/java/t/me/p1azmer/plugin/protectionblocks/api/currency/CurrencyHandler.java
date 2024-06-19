package t.me.p1azmer.plugin.protectionblocks.api.currency;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface CurrencyHandler {
    double getBalance(@NotNull Player player);

    void give(@NotNull Player player, double amount);

    void take(@NotNull Player player, double amount);
}