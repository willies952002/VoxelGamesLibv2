package com.voxelgameslib.voxelgameslib.feature.features;

import com.google.gson.annotations.Expose;

import com.voxelgameslib.voxelgameslib.components.scoreboard.Scoreboard;
import com.voxelgameslib.voxelgameslib.event.GameEvent;
import com.voxelgameslib.voxelgameslib.event.events.game.GameJoinEvent;
import com.voxelgameslib.voxelgameslib.event.events.game.GameLeaveEvent;
import com.voxelgameslib.voxelgameslib.feature.AbstractFeature;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.lang.LangKey;

import javax.annotation.Nonnull;

import org.bukkit.boss.BossBar;

import lombok.extern.java.Log;

/**
 * Small feature that handles stuff related to the lobby phase
 */
@Log
public class LobbyFeature extends AbstractFeature {

    private Scoreboard scoreboard;
    private boolean starting = false;
    @Expose
    private int startDelay = 120 * 20; // long start delay
    @Expose
    private int fastStartDelay = 15 * 20; // short start delay
    private double curr;
    private BossBar bossBar;

    public LobbyFeature() {
        log.finer("creating lobby feature with starting " + starting + " curr " + curr);
    }

    @Override
    public void start() {
        curr = startDelay;
        log.finer("Starting lobby feature with starting " + starting + " curr " + curr);
        // bossbar
        bossBar = getPhase().getFeature(BossBarFeature.class).getBossBar();
        bossBar.setVisible(false);

        // scoreboard
        scoreboard = getPhase().getFeature(ScoreboardFeature.class).getScoreboard();

        scoreboard.createAndAddLine("lobby-line",
                getPhase().getGame().getPlayers().size() + "/" + getPhase().getGame().getMaxPlayers());
        scoreboard.createAndAddLine("Waiting for players...");
    }

    @Override
    public void tick() {
        if (starting) {
            curr--;
            if (curr <= 0) {
                log.finer("Timer over, ending phase");
                getPhase().getGame().endPhase();
                return;
            }

            bossBar.setProgress((curr / startDelay));
        }
    }

    @Override
    @Nonnull
    public Class[] getDependencies() {
        return new Class[]{ScoreboardFeature.class, BossBarFeature.class};
    }

    @GameEvent
    public void onJoin(@Nonnull GameJoinEvent event) {
        scoreboard.getLine("lobby-line").ifPresent(line -> line.setValue(
                getPhase().getGame().getPlayers().size() + "/" + getPhase().getGame().getMaxPlayers()));

        if (getPhase().getGame().getPlayers().size() >= getPhase().getGame().getMinPlayers()) {
            if (!starting) {
                starting = true;
                curr = startDelay;
                //TODO also update scoreboard
                getPhase().getGame().broadcastMessage(LangKey.GAME_STARTING);
                bossBar.setTitle(Lang.parseLegacyFormat(Lang.string(LangKey.GAME_STARTING)));
                bossBar.setVisible(true);
            }

            if (starting && getPhase().getGame().getPlayers().size() == getPhase().getGame().getMaxPlayers()) {
                if (curr > fastStartDelay) {
                    curr = fastStartDelay;
                }

                getPhase().getGame().broadcastMessage(LangKey.GAME_STARTING_ACCELERATED);
            }
        }
    }

    @GameEvent
    public void onLeave(@Nonnull GameLeaveEvent event) {
        scoreboard.getLine("lobby-line").ifPresent(line -> line.setValue(
                getPhase().getGame().getPlayers().size() + "/" + getPhase().getGame().getMaxPlayers()));
        if (getPhase().getGame().getPlayers().size() <= getPhase().getGame().getMinPlayers()
                && starting) {
            starting = false;
            // TODO also update scoreboard
            getPhase().getGame().broadcastMessage(LangKey.GAME_START_ABORTED);
            bossBar.setTitle("");
            bossBar.setVisible(false);
        }
    }
}
