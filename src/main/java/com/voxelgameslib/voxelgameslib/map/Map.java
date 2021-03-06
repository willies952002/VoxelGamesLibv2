package com.voxelgameslib.voxelgameslib.map;

import com.google.gson.annotations.Expose;

import com.voxelgameslib.voxelgameslib.exception.VoxelGameLibException;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.lang.LangKey;
import com.voxelgameslib.voxelgameslib.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import lombok.Data;
import lombok.extern.java.Log;

/**
 * A map. A map is a world that is playable in gamemodes. has all kind of extra informations about a
 * world.
 */
@Log
@Data
public class Map {

    @Expose
    private MapInfo info;
    @Expose
    private Vector3D center;
    @Expose
    private int radius;
    @Expose
    private String worldName;
    @Expose
    private List<Marker> markers = new ArrayList<>();
    @Expose
    private List<ChestMarker> chestMarkers = new ArrayList<>();

    private HashMap<UUID, String> loadedNames = new HashMap<>();

    /**
     * @param mapInfo   the map info for this map
     * @param worldName the name of the world
     * @param center    the center of this map
     * @param radius    the radius of this map
     */
    public Map(@Nonnull MapInfo mapInfo, @Nonnull String worldName, @Nonnull Vector3D center,
               int radius) {
        this.info = mapInfo;
        this.worldName = worldName;
        this.center = center;
        this.radius = radius;
        loadedNames = new HashMap<>();
    }

    /**
     * Tries to parse all marker data into the definitions
     *
     * @param mapHandler the maphandler that provides the marker definitions
     */
    public void initMarkers(@Nonnull MapHandler mapHandler) {
        markers.forEach(marker -> marker.setMarkerDefinition(mapHandler.createMarkerDefinition(marker.getData())));
    }

    /**
     * Gets a chestmarker that have the name as data
     *
     * @param name the name to search for
     * @return the optically found chestmarker
     */
    @Nonnull
    public Optional<ChestMarker> getChestMarker(@Nonnull String name) {
        return chestMarkers.stream().filter(chestMarker -> chestMarker.getData().equalsIgnoreCase(name))
                .findAny();
    }

    /**
     * Prints a nice summary of this map
     *
     * @param sender the user to send the summary to
     */
    public void printSummary(@Nonnull User sender) {
        Lang.msg(sender, LangKey.WORLD_CREATOR_MAP_SUMMARY, info.getName(), worldName, info.getAuthor(),
                center, radius, info.getGamemodes());
        //TODO print summery of map
    }

    @Nonnull
    public String getLoadedName(@Nonnull UUID gameid) {
        if (loadedNames == null) {
            loadedNames = new HashMap<>();
        }
        return Optional.ofNullable(loadedNames.get(gameid))
                .orElseThrow(() -> new VoxelGameLibException("Map " + worldName + " was never loaded for game " + gameid));
    }

    public void load(@Nonnull UUID gameid, @Nonnull String name) {
        if (loadedNames == null) {
            loadedNames = new HashMap<>();
        }
        if (loadedNames.put(gameid, name) != null) {
            log.warning("Loaded the same map for the same game instance twice?");
        }
    }

    public boolean isLoaded(@Nonnull UUID gameid) {
        log.finer("Is  " + info.getName() + " loaded?");
        if (loadedNames == null) {
            log.finer("Loaded names = null");
            loadedNames = new HashMap<>();
        }
        return loadedNames.containsKey(gameid);
    }

    public void unload(@Nonnull UUID gameid) {
        if (loadedNames == null) {
            loadedNames = new HashMap<>();
        }
        loadedNames.remove(gameid);
    }

    @Nonnull
    public List<Marker> getMarkers(@Nonnull MarkerDefinition spawnMarker) {
        return markers.stream().filter(marker -> spawnMarker.isOfSameType(marker.getMarkerDefinition())).collect(Collectors.toList());
    }
}
