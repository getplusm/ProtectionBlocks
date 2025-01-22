package t.me.p1azmer.plugin.protectionblocks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.protectionblocks.Perms;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;

import java.util.List;

public class DepositCommand extends AbstractCommand<ProtectionPlugin> {
    public DepositCommand(@NotNull ProtectionPlugin plugin) {
        super(plugin, new String[]{"deposit", "dep"}, Perms.COMMAND_DEPOSIT);
    }

    @Override
    public @NotNull List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return player.hasPermission(Perms.COMMAND_DEPOSIT_OTHER) ?
                    CollectionsUtil.playerNames()
                    : plugin.getRegionManager()
                    .getRegions()
                    .stream()
                    .filter(f -> f.isAllowed(player))
                    .map(AbstractConfigHolder::getId)
                    .toList();
        }
        if (arg == 2 && player.hasPermission(Perms.COMMAND_DEPOSIT_OTHER)) {
            String playerName = args[1];
            return this.plugin.getRegionManager()
                    .getRegions()
                    .stream()
                    .filter(f -> f.getOwnerName().equalsIgnoreCase(playerName))
                    .map(AbstractConfigHolder::getId)
                    .toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
