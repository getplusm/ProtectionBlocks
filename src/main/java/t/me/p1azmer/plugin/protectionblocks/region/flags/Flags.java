package t.me.p1azmer.plugin.protectionblocks.region.flags;

import org.bukkit.entity.Player;
import t.me.p1azmer.engine.utils.Pair;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

import java.util.Set;
import java.util.function.Function;

public class Flags {
    public static final Function<Pair<FlagPlayerAction, Region>, Boolean> USE_COMMANDS =
      pair -> {
          FlagPlayerAction action = pair.getFirst();
          Set<Object> notAllowedData = action.getNotAllowedData();
          Region region = pair.getSecond();
          Player player = action.getPlayer();
          if (action.getAction() instanceof String command) {
              if (!region.isAllowed(player)) {
                  return !notAllowedData.contains(command) && !notAllowedData.contains("*");
              }
          }
          return true;
      };
}