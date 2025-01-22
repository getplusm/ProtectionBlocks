package t.me.p1azmer.plugin.protectionblocks.region.flags;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.ProtectionPlugin;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Flag<E extends Event, O> implements Placeholder {

    String name;
    boolean enabledDefault;
    EventHelper<E, O> eventHelper;
    PlaceholderMap placeholders;

    @NonFinal
    String displayName;
    @NonFinal
    ItemStack icon;

    public Flag(@NotNull String name, boolean enabledDefault, @NotNull ItemStack icon, @NotNull EventHelper<E, O> eventHelper) {
        this.name = name.toLowerCase();
        this.enabledDefault = enabledDefault;
        this.eventHelper = eventHelper;
        this.setDisplayName(StringUtil.capitalizeUnderscored(name));
        this.setIcon(icon);

        this.placeholders = new PlaceholderMap()
                .add(Placeholders.FLAG_NAME, this::getDisplayName);
    }

    @NotNull
    public static <E extends Event, O> Flag<E, O> create(@NotNull String name,
                                                         boolean enabledDefault,
                                                         @NotNull EventHelper<E, O> eventHelper) {
        return create(name, enabledDefault, Material.MAP, eventHelper);
    }

    @NotNull
    public static <E extends Event, O> Flag<E, O> create(@NotNull String name,
                                                         boolean enabledDefault,
                                                         @NotNull Material icon,
                                                         @NotNull EventHelper<E, O> eventHelper) {
        return create(name, enabledDefault, new ItemStack(icon), eventHelper);
    }

    @NotNull
    public static <E extends Event, O> Flag<E, O> create(@NotNull String name,
                                                         boolean enabledDefault,
                                                         @NotNull ItemStack icon,
                                                         @NotNull EventHelper<E, O> eventHelper) {
        return new Flag<>(name, enabledDefault, icon, eventHelper);
    }

    public boolean loadSettings(@NotNull ProtectionPlugin plugin) {
        JYML config = plugin.getConfig();
        String path = "Flags." + this.getName();
        JOption<Boolean> enabled = JOption.create(path + ".Default.Enabled", true);
        if (!enabled.read(config)) {
            return false;
        }
        JOption<String> displayName = JOption.create(path + ".DisplayName", this.getDisplayName());
        JOption<ItemStack> icon = JOption.create(path + ".Icon", this.getIcon());
        this.setDisplayName(displayName.read(config));
        this.setIcon(icon.read(config));

        return true;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    @NotNull
    public Flag<E, O> setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
        return this;
    }

    public void saveSettings(@NotNull ProtectionPlugin plugin) {
        JYML config = plugin.getConfig();
        String path = "Flags." + this.getName();

        config.set(path + ".Default.Enabled", isEnabledDefault());
        config.set(path + ".DisplayName", this.getDisplayName());
        config.set(path + ".Icon", this.getIcon());
    }
}
