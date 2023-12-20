package t.me.p1azmer.plugin.protectionblocks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.NexPlugin;
import t.me.p1azmer.engine.api.command.GeneralCommand;
import t.me.p1azmer.engine.api.data.UserDataHolder;
import t.me.p1azmer.engine.command.list.ReloadSubCommand;
import t.me.p1azmer.engine.utils.EngineUtils;
import t.me.p1azmer.plugin.protectionblocks.api.integration.HologramHandler;
import t.me.p1azmer.plugin.protectionblocks.commands.EditorCommand;
import t.me.p1azmer.plugin.protectionblocks.commands.GiveCommand;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.currency.CurrencyManager;
import t.me.p1azmer.plugin.protectionblocks.data.DataHandler;
import t.me.p1azmer.plugin.protectionblocks.data.UserManager;
import t.me.p1azmer.plugin.protectionblocks.data.impl.RegionUser;
import t.me.p1azmer.plugin.protectionblocks.editor.EditorLocales;
import t.me.p1azmer.plugin.protectionblocks.integration.holograms.HologramDecentHandler;
import t.me.p1azmer.plugin.protectionblocks.integration.holograms.HologramDisplaysHandler;
import t.me.p1azmer.plugin.protectionblocks.region.RegionManager;

public class ProtectionPlugin extends NexPlugin<ProtectionPlugin> implements UserDataHolder<ProtectionPlugin, RegionUser> {
    private DataHandler userData;
    private UserManager userManager;


    private RegionManager regionManager;
    private HologramHandler hologramHandler;
    private CurrencyManager currencyManager;

    @Override
    protected @NotNull ProtectionPlugin getSelf() {
        return this;
    }

    @Override
    public void enable() {
        this.setupHologramHandler();

        this.currencyManager = new CurrencyManager(this);
        this.currencyManager.setup();

        this.regionManager = new RegionManager(this);
        this.regionManager.setup();
    }

    @Override
    public void disable() {
        if (this.hologramHandler != null) {
            this.hologramHandler.shutdown();
            this.hologramHandler = null;
        }

        if (this.regionManager != null) {
            this.regionManager.shutdown();
            this.regionManager = null;
        }
        if (this.currencyManager != null){
            this.currencyManager.shutdown();
            this.currencyManager = null;
        }
    }

    private void setupHologramHandler() {
        boolean hd = EngineUtils.hasPlugin("HolographicDisplays");
        if (hd) {
            this.hologramHandler = new HologramDisplaysHandler(this);
            this.hologramHandler.setup();
        } else if (EngineUtils.hasPlugin("DecentHolograms")) {
            this.hologramHandler = new HologramDecentHandler(this);
            this.hologramHandler.setup();
        }


        if (this.hologramHandler != null) {
            this.info("Using " + (hd ? "HolographicDisplays" : "DecentHolograms") + " hologram handler.");
        } else {
            this.warn("No hologram handler is available. Do your server met all the requirements?");
        }
    }

    @Override
    public void loadConfig() {
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
        this.getLangManager().loadEditor(EditorLocales.class);
        this.getLangManager().loadEnum(RegionManager.DamageType.class);
        this.getLang().save();
    }

    @Override
    public void registerHooks() {

    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<ProtectionPlugin> generalCommand) {
        generalCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
        generalCommand.addChildren(new EditorCommand(this));
        generalCommand.addChildren(new GiveCommand(this));
    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    @Override
    public boolean setupDataHandlers() {
        this.userData = DataHandler.getInstance(this);
        this.userData.setup();

        this.userManager = new UserManager(this);
        this.userManager.setup();
        return true;
    }

    @Override
    @NotNull
    public DataHandler getData() {
        return this.userData;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @NotNull
    public RegionManager getRegionManager() {
        return regionManager;
    }

    @Nullable
    public HologramHandler getHologramHandler() {
        return hologramHandler;
    }

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }
}
