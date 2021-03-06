package com.voxelgameslib.voxelgameslib.command.commands;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.lang.LangHandler;
import com.voxelgameslib.voxelgameslib.lang.LangKey;
import com.voxelgameslib.voxelgameslib.lang.Locale;
import com.voxelgameslib.voxelgameslib.persistence.PersistenceHandler;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.CommandUtil;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.annotation.UnknownHandler;

/**
 * Handles all commands related to lang and i18n
 */
@Singleton
@CommandAlias("lang")
@SuppressWarnings("JavaDoc") // commands don't need javadoc, go read the command's descriptions
public class LangCommands extends BaseCommand {

    @Inject
    private LangHandler langHandler;
    @Inject
    private PersistenceHandler persistenceHandler;

    @Default
    @UnknownHandler
    @CommandPermission("%user")
    public void lang(@Nonnull User sender) {
        StringBuilder sb = new StringBuilder();
        for (Locale loc : langHandler.getInstalledLocales()) {
            sb.append(loc.getTag()).append(" (").append(loc.getName()).append("), ");
        }
        sb.setLength(sb.length() - 1);
        Lang.msg(sender, LangKey.LANG_INSTALLED, sb.toString());
        Lang.msg(sender, LangKey.LANG_CURRENT, sender.getLocale().getName());
        Lang.msg(sender, LangKey.TRANSLATED_BY);
    }

    @Subcommand("set")
    @CommandPermission("%user")
    @Syntax("<locale> - the new locale you want to use")
    @CommandCompletion("@locales")
    public void set(@Nonnull User sender, @Nonnull Locale locale) {
        sender.setLocale(locale);
        Lang.msg(sender, LangKey.LANG_UPDATE, locale.getName());
        if (!langHandler.getInstalledLocales().contains(locale)) {
            Lang.msg(sender, LangKey.LANG_NOT_ENABLED, locale.getName());
        }

        persistenceHandler.getProvider().saveUser(sender);
    }
}
