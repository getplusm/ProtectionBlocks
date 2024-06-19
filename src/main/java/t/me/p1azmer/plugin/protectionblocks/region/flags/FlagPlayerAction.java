package t.me.p1azmer.plugin.protectionblocks.region.flags;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.Set;

@Data
public class FlagPlayerAction {
    private final Set<Object> notAllowedData;
    private final Player player;
    private final Object action;
}