package t.me.p1azmer.plugin.protectionblocks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.plugin.protectionblocks.Perms;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;

public class EditorCommand extends AbstractCommand<ProtectionPlugin> {

    public EditorCommand(@NotNull ProtectionPlugin plugin) {
        super(plugin, new String[]{"editor"}, Perms.COMMAND_EDITOR);
        this.setDescription(plugin.getMessage(Lang.COMMAND_EDITOR_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        this.plugin.getRegionManager().getEditor().openNextTick((Player) sender, 1);
    }
}