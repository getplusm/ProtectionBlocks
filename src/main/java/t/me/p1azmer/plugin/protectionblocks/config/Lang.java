package t.me.p1azmer.plugin.protectionblocks.config;

import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.lang.EngineLang;
import t.me.p1azmer.plugin.protectionblocks.Placeholders;

import static t.me.p1azmer.engine.utils.Colors2.*;

public class Lang extends EngineLang {
    public static final LangKey COMMAND_EDITOR_DESC = LangKey.of("Command.Editor.Desc", "Open the region editor.");

    public static final LangKey COMMAND_GIVE_DESC = LangKey.of("Command.Give.Desc", "Give specified region block to a player.");
    public static final LangKey COMMAND_GIVE_USAGE = LangKey.of("Command.Give.Usage", "<region block> <player> <amount>");
    public static final LangKey COMMAND_GIVE_DONE = LangKey.of("Command.Give.Done", GRAY + "Given " + GREEN + "x%amount% %region_block_name%" + GRAY + " to " + GREEN + "%player_name%" + GRAY + ".");

    public static final LangKey COMMAND_TELEPORT_DESC = LangKey.of("Command.Teleport.Desc", "Teleport to the region block.");
    public static final LangKey COMMAND_TELEPORT_USAGE = LangKey.of("Command.Teleport.Usage", "<region_id>");
    public static final LangKey COMMAND_TELEPORT_DONE = LangKey.of("Command.Teleport.Done", GREEN + "You have successfully teleported to the region block!");

    public static final LangKey COMMAND_MENU_DESC = LangKey.of("Command.Menu.Desc", "Open the Menu of region block.");
    public static final LangKey COMMAND_MENU_USAGE = LangKey.of("Command.Menu.Usage", "<region_id>");
    public static final LangKey COMMAND_MENU_DONE = LangKey.of("Command.Menu.Done", GREEN + "The region block menu is open!");

    public static final LangKey COMMAND_PREVIEW_DESC = LangKey.of("Command.Preview.Desc", "Open the Preview Menu of region blocks.");

    public static final LangKey ERROR_REGION_BLOCK_INVALID = LangKey.of("Error.Region_Block.Invalid", RED + "Invalid Region Block!");

    public static final LangKey ERROR_REGION_NOT_FOUND = LangKey.of("Error.Region.Not_Found", RED + "Region not found!");
    public static final LangKey REGION_SUCCESS_CREATED = LangKey.of("Messages.Region.Success.Created", GREEN + "New Region successfully created!");

    public static final LangKey REGION_SUCCESS_CREATED_WITH_LIFE_TIME = LangKey.of("Messages.Region.Success.Life_Time_Created", NO_PREFIX + GREEN + "The new region has been successfully created." + RED + "Be careful" + GREEN + ", this region will be destroyed in: " + LIGHT_PURPLE + Placeholders.REGION_EXPIRE_IN);
    public static final LangKey REGION_ERROR_CREATED_NEARBY_RG = LangKey.of("Messages.Region.Error.Nearby_Region", RED + "You cannot create a region here, as it will cross over with another one!");
    public static final LangKey REGION_ERROR_CREATED_LIMIT = LangKey.of("Messages.Region.Error.Limit", RED + "You cannot create a region of this type because you have reached the limit!");
    public static final LangKey REGION_ERROR_CREATED_GLOBAL_LIMIT = LangKey.of("Messages.Region.Error.Global_Limit", RED + "You cannot create a region because you have reached the limit!");
    public static final LangKey REGION_SUCCESS_DESTROY_SELF = LangKey.of("Messages.Region.Success.Destroy.Self", GREEN + "Region " + GRAY + "\u29c8" + Placeholders.REGION_ID + GREEN + " successfully removed!");
    public static final LangKey REGION_SUCCESS_DESTROY_TARGET = LangKey.of("Messages.Region.Success.Destroy.Target", LIGHT_PURPLE + "You've just destroyed a region " + GRAY + "\u29c8 " + LIGHT_PURPLE + "!");
    public static final LangKey REGION_SUCCESS_DAMAGED_TARGET = LangKey.of("Messages.Region.Success.Damaged.Target", GREEN + "Region " + GRAY + "\u29c8" + Placeholders.REGION_ID + GREEN + " has been damaged, but it's not destroyed yet");
    public static final LangKey REGION_SUCCESS_DAMAGED_SELF = LangKey.of("Messages.Region.Success.Damaged.Self", NO_PREFIX +
            RED + BOLD + "!! ALARM !!\n" +
            BLUE + "Your Region " + ORANGE + Placeholders.REGION_LOCATION + BLUE + " has been damaged!" +
            RED + "\nHe has " + BOLD + Placeholders.REGION_HEALTH + RED + " health(s) left and will be destroyed!\n");
    public static final LangKey REGION_DESTROY_NOTIFY = LangKey.of("Messages.Region.Notify.Destroy", NO_PREFIX +
            RED + BOLD + "!! ALARM !!\n" +
            BLUE + "Your Region " + ORANGE + Placeholders.REGION_LOCATION + BLUE + " was destroyed!\n");

