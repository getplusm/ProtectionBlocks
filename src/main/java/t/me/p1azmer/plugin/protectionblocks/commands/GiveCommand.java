package t.me.p1azmer.plugin.protectionblocks.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.command.AbstractCommand;
import t.me.p1azmer.engine.api.command.CommandResult;
import t.me.p1azmer.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.engine.utils.CollectionsUtil;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.plugin.protectionblocks.Perms;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.config.Lang;
import t.me.p1azmer.plugin.protectionblocks.region.impl.block.RegionBlock;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveCommand extends AbstractCommand<ProtectionPlugin> {
    public GiveCommand(@NotNull ProtectionPlugin plugin) {
        super(plugin, new String[]{"give"}, Perms.COMMAND_GIVE);
        this.setUsage(plugin.getMessage(Lang.COMMAND_GIVE_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_GIVE_DESC));
    }

    @Override
    public @NotNull List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.plugin.getRegionManager().getRegionBlocks().stream().map(AbstractConfigHolder::getId).collect(Collectors.toList());
        }
        if (arg == 2) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 3) {
            return Arrays.asList("1", "10", "100");
        }

        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        RegionBlock regionBlock = this.plugin.getRegionManager().getRegionBlockById(result.getArg(1));
        if (regionBlock == null) {
            this.plugin.getMessage(Lang.ERROR_REGION_BLOCK_INVALID).send(sender);
            return;
        }

        Player player = null;
        if (result.length() >= 3)
            player = plugin.getServer().getPlayer(result.getArg(2));
        else if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        int amount = result.getInt(3, 1);
        if (amount <= 0) {
            this.errorNumber(sender, result.getArg(3));
            return;
        }

        PlayerUtil.addItem(player, regionBlock.getItem(), amount);

        plugin.getMessage(Lang.COMMAND_GIVE_DONE)
              .replace(regionBlock.replacePlaceholders())
              .replace(Placeholders.forPlayer(player))
              .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
              .send(sender);
    }
}