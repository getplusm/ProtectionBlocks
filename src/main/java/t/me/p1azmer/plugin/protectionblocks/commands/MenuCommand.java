package t.me.p1azmer.plugin.protectionblocks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.plugin.protectionblocks.Perms;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.data.impl.RegionUser;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

import java.util.ArrayList;
import java.util.List;

public class MenuCommand extends AbstractCommand<ProtectionPlugin> {
    public MenuCommand(@NotNull ProtectionPlugin plugin) {
        super(plugin, new String[]{"menu", "gui"}, Perms.COMMAND_MENU);
        this.setUsage(plugin.getMessage(Lang.COMMAND_MENU_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_MENU_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    public @NotNull List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        RegionUser user = plugin.getUserManager().getUserData(player);
        if (arg == 1) {
            return new ArrayList<>(user.getRegions().keySet());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (!(sender instanceof Player player)) {
            this.errorSender(sender);
            return;
        }
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Region region = this.plugin.getRegionManager().getRegionById(result.getArg(1));
        if (region == null) {
            this.plugin.getMessage(Lang.ERROR_REGION_NOT_FOUND).send(sender);
            return;
        }

        if (!region.isAllowed(player)) {
            return;
        }
        region.getRegionMenu().open(player, 1);
        plugin.getMessage(Lang.COMMAND_MENU_DONE)
              .replace(Placeholders.forLocation(region.getBlockLocation()))
              .replace(Placeholders.forPlayer(player))
              .send(sender);
    }
}