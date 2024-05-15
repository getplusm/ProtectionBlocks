package t.me.p1azmer.plugin.protectionblocks.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractManager;
import t.me.p1azmer.engine.integration.external.VaultHook;
import t.me.p1azmer.engine.utils.EngineUtils;
import t.me.p1azmer.engine.utils.FileUtil;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.currency.Currency;
import t.me.p1azmer.plugin.protectionblocks.api.currency.CurrencyHandler;
import t.me.p1azmer.plugin.protectionblocks.currency.handler.ExpPointsHandler;
import t.me.p1azmer.plugin.protectionblocks.currency.handler.VaultEconomyHandler;
import t.me.p1azmer.plugin.protectionblocks.currency.impl.ConfigCurrency;
import t.me.p1azmer.plugin.protectionblocks.currency.impl.DummyCurrency;
import t.me.p1azmer.plugin.protectionblocks.currency.impl.ItemCurrency;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CurrencyManager extends AbstractManager<ProtectionPlugin> {

  public static final String DIR_DEFAULT = "/currency/default/";
  public static final String DIR_CUSTOM = "/currency/custom_item/";

  public static final String EXP = "exp";
  public static final String VAULT = "vault";

  public static final DummyCurrency DUMMY = new DummyCurrency();

  private final Map<String, Currency> currencyMap;

  public CurrencyManager(@NotNull ProtectionPlugin plugin) {
    super(plugin);
    this.currencyMap = new HashMap<>();
  }

  @Override
  protected void onLoad() {
    this.plugin.getConfigManager().extractResources(DIR_DEFAULT);
    this.plugin.getConfigManager().extractResources(DIR_CUSTOM);

    this.registerCurrency(EXP, ExpPointsHandler::new);

    if (EngineUtils.hasVault() && VaultHook.hasEconomy()) {
      this.registerCurrency(VAULT, VaultEconomyHandler::new);
    }

    for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + DIR_CUSTOM, true)) {
      ItemCurrency currency = new ItemCurrency(plugin, cfg);
      if (currency.load()) {
        this.registerCurrency(currency);
      }
    }
  }

  @Override
  protected void onShutdown() {
    this.currencyMap.clear();
  }

  public boolean registerCurrency(@NotNull String id, @NotNull Supplier<CurrencyHandler> supplier) {
    JYML cfg = JYML.loadOrExtract(plugin, DIR_DEFAULT, id.toLowerCase() + ".yml");
    ConfigCurrency currency = new ConfigCurrency(plugin, cfg, supplier.get());
    if (!currency.load()) return false;

    return this.registerCurrency(currency);
  }

  public boolean registerCurrency(@NotNull Currency currency) {
    this.getCurrencyMap().put(currency.getId(), currency);
    this.plugin.info("Registered currency: " + currency.getId());
    return true;
  }

  public boolean hasCurrency() {
    return !this.getCurrencyMap().isEmpty();
  }

  @NotNull
  public Map<String, Currency> getCurrencyMap() {
    return currencyMap;
  }

  @NotNull
  public Collection<Currency> getCurrencies() {
    return this.getCurrencyMap().values();
  }

  @NotNull
  public Set<String> getCurrencyIds() {
    return this.getCurrencyMap().keySet();
  }

  @Nullable
  public Currency getCurrency(@NotNull String id) {
    return this.getCurrencyMap().get(id.toLowerCase());
  }

  @NotNull
  public Currency getAny() {
    return this.getCurrencies().stream().findFirst().orElseThrow();
  }
}