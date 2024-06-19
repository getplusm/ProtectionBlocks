package t.me.p1azmer.plugin.protectionblocks;

import t.me.p1azmer.engine.api.server.JPermission;

public class Perms {
    private static final String PREFIX = "protectionblocks.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS = PREFIX + "bypass.";

    public static final JPermission PLUGIN = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all the plugin functions.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all the plugin commands.");
    public static final JPermission BYPASS = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Access to all bypass.");

    public static final JPermission COMMAND_RELOAD = new JPermission(PREFIX_COMMAND + "reload", "Access to the 'reload' sub-command.");
    public static final JPermission COMMAND_EDITOR = new JPermission(PREFIX_COMMAND + "editor", "Access to the 'editor' sub-command.");
    public static final JPermission COMMAND_GIVE = new JPermission(PREFIX_COMMAND + "give", "Access to the 'give' sub-command.");

    public static final JPermission COMMAND_DEPOSIT = new JPermission(PREFIX_COMMAND + "deposit", "Access to the 'deposit' sub-command.");
    public static final JPermission COMMAND_DEPOSIT_OTHER = new JPermission(PREFIX_COMMAND + "deposit.other", "Access to the 'deposit other' sub-command.");

    public static final JPermission COMMAND_TELEPORT = new JPermission(PREFIX_COMMAND + "teleport", "Access to the 'teleport' sub-command.");
    public static final JPermission COMMAND_MENU = new JPermission(PREFIX_COMMAND + "menu", "Access to the 'menu' sub-command.");
    public static final JPermission COMMAND_PREVIEW_MENU = new JPermission(PREFIX_COMMAND + "preview.menu", "Access to the 'preview menu' sub-command.");

    public static final JPermission BYPASS_REGION_MANIPULATION = new JPermission(PREFIX_BYPASS + "region.manipulation", "Access to the manipulation in regions");

    static {
        PLUGIN.addChildren(COMMAND, BYPASS);

        COMMAND.addChildren(COMMAND_RELOAD, COMMAND_EDITOR, COMMAND_GIVE,
          COMMAND_DEPOSIT, COMMAND_DEPOSIT_OTHER, COMMAND_TELEPORT, COMMAND_MENU, COMMAND_PREVIEW_MENU);

        BYPASS.addChildren(BYPASS_REGION_MANIPULATION);
    }
}
