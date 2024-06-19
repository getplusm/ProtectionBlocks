package t.me.p1azmer.plugin.protectionblocks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.plugin.protectionblocks.Perms;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;

public class PreviewCommand extends AbstractCommand<ProtectionPlugin> {
    public PreviewCommand(@NotNull ProtectionPlugin plugin) {
        super(plugin, new String[]{"preview", "prev"}, Perms.COMMAND_PREVIEW_MENU);
        this.setDescription(plugin.getMessage(Lang.COMMAND_PREVIEW_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (!(sender instanceof Player player)) {
            this.errorSender(sender);
            return;
        }
        this.plugin.getRegionManager().getRecipePreviewListMenu().openAsync(player, 1);
    }
}