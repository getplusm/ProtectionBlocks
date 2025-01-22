package t.me.p1azmer.plugin.protectionblocks.region.members;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.config.Writeable;

import java.util.Optional;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberRole implements Writeable {
    String id;
    String displayName;
    boolean defaultRole;
    boolean permanent;
    int priority;

    public MemberRole(@NotNull String id, @NotNull String displayName,
                      boolean defaultRole, boolean permanent,
                      int priority) {
        this.id = id;
        this.displayName = displayName;
        this.defaultRole = defaultRole;
        this.permanent = permanent;
        this.priority = priority;
    }

    public static MemberRole read(@NotNull JYML config, @NotNull String path) {
        String id = Optional.ofNullable(config.getString(path + ".Id")).orElseThrow(() -> {
            return new IllegalArgumentException("Member role id is not specified at " + path);
        });
        String displayName = Optional.ofNullable(config.getString(path + ".Display_Name")).orElseThrow(() -> {
            return new IllegalArgumentException("Member role name is not specified at " + path);
        });
        boolean defaultRole = config.getBoolean(path + ".Default_Role", false);
        boolean permanent = config.getBoolean(path + ".Permanent", false);
        int priority = config.getInt(path + ".Priority", 100);
        return new MemberRole(id, displayName, defaultRole, permanent, priority);
    }

    @Override
    public void write(@NotNull JYML config, @NotNull String path) {
        config.set(path + ".Id", id);
        config.set(path + ".Display_Name", displayName);
        config.set(path + ".Default_Role", defaultRole);
        config.set(path + ".Permanent", permanent);
        config.set(path + ".Priority", priority);
    }
}
