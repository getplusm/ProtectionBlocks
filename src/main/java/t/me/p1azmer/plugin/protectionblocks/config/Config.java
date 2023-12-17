package t.me.p1azmer.plugin.protectionblocks.config;

import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.EngineUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static t.me.p1azmer.engine.utils.Colors.*;
import static t.me.p1azmer.plugin.protectionblocks.Placeholders.*;

public class Config {
    public static final String REGION_DIR = "/regions/";
    public static final String REGION_BLOCKS_DIR = "/blocks/";

    public static final JOption<Map<String, List<String>>> REGION_HOLOGRAM_TEMPLATES = JOption.forMap("Settings.Hologram.Templates",
            (cfg, path, key) -> Colorizer.apply(cfg.getStringList(path + "." + key)),
            () -> {
                return Map.of(
                        DEFAULT, Arrays.asList(
                                WHITE + REGION_BLOCK_NAME,
                                YELLOW + BOLD + REGION_OWNER_NAME
                        )
                );
            },
            "Here you can create your own hologram text templates to use it in region blocks.",
            "You can use 'Region' placeholders: " + WIKI_PLACEHOLDERS,
            EngineUtils.PLACEHOLDER_API + " is also supported here (if hologram handler supports it)."
    ).setWriter((cfg, path, map) -> map.forEach((id, text) -> cfg.set(path + "." + id, text)));

    public static final JOption<Double> REGION_HOLOGRAM_Y_OFFSET = JOption.create("Settings.Hologram.Y_Offset",
            1.5D,
            "Sets Y offset for hologram location.");

    public static final JOption<String> UNBREAKABLE = JOption.create("Settings.Unbreakable.Name", RED + "Unbreakable", "Sets the name that will be displayed if the region is unbreakable");
    public static final JOption<Boolean> REGION_BLOCK_BREAK_OWNER_ONLY = JOption.create("Settings.Region_Block.Break_Owner_Only", false, "Sets the setting that only the owner of the region can break the Region Block");
}
