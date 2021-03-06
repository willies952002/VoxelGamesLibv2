package com.voxelgameslib.voxelgameslib.components.scoreboard;

import com.voxelgameslib.voxelgameslib.handler.Handler;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

public class ScoreboardHandler implements Handler {

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Nonnull
    public Scoreboard createScoreboard(@Nonnull String name) {
        BukkitScoreboard scoreboard = new BukkitScoreboard();
        scoreboard.setImplObject(Bukkit.getScoreboardManager().getNewScoreboard());
        scoreboard.setTitle(name);
        return scoreboard;
    }
}
