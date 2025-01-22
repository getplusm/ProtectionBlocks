package t.me.p1azmer.plugin.protectionblocks.editor;

import t.me.p1azmer.engine.api.editor.EditorLocale;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;

import static t.me.p1azmer.engine.utils.Colors2.*;

public class EditorLocales extends t.me.p1azmer.engine.api.editor.EditorLocales {

    private static final String PREFIX = "Editor.";

    public static final EditorLocale REGION_BLOCK_OBJECT = builder(PREFIX + "Region.Block.Object")
            .name(Placeholders.REGION_BLOCK_NAME + " &7(ID: &f" + Placeholders.REGION_BLOCK_ID + "&7)")
            .click(LMB, "Configure")
            .click(SHIFT_RMB, "Delete " + RED + "(No Undo)")
            .build();
    public static final EditorLocale REGION_BLOCK_CREATE = builder(PREFIX + "Region.Block.Create")
            .name("New Region Block")
            .build();
    public static final EditorLocale REGION_BLOCK_ITEM = builder(PREFIX + "Region.Block.Change.Item")
            .text("Block Item")
            .emptyLine()
            .text("Sets the region block.", "This block will be used as the root of the region")
            .emptyLine()
            .click(DRAG_DROP, "Replace Item")
            .click(RMB, "Get a Copy")
            .build();
    public static final EditorLocale REGION_BLOCK_NAME = builder(PREFIX + "Region.Block.Change.Name")
            .name("Name")
            .text("Sets the displayed name of the block", "Used in messages, menus & hologram")
            .emptyLine()
            .currentHeader()
            .current("Displayed Name", Placeholders.REGION_BLOCK_NAME + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale REGION_BLOCK_SIZE = builder(PREFIX + "Region.Block.Change.Size")
            .name("Region Size")
            .text(
                    "Sets what the region size",
                    "will be for this block",
                    "",
                    RED + "**Experimental function**",
                    "",
                    RED + "I do not recommend that you",
                    RED + "set infinity to true,",
                    RED + "because this will slow down both",
                    RED + "the server and the plugin very much."
            )
            .emptyLine()
            .currentHeader()
            .current("Radius", Placeholders.REGION_BLOCK_SIZE + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .current("Infinity Y", Placeholders.REGION_IS_INFINITY_Y_BLOCKS + GRAY + " (" + WHITE + RMB + GRAY + ")")
            .build();
    public static final EditorLocale REGION_BLOCK_STRENGTH = builder(PREFIX + "Region.Block.Change.Strength")
            .name("Region Strength")
            .text("Sets the block strength of the region.", "The block must be broken as", "many times as its strength.")
            .emptyLine()
            .currentHeader()
            .current("Strength", Placeholders.REGION_BLOCK_STRENGTH + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();

    public static final EditorLocale REGION_BLOCK_DEPOSIT = builder(PREFIX + "Region.Block.Change.Deposit")
            .name("Region Deposit")
            .text("Sets the sum of the extension of the block's life", "Sets the currency for payment")
            .emptyLine()
            .currentHeader()
            .current("Price", Placeholders.REGION_BLOCK_DEPOSIT_PRICE + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .current("Currency", Placeholders.REGION_BLOCK_DEPOSIT_CURRENCY + GRAY + " (" + WHITE + RMB + GRAY + ")")
            .build();

    public static final EditorLocale REGION_HOLOGRAM = builder(PREFIX + "Region.Block.Change.Hologram")
            .name("Hologram")
            .text("Creates hologram above block",
                    "with certain text template.",
                    GRAY + "Example:",
                    GRAY + "You can disable hologram and create 'invisible' region",
                    "",
                    GRAY + "If set to " + LIGHT_YELLOW + "In Region" + GRAY + ",",
                    GRAY + "the region hologram will only be displayed",
                    GRAY + "when the player is in the region")
            .emptyLine()
            .currentHeader()
            .current("In Region Only", Placeholders.REGION_BLOCK_HOLOGRAM_IN_REGION + GRAY + " (" + WHITE + DROP_KEY + GRAY + ")")
            .current("Enabled", Placeholders.REGION_BLOCK_HOLOGRAM_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .current("Template", Placeholders.REGION_BLOCK_HOLOGRAM_TEMPLATE + GRAY + " (" + WHITE + RMB + GRAY + ")")
            .build();
    public static final EditorLocale REGION_BLOCK_LIFE_TIME = builder(PREFIX + "Region.Block.Change.Life_Time")
            .name("Region Life Time")
            .text(
                    "All regions can be made to have a",
                    "life time with its extension.",
                    "You can add a group and",
                    "set any time for it.",
                    "After this time the region will be",
                    "deleted as well as the block itself",
                    "",
                    RED + "You can only change it in the " + BOLD + "config.yml" + RED + " file"
            )
            .currentHeader()
            .current("Enabled", Placeholders.REGION_BLOCK_LIFE_TIME_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale REGION_BLOCK_PLACE_LIMIT = builder(PREFIX + "Region.Block.Change.Place_Limit")
            .name("Region Place Limit")
            .text(
                    "You can set a group limit to place this block.",
                    "If a player reaches the limit,",
                    "he will not be able to create a region of this block",
                    "",
                    RED + "You can only change it in the " + BOLD + "config.yml" + RED + " file"
            )
            .currentHeader()
            .current("Enabled", Placeholders.REGION_BLOCK_PLACE_LIMIT_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale REGION_BLOCK_GROUP_SIZE = builder(PREFIX + "Region.Block.Change.Group_Size")
            .name("Region Groups Size")
            .text(
                    "You can set the region size",
                    "to individual for per group",
                    "",
                    RED + "You can only change it in the " + BOLD + "config.yml" + RED + " file"
            )
            .currentHeader()
            .current("Enabled", Placeholders.REGION_BLOCK_GROUP_SIZE_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .build();
    public static final EditorLocale REGION_BLOCK_WORLDS = builder(PREFIX + "Region.Block.Change.Worlds")
            .name("Region Worlds")
            .text("List of worlds where this",
                    "region block will be available")
            .emptyLine()
            .currentHeader()
            .current("List", Placeholders.REGION_BLOCK_WORLDS)
            .emptyLine()
            .click(LMB, "Add world")
            .click(SHIFT_RMB, "Clear")
            .build();
    public static final EditorLocale REGION_BLOCK_BREAKERS_ICON = builder(PREFIX + "Region.Block.Breakers.Navigate")
            .name("Region Breakers")
            .text(YELLOW + "Breakers " + GRAY + "-" + LIGHT_YELLOW + " Items that can destroy a region")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale REGION_BLOCK_FLAGS = builder(PREFIX + "Region.Block.Flags.Navigate")
            .name("Region Flags")
            .click(LMB, "Navigate")
            .build();
    public static final EditorLocale REGION_BLOCK_RECIPE_ICON = builder(PREFIX + "Region.Block.Recipe.Navigate")
            .name("Block Recipe")
            .text(
                    "Create a crafting recipe for this block",
                    "",
                    YELLOW + "Enabled: " + Placeholders.REGION_BLOCK_RECIPE_ENABLED + GRAY + " (" + WHITE + DROP_KEY + GRAY + ")"
            )
            .click(LMB, "Navigate")
            .click(SHIFT_RMB, "Clear")
            .build();
    public static final EditorLocale REGION_BLOCK_RECIPE_ITEM = builder(PREFIX + "Region.Block.Recipe.Item")
            .emptyLine()
            .click(DRAG_DROP, "Replace Item")
            .click(RMB, "Get a Copy")
            .build();
    public static final EditorLocale REGION_BLOCK_RECIPE_BLOCK_ITEM = builder(PREFIX + "Region.Block.Recipe.Block_Item")
            .text("The result, which is a private block")
            .build();
    public static final EditorLocale REGION_BLOCK_RECIPE_CLEAR = builder(PREFIX + "Region.Block.Recipe.Clear")
            .name(RED + "Clear (No Undo)")
            .build();
    // flags
    public static final EditorLocale REGION_FLAG_OBJECT = builder(PREFIX + "Region.Block.Flag.Object")
            .name(Placeholders.FLAG_NAME)
            .current("Enabled", Placeholders.FLAG_ENABLED + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .current("Trigger by Non Members", Placeholders.FLAG_TRIGGER_BY_NON_MEMBERS + GRAY + " (" + WHITE + RMB + GRAY + ")")
            .current("Trigger Roles", Placeholders.FLAG_TRIGGER_ROLES)
            .emptyLine()
            .click(SHIFT_LMB, "Change Role Access")
            .click(SHIFT_RMB, "Clear Role Access")
            .build();
    // breakers
    public static final EditorLocale REGION_BLOCK_BREAKERS_CREATE = builder(PREFIX + "Region.Block.Breakers.Create")
            .name("New Breaker")
            .emptyLine()
            .text(GREEN + "Note" + GRAY + ":", "If you added TNT,", "it means that the block can be", "broken with a TNT blast")
            .emptyLine()
            .click(DRAG_DROP, "Add")
            .build();
    public static final EditorLocale REGION_BLOCK_BREAKERS_CLEAR = builder(PREFIX + "Region.Block.Breakers.Clear")
            .name("Clear breakers")
            .click(LMB, "Clear")
            .build();
    public static final EditorLocale REGION_BLOCK_BREAKERS_OBJECT = builder(PREFIX + "Region.Block.Breakers.Object")
            .current("Damage Type", Placeholders.REGION_BLOCK_BREAKER_DMG_TYPE + GRAY + " (" + WHITE + LMB + GRAY + ")")
            .emptyLine()
            .click(DRAG_DROP, "Replace Item")
            .click(DRAG_DROP, "Delete " + RED + "(No Undo)")
            .build();
}