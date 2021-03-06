package com.voxelgameslib.voxelgameslib.feature.features;

import com.voxelgameslib.voxelgameslib.components.scoreboard.Scoreboard;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.feature.FeatureInfo;
import com.voxelgameslib.voxelgameslib.map.Map;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;

@FeatureInfo(name = "MapInfoFeature", author = "MiniDigger", version = "1.0",
        description = "Displays some information about the current map in the scoreboard of the phase")
public class MapInfoFeature extends AbstractFeature {

    @Override
    public void start() {
        MapFeature mapFeature = getPhase().getFeature(MapFeature.class);
        Map map = mapFeature.getMap();

        ScoreboardFeature scoreboardFeature = getPhase().getFeature(ScoreboardFeature.class);
        Scoreboard scoreboard = scoreboardFeature.getScoreboard();

        for (String mode : map.getInfo().getGamemodes()) {
            scoreboard.createAndAddLine(mode);
        }

        scoreboard.createAndAddLine(ChatColor.YELLOW + "" + ChatColor.BOLD + "Gamemodes: ");
        scoreboard.createAndAddLine(map.getInfo().getAuthor());
        scoreboard.createAndAddLine(ChatColor.YELLOW + "" + ChatColor.BOLD + "Author: ");
        scoreboard.createAndAddLine(map.getInfo().getName());
        scoreboard.createAndAddLine(ChatColor.YELLOW + "" + ChatColor.BOLD + "Map: ");
    }

    @Override
    @Nonnull
    public Class[] getDependencies() {
        return new Class[]{MapFeature.class, ScoreboardFeature.class};
    }
}
