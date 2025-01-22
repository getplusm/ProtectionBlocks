package t.me.p1azmer.plugin.protectionblocks.region.flags;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.plugin.protectionblocks.Perms;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;
import t.me.p1azmer.plugin.protectionblocks.region.members.RegionMember;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WrappedEvent<E extends Event, O> implements Listener, EventExecutor, RegionFlagProcessor<O, E> {

    ProtectionPlugin plugin;
    Class<E> eventClass;
    Flag<E, O> flag;

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event bukkitEvent) {
        if (!this.eventClass.isAssignableFrom(bukkitEvent.getClass())) return;

        E event = this.eventClass.cast(bukkitEvent);
        this.flag.getEventHelper().handle(this.plugin, event, this);
    }

    @Override
    public void triggerFlag(@Nullable Player player, @Nullable O object, @NotNull E event) {
        if (player == null) return;

        Location location = player.getLocation();
        if (player.isOp() || player.hasPermission(Perms.BYPASS)) return;

        plugin.getRegionManager().getOptionalRegionByLocation(location).ifPresent(region -> {
            if (region.isOwner(player.getUniqueId())) {
                return;
            }

            region.getFlagSetting(flag.getName()).ifPresent(flagSettings -> {
                if (!flagSettings.isEnabled()) {
                    return;
                }

                RegionMember regionMember = region.getMemberByPlayer(player);
                if (regionMember == null) {
                    if (flagSettings.isTriggerByNonMembers()) {
                        cancelBukkitEvent(event);
                    }
                    return;
                }
                if (flagSettings.getTriggerRoles().contains(regionMember.getRole())) {
                    cancelBukkitEvent(event);
                }
            });
        });
    }

    private static <E extends Event> void cancelBukkitEvent(E event) {
        if (event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
    }
}
