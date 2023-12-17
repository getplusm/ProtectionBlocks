package t.me.p1azmer.plugin.protectionblocks.region.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.api.placeholder.IPlaceholderMap;
import t.me.p1azmer.engine.api.placeholder.PlaceholderMap;
import t.me.p1azmer.engine.utils.PlayerUtil;
import t.me.p1azmer.engine.utils.TimeUtil;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;

import java.util.UUID;

public class RegionMember implements IPlaceholderMap {
    private final UUID id;
    private final String name;
    private final long joinTime;

    private final PlaceholderMap placeholderMap;
    public RegionMember(@NotNull UUID id, @NotNull String name, long joinTime) {
        this.id = id;
        this.name = name;
        this.joinTime = joinTime;

        this.placeholderMap = new PlaceholderMap()
                .add(Placeholders.MEMBER_JOIN_TIME, ()-> TimeUtil.formatTime(System.currentTimeMillis()-this.getJoinTime()))
                .add(Placeholders.MEMBER_NAME, this::getName)
        ;
    }

    public static RegionMember of(@NotNull Player player){
        return new RegionMember(player.getUniqueId(), player.getName(), System.currentTimeMillis());
    }

    public static RegionMember read(@NotNull JYML cfg, @NotNull String path){
        UUID id = UUID.fromString(cfg.getString(path+".Id", ""));
        String name = cfg.getString(path+".Name", id.toString());
        long joinTime = cfg.getLong(path+".Join_Time");
        return new RegionMember(id, name, joinTime);
    }

    public void write(@NotNull JYML cfg, @NotNull String path){
        cfg.set(path+".Id", this.getId().toString());
        cfg.set(path+".Name", this.getName());
        cfg.set(path+".Join_Time", this.getJoinTime());
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public long getJoinTime() {
        return joinTime;
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Nullable
    public Player getPlayer(){
        return PlayerUtil.getPlayer(this.getName());
    }
}