    public static final LangKey REGION_ENTER_NOTIFY = LangKey.of("Messages.Region.Notify.Enter", GRAY + "You've entered a " + LIGHT_PURPLE + Placeholders.REGION_OWNER_NAME + GRAY + " region");
    public static final LangKey REGION_EXIT_NOTIFY = LangKey.of("Messages.Region.Notify.Exit", GRAY + "You've leave a " + LIGHT_PURPLE + Placeholders.REGION_OWNER_NAME + GRAY + " region");
    public static final LangKey REGION_ERROR_BREAK = LangKey.of("Messages.Region.Notify.Break", ORANGE + "Hey!" + GRAY + " I'm sorry, but you " + ORANGE + "can't break this block" + GRAY + " here!");
    public static final LangKey REGION_ERROR_INTERACT = LangKey.of("Messages.Region.Notify.Interact", ORANGE + "Hey!" + GRAY + " I'm sorry, but you " + ORANGE + "can't interact with this" + GRAY + " here!");
    public static final LangKey REGION_CREATE_CANCELLED_VIA_EVENT = LangKey.of("Message.Region.Create.Error.Event", RED + "You cannot create a region for unknown reasons");

    public static final LangKey MENU_REGION_NO_ACCESS = LangKey.of("Messages.Menu.No_Access", RED + "You don't have access to open this menu!");
    public static final LangKey MENU_REGION_DEPOSIT_NO_ENOUGH_MONEY = LangKey.of("Messages.Menu.Deposit.No_Enough_Money", RED + "You don't have enough money to pay for it!");
    public static final LangKey MENU_REGION_DEPOSIT_SUCCESS = LangKey.of("Messages.Menu.Deposit.Success", GREEN + "The life of the region has been successfully paid for!");
    public static final LangKey MENU_MEMBERS_KICK_SUCCESS = LangKey.of("Messages.Menu.Members.Kick.Success", GREEN + "Member " + LIGHT_YELLOW + Placeholders.MEMBER_NAME + GREEN + " success removed from region!");

    public static final LangKey Editor_Region_Block_Enter_Create = new LangKey("Editor.Region.Block.Enter.Create", GRAY + "Enter " + GREEN + "unique " + GRAY + "block " + GREEN + "identifier" + GRAY + "...");
    public static final LangKey Editor_Region_Block_Enter_World = new LangKey("Editor.Region.Block.Enter.World", GRAY + "Enter " + GREEN + "world name" + GRAY + "...");
    public static final LangKey Editor_Region_Block_Enter_Name = new LangKey("Editor.Region.Block.Enter.Name", GRAY + "Enter " + GREEN + "name" + GRAY + "...");
    public static final LangKey Editor_Region_Enter_Player_name = new LangKey("Editor.Region.Enter.Player_Name", GRAY + "Enter " + GREEN + "player name" + GRAY + "...");
    public static final LangKey Editor_Region_Block_Enter_Currency = new LangKey("Editor.Region.Block.Enter.Currency", GRAY + "Enter " + GREEN + "currency" + GRAY + "...");
    public static final LangKey Editor_Region_Block_Enter_Value = new LangKey("Editor.Region.Block.Enter.Value", GRAY + "Enter " + GREEN + "value" + GRAY + "...");
    public static final LangKey Editor_Region_Block_Enter_LifeTime = new LangKey("Editor.Region.Block.Enter.Life_Time", GRAY + "Enter " + GREEN + "name " + GRAY + "and " + GREEN + "time in seconds" + GRAY + "...");
    public static final LangKey Editor_Region_Block_Enter_Hologram_Template = LangKey.of("Editor.Region.Block.Enter.HologramTemplate", GRAY + "Enter " + GREEN + "hologram template " + GRAY + "...");
    public static final LangKey Editor_Region_Block_Error_Exist = new LangKey("Editor.Region.Block.Error.Exist", "&cBlock already exists!");
    public static final LangKey Editor_Region_Members_Player_NF = new LangKey("Editor.Region.Members.Error.Player.Not_Found", "&cPlayer not found!");
    public static final LangKey Editor_Region_Members_Player_Already = new LangKey("Editor.Region.Members.Error.Player.Already", "&cPlayer is already in your region!");
    public static final LangKey Editor_Region_Block_Error_Currency_NF = new LangKey("Editor.Region.Block.Error.Currency.Not_Found", "&cCurrency not found!");
}
