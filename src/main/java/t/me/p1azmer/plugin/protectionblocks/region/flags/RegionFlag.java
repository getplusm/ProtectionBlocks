package t.me.p1azmer.plugin.protectionblocks.region.flags;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JYML;
import t.me.p1azmer.engine.utils.placeholder.Placeholder;
import t.me.p1azmer.engine.utils.placeholder.PlaceholderMap;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;
import t.me.p1azmer.plugin.protectionblocks.region.impl.Region;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class RegionFlag implements Placeholder {
    private final String name;
    private final BiFunction<FlagPlayerAction, Region, Boolean> function;
    private final Set<Object> notAllowedData;
    private final PlaceholderMap placeholders;

    public RegionFlag(String name, BiFunction<FlagPlayerAction, Region, Boolean> function, Set<Object> notAllowedData) {
        this.name = name;
        this.function = function;
        this.notAllowedData = notAllowedData;

        this.placeholders = new PlaceholderMap()
          .add(Placeholders.FLAG_NAME, this::getName)
          .add(Placeholders.FLAG_VALUES, () -> String.join("\n", this.getNotAllowedData()
                                                                     .stream()
                                                                     .map(String::valueOf)
                                                                     .collect(Collectors.toSet())));
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Name", this.getName());
        cfg.set(path + ".Not_Allowed_Data", this.getNotAllowedData());
    }

    public static RegionFlag read(@NotNull JYML cfg, @NotNull String path) {
        String name = cfg.getString(path + ".Name");
        if (name == null) return null;
        Set<Object> notAllowedData = new HashSet<>(cfg.getList(path + ".Not_Allowed_Data", new ArrayList<>()));
        return findRegionFlagByName(name, notAllowedData);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static RegionFlag findRegionFlagByName(@NotNull String name, @NotNull Set<Object> notAllowedData) {
        for (Field field : Flags.class.getFields()) {
            if (Function.class.isAssignableFrom(field.getType())) {
                try {
                    @SuppressWarnings("unchecked")
                    Function<Set<Object>, RegionFlag> func = (Function<Set<Object>, RegionFlag>) field.get(null);
                    RegionFlag regionFlag = func.apply(notAllowedData);
                    if (regionFlag != null && regionFlag.getName().equalsIgnoreCase(name)) {
                        return regionFlag;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}