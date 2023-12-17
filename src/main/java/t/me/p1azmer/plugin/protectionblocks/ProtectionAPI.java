package t.me.p1azmer.plugin.protectionblocks;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.protectionblocks.currency.CurrencyManager;

public class ProtectionAPI {
    public static final ProtectionPlugin PLUGIN = ProtectionPlugin.getPlugin(ProtectionPlugin.class);

    @NotNull
    public static CurrencyManager getCurrencyManager() {
        return PLUGIN.getCurrencyManager();
    }
}
