package t.me.p1azmer.plugin.protectionblocks.region.impl;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.engine.utils.TimeUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;

import java.util.UUID;

@Getter
public class RegionMember implements Placeholder {
    private final UUID uuid;
    private final String name;
    private final long joinTime;

    private final PlaceholderMap placeholders;

    public RegionMember(@NotNull UUID uuid, @NotNull String name, long joinTime) {
        this.uuid = uuid;
        this.name = name;
        this.joinTime = joinTime;

        this.placeholders = new PlaceholderMap()
          .add(Placeholders.MEMBER_JOIN_TIME, () -> TimeUtil.formatTime(System.currentTimeMillis() - this.getJoinTime()))
          .add(Placeholders.MEMBER_NAME, this::getName);
    }

    public static RegionMember of(@NotNull Player player) {
        return new RegionMember(player.getUniqueId(), player.getName(), System.currentTimeMillis());
    }

    public static RegionMember read(@NotNull JYML cfg, @NotNull String path) {
        UUID id = UUID.fromString(cfg.getString(path + ".Id", ""));
        String name = cfg.getString(path + ".Name", id.toString());
        long joinTime = cfg.getLong(path + ".Join_Time");
        return new RegionMember(id, name, joinTime);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Id", this.getUuid().toString());
        cfg.set(path + ".Name", this.getName());
        cfg.set(path + ".Join_Time", this.getJoinTime());
    }

    @Nullable
    public Player getPlayer() {
        return PlayerUtil.getPlayer(this.getName());
    }
}