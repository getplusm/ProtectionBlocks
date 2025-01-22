package t.me.p1azmer.plugin.protectionblocks.config;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.engine.api.config.JOption;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.EngineUtils;
import t.me.p1azmer.plugin.protectionblocks.region.members.MemberRole;

import java.util.*;

import static t.me.p1azmer.engine.utils.Colors2.*;
import static t.me.p1azmer.plugin.protectionblocks.Placeholders.*;

public class Config {
    public static final String REGION_DIR = "/regions/";
    public static final String REGION_BLOCKS_DIR = "/blocks/";

    public static final JOption<String> FLAG_EDITOR_TITLE = JOption.create(
            "Settings.Region.Editor.Flags.Title",
            "Region Flags"
    );

    public static final JOption<Boolean> FLAGS_ANTI_GLITCH_TRACK_BLOCKS = JOption.create("Flags.Anti_Glitch.Track_Player_Blocks",
            true,
            "Sets whether plugin should store data of player placed blocks and prevent them");

    public static final JOption<Map<String, List<String>>> REGION_HOLOGRAM_TEMPLATES = JOption.create("Settings.Hologram.Templates",
            (cfg, path, def) -> {
                Map<String, List<String>> map = new HashMap<>();
                for (String key : cfg.getSection(path)) {
                    List<String> list = Colorizer.apply(cfg.getStringList(path + "." + key));

                    map.put(key, list);
                }
                return map;
            },
            (cfg, path, map) -> map.forEach((id, text) -> cfg.set(path + "." + id, text)),
            () -> Map.of(DEFAULT, Arrays.asList(WHITE + REGION_BLOCK_NAME, YELLOW + BOLD + REGION_OWNER_NAME)),
            "Here you can create your own hologram text templates to use it in region blocks.",
            "You can use 'Region' placeholders: " + WIKI_PLACEHOLDERS,
            EngineUtils.PLACEHOLDER_API + " is also supported here (if hologram handler supports it)."
    );

    public static final JOption<Double> REGION_HOLOGRAM_Y_OFFSET = JOption.create(
            "Settings.Hologram.Y_Offset",
            1.5D,
            "Sets Y offset for hologram location."
    );

    public static final JOption<Set<MemberRole>> MEMBER_ROLES = new JOption<>(
            "Settings.Region.Members.Roles",
            (cfg, path, def) -> {
                Set<MemberRole> roles = new HashSet<>();
                for (String key : cfg.getSection(path)) {
                    roles.add(MemberRole.read(cfg, path + "." + key));
                }
                return roles;
            }, (cfg, path, set) -> set.forEach(role -> role.write(cfg, path + "." + role.getId())),
            Set.of(
                    new MemberRole("Member", GRAY + "Member", true, false, 100),
                    new MemberRole("Raid_Master", BLUE + "Raid Master", false, false, 99),
                    new MemberRole("Co_Owner", RED + "Co-Owner", false, true, 0)
            ),
            "Here you can set the roles that are allowed to use region blocks.");

    public static final JOption<String> UNBREAKABLE = JOption.create(
            "Settings.Unbreakable.Name",
            RED + "Unbreakable",
            "Sets the name that will be displayed if the region is unbreakable"
    );
    public static final JOption<Boolean> REGION_BLOCK_BREAK_OWNER_ONLY = JOption.create(
            "Settings.Region_Block.Break_Owner_Only",
            false,
            "Sets the setting that only the owner of the region can break the Region Block"
    );

    public static final JOption<Integer> TOTAL_REGION_LIMIT = JOption.create(
            "Settings.Region.Limit",
            0,
            "Sets the total region limit per user.",
            "Set to 0 to disable"
    );

    public static @NotNull MemberRole getDefaultMemberRole() {
        return MEMBER_ROLES.get().stream().filter(MemberRole::isDefaultRole).max(Comparator.comparingInt(MemberRole::getPriority)).orElseThrow(() -> {
            return new IllegalStateException("Default role not found");
        });
    }

    public static @NotNull MemberRole getNextMemberRole(@NotNull MemberRole currentRole) {
        var filteredRoles = MEMBER_ROLES.get().stream()
                .filter(role -> !role.isPermanent())
                .sorted(Comparator.comparingInt(MemberRole::getPriority))
                .toList();

        if (filteredRoles.isEmpty()) {
            throw new IllegalArgumentException("Not have enough roles to roll next");
        }

        int currentIndex = filteredRoles.indexOf(currentRole);
        if (currentIndex == -1) {
            throw new IllegalArgumentException("Current role not found in filtered roles");
        }

        int nextIndex = (currentIndex + 1) % filteredRoles.size();
        return filteredRoles.get(nextIndex);
    }
}
