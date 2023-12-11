package t.me.p1azmer.plugin.protectionblocks;

import t.me.p1azmer.engine.api.server.JPermission;

public class Perms {
    private static final String PREFIX = "protectionstones.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final JPermission PLUGIN = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all the plugin functions.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all the plugin commands.");

    public static final JPermission COMMAND_RELOAD = new JPermission(PREFIX_COMMAND + "reload", "Access to the 'reload' sub-command.");
    public static final JPermission COMMAND_EDITOR = new JPermission(PREFIX_COMMAND + "editor", "Access to the 'editor' sub-command.");
    public static final JPermission COMMAND_GIVE = new JPermission(PREFIX_COMMAND + "give", "Access to the 'give' sub-command.");

    static {
        PLUGIN.addChildren(COMMAND);

        COMMAND.addChildren(COMMAND_RELOAD, COMMAND_EDITOR, COMMAND_GIVE);
    }
}
