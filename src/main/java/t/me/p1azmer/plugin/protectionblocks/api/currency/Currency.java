package t.me.p1azmer.plugin.protectionblocks.api.currency;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;

public interface Currency extends Placeholder {

    @NotNull
    default String formatValue(double price) {
        return NumberUtil.format(price);
    }

    @NotNull
    default String format(double price) {
        return this.replacePlaceholders().apply(this.getFormat())
                   .replace(Placeholders.GENERIC_AMOUNT, this.formatValue(price))
                   .replace(Placeholders.GENERIC_PRICE, this.formatValue(price));
    }

    @Nullable
    default CurrencyOfflineHandler getOfflineHandler() {
        if (this instanceof CurrencyOfflineHandler handler) return handler;
        if (this.getHandler() instanceof CurrencyOfflineHandler handler) return handler;

        return null;
    }

    @NotNull CurrencyHandler getHandler();

    @NotNull String getId();

    @NotNull String getName();

    @NotNull String getFormat();

    @NotNull ItemStack getIcon();
}