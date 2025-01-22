package t.me.p1azmer.plugin.protectionblocks.region.flags;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlagSettings {
    boolean enabled;
    // add role settings
}
