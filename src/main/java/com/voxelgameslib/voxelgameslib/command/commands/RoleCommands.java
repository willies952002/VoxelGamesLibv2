package com.voxelgameslib.voxelgameslib.command.commands;

import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.lang.LangKey;
import com.voxelgameslib.voxelgameslib.persistence.PersistenceHandler;
import com.voxelgameslib.voxelgameslib.role.Role;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.utils.CommandUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * Handles all commands related to roles
 */
@Singleton
@SuppressWarnings("JavaDoc") // commands don't need javadoc, go read the command's descriptions
@CommandAlias("role")
public class RoleCommands extends BaseCommand {

    @Inject
    private PersistenceHandler persistenceHandler;

    @Default
    @UnknownHandler
    @Subcommand("help")
    @CommandPermission("%user")
    public void help(@Nonnull User sender) {
        Lang.msg(sender, LangKey.ROLE_SELF, sender.getRole().getName());
        CommandUtil.printHelp(sender, getCommandHelp());
    }

    @CommandPermission("%moderator")
    @Syntax("[user] - the user which role you wanna get")
    public void role(@Nonnull User sender, @Nullable @co.aikar.commands.annotation.Optional User user) {
        if (user == null) {
            Lang.msg(sender, LangKey.ROLE_SELF, sender.getRole().getName());
            return;
        }

        Lang.msg(sender, LangKey.ROLE_OTHERS, user.getDisplayName(), user.getRole().getName());
    }

    @Subcommand("set")
    @Syntax("<user> - the user which role should be changed\n"
            + "<role> - the new role")
    @CommandPermission("%admin")
    @CommandCompletion("@players @roles")
    public void set(@Nonnull User sender, @Nonnull User user, @Nonnull Role role) {
        user.setRole(role);
        user.applyRolePrefix();
        user.applyRoleSuffix();
        Lang.msg(sender, LangKey.ROLE_UPDATED_OTHER,
                user.getDisplayName(), role.getName());
        persistenceHandler.getProvider().saveUser(user);
    }
}
