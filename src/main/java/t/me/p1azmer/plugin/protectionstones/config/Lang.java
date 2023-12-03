package t.me.p1azmer.plugin.protectionstones.config;

import t.me.p1azmer.engine.api.lang.LangKey;
import t.me.p1azmer.engine.lang.EngineLang;
import t.me.p1azmer.plugin.protectionstones.Placeholders;

import static t.me.p1azmer.engine.utils.Colors.*;

public class Lang extends EngineLang {
    public static final LangKey REGION_SUCCESSFULLY_CREATED = LangKey.of("Messages.Region.Successfully.Created", GREEN + "Region " + GRAY + "#" + Placeholders.REGION_ID + GREEN + " successfully created!");
}
