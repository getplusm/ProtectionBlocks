package t.me.p1azmer.plugin.protectionblocks.currency.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.api.currency.Currency;
import t.me.p1azmer.plugin.protectionblocks.api.currency.CurrencyHandler;

public class ConfigCurrency extends AbstractConfigHolder<ProtectionPlugin> implements Currency {

  private String name;
  private String format;
  private ItemStack icon;

  private final CurrencyHandler handler;
  private final PlaceholderMap placeholderMap;

  public ConfigCurrency(@NotNull ProtectionPlugin plugin, @NotNull JYML cfg, @NotNull CurrencyHandler handler) {
    super(plugin, cfg);
    this.handler = handler;

    this.placeholderMap = new PlaceholderMap()
      .add(Placeholders.CURRENCY_ID, this::getId)
      .add(Placeholders.CURRENCY_NAME, this::getName);
  }

  @Override
  public boolean load() {
    boolean enabled = JOption.create("Enabled", true).read(cfg);
    if (!enabled) return false;

    this.name = Colorizer.apply(JOption.create("Name", StringUtil.capitalizeUnderscored(this.getId())).read(cfg));
    this.format = Colorizer.apply(JOption.create("Format", Placeholders.GENERIC_PRICE + " " + Placeholders.CURRENCY_NAME).read(cfg));
    this.icon = JOption.create("Icon", new ItemStack(Material.GOLD_INGOT)).read(cfg);
    this.cfg.saveChanges();
    return true;
  }

  @Override
  public void onSave() {
    this.cfg.set("Name", this.getName());
    this.cfg.set("Format", this.getFormat());
    this.cfg.setItem("Icon", this.getIcon());
  }

  @Override
  @NotNull
  public PlaceholderMap getPlaceholders() {
    return this.placeholderMap;
  }

  @NotNull
  @Override
  public CurrencyHandler getHandler() {
    return handler;
  }

  @NotNull
  @Override
  public String getName() {
    return name;
  }

  @NotNull
  @Override
  public String getFormat() {
    return format;
  }

  @NotNull
  @Override
  public ItemStack getIcon() {
    if (this.icon == null || this.icon.getType().isAir()) {
      this.icon = new ItemStack(Material.GOLD_INGOT);
    }
    return new ItemStack(this.icon);
  }
}