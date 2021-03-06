package com.voxelgameslib.voxelgameslib.components.signs;

import com.voxelgameslib.voxelgameslib.exception.UserException;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.lang.LangKey;
import com.voxelgameslib.voxelgameslib.role.Permission;
import com.voxelgameslib.voxelgameslib.role.Role;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.user.UserHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import lombok.extern.java.Log;

@Log
@SuppressWarnings({"JavaDoc", "Duplicates"})
public class SignListener implements Listener {

    private Permission placeHolderSignPlace = Permission
            .register("sign.placeholder.place", Role.MODERATOR);
    private Permission placeHolderSignBreak = Permission
            .register("sign.placeholder.break", Role.MODERATOR);
    //TODO check perm for sign placement
    private Permission buttonSignPlace = Permission.register("sign.button.place", Role.MODERATOR);
    private Permission buttonSignBreak = Permission.register("sign.button.break", Role.MODERATOR);

    @Inject
    private SignHandler signHandler;
    @Inject
    private UserHandler userHandler;

    @EventHandler
    public void signBreakEvent(@Nonnull BlockBreakEvent event) {
        // is block a sign?
        if (event.getBlock().getState() instanceof Sign) {
            User user = userHandler.getUser(event.getPlayer().getUniqueId()).orElseThrow(() -> new UserException(
                    "Unknown user " + event.getPlayer().getDisplayName() + "(" + event.getPlayer().getUniqueId() + ")"));
            Sign sign = (Sign) event.getBlock().getState();
            for (int i = 0; i < sign.getLines().length; i++) {
                String line = sign.getLines()[i];
                for (String key : signHandler.getSignPlaceholders().getPlaceHolders().keySet()) {
                    if (line.contains("%" + key + "%")) {
                        // has user permission for that?
                        if (user.hasPermission(placeHolderSignBreak)) {
                            Lang.msg(user, LangKey.SIGNS_BREAK_SUCCESS, key);
                            event.getBlock().setType(Material.AIR);
                            return;
                        } else {
                            event.setCancelled(true);
                            Lang.msg(user, LangKey.SIGNS_BREAK_NO_PERM, key, placeHolderSignBreak.getRole().getName());
                            return;
                        }
                    }
                }

                for (String key : signHandler.getSignButtons().getButtons().keySet()) {
                    if (line.contains("%" + key + "%")) {
                        // has user permission for that?
                        if (user.hasPermission(buttonSignBreak)) {
                            Lang.msg(user, LangKey.SIGNS_BREAK_SUCCESS, key);
                            event.getBlock().setType(Material.AIR);
                            return;
                        } else {
                            event.setCancelled(true);
                            Lang.msg(user, LangKey.SIGNS_BREAK_NO_PERM, key, buttonSignBreak.getRole().getName());
                            return;
                        }
                    }
                }
            }
        }
    }
}
