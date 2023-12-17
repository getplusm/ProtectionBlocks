package t.me.p1azmer.plugin.protectionblocks.currency.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.currency.CurrencyManager;
import t.me.p1azmer.plugin.protectionblocks.currency.handler.ItemStackHandler;

public class ItemCurrency extends ConfigCurrency {

    public ItemCurrency(@NotNull ProtectionPlugin plugin, @NotNull String id) {
        this(plugin, JYML.loadOrExtract(plugin, CurrencyManager.DIR_CUSTOM + id + ".yml"));
    }

    public ItemCurrency(@NotNull ProtectionPlugin plugin, @NotNull JYML cfg) {
        super(plugin, cfg, new ItemStackHandler(new ItemStack(Material.AIR)));
    }

    @NotNull
    @Override
    public ItemStackHandler getHandler() {
        return (ItemStackHandler) super.getHandler();
    }

    @Override
    public boolean load() {
        if (!super.load()) return false;

        ItemStack item;
        if (cfg.contains("Item.Material")) {
            item = cfg.getItem("Item");
        }
        else {
            item = cfg.getItemEncoded("Item");
        }
        if (item == null) {
            this.plugin.error("Invalid item stack for '" + this.getId() + "' item currency!");
            return false;
        }
        this.getHandler().setItem(item);
        return true;
    }

    @Override
    public void onSave() {
        super.onSave();
        this.cfg.setItemEncoded("Item", this.getHandler().getItem());
    }
}