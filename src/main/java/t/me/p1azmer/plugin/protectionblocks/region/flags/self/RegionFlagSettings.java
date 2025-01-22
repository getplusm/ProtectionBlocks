package t.me.p1azmer.plugin.protectionblocks.region.flags.self;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.config.Writeable;
import t.me.p1azmer.plugin.protectionblocks.config.Config;
import t.me.p1azmer.plugin.protectionblocks.region.flags.Flag;
import t.me.p1azmer.plugin.protectionblocks.region.flags.FlagsController;
import t.me.p1azmer.plugin.protectionblocks.region.members.MemberRole;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegionFlagSettings implements Writeable {
    Flag<?, ?> flag;
    @NonFinal
    Set<MemberRole> triggerRoles;
    @NonFinal
    boolean enabled;
    @NonFinal
    boolean triggerByNonMembers;


    public static @NotNull RegionFlagSettings read(@NotNull FlagsController flagsController, @NotNull JYML config, @NotNull String path) {
        Flag<?, ?> flag = Optional.ofNullable(config.getString(path + ".Flag")).map(flagRaw -> {
            return Optional.ofNullable(flagsController.getActionType(flagRaw)).orElseThrow(() -> {
                return new IllegalArgumentException("Flag not found " + flagRaw + " in config");
            });
        }).orElseThrow(() -> {
            return new IllegalArgumentException("Flag not found at " + path);
        });
        Set<MemberRole> memberRoles = Optional.of(config.getStringSet(path + ".Trigger_Roles")).map(roleSet -> {
            return Config.MEMBER_ROLES.get().stream().filter(role -> roleSet.contains(role.getId())).collect(Collectors.toSet());
        }).orElseThrow(() -> {
            return new IllegalArgumentException("Role not found at " + path);
        });
        boolean enabled = config.getBoolean(path + ".Enabled");
        boolean allowedToNonMembers = config.getBoolean(path + ".Trigger_By_Non_Members");
        return new RegionFlagSettings(flag, memberRoles, enabled, allowedToNonMembers);
    }

    @Override
    public void write(@NotNull JYML config, @NotNull String path) {
        config.set(path + ".Flag", flag.getName());
        config.set(path + ".Trigger_Roles", triggerRoles.stream().map(MemberRole::getId).collect(Collectors.toSet()));
        config.set(path + ".Enabled", enabled);
        config.set(path + ".Trigger_By_Non_Members", triggerByNonMembers);
    }
}
