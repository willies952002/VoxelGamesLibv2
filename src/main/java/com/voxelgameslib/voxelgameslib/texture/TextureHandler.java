package com.voxelgameslib.voxelgameslib.texture;

import com.google.gson.Gson;
import com.google.inject.name.Named;

import com.voxelgameslib.voxelgameslib.handler.Handler;
import com.voxelgameslib.voxelgameslib.utils.ItemBuilder;

import org.mineskin.MineskinClient;
import org.mineskin.Model;
import org.mineskin.SkinOptions;
import org.mineskin.Visibility;
import org.mineskin.data.Skin;
import org.mineskin.data.SkinCallback;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.extern.java.Log;

@Log
@Singleton
public class TextureHandler implements Handler {

    private List<Skin> loadedSkins = new ArrayList<>();

    @Inject
    @Named("IgnoreExposedBS")
    private Gson gson;

    @Inject
    @Named("SkinsFolder")
    private File skinsFolder;

    private MineskinClient mineskinClient = new MineskinClient();

    @Override
    public void start() {
        if (!skinsFolder.exists()) {
            log.warning("Skins folder doesn't exit, creating");
            skinsFolder.mkdirs();
        }
        File[] files = skinsFolder.listFiles();
        if (files != null) {
            Arrays.stream(files).map(file -> file.getName().replace(".json", "")).forEach(this::loadSkin);
        }
        log.info("Loaded " + loadedSkins.size() + " skins");
    }

    @Override
    public void stop() {

    }

    @Nonnull
    public Optional<Skin> getSkin(@Nonnull String name) {
        return loadedSkins.stream().filter(skin -> skin.name.equals(name)).findFirst();
    }

    @Nonnull
    public Optional<Skin> getSkin(int id) {
        return loadedSkins.stream().filter(skin -> skin.id == id).findFirst();
    }

    public void fetchSkin(@Nonnull String name, @Nonnull String url, @Nullable SkinCallback skinCallback) {
        // check cache
        Optional<Skin> s = getSkin(name);
        if (s.isPresent()) {
            if (skinCallback != null) {
                skinCallback.done(s.get());
            }
            return;
        }

        // fetch from mineskin
        mineskinClient.generateUrl(url, SkinOptions.create(name, Model.DEFAULT, Visibility.PRIVATE), skin -> {
            loadedSkins.add(skin);
            saveSkin(skin);
            if (skinCallback != null) {
                skinCallback.done(skin);
            }
        });
    }

    public void fetchSkin(int id, @Nullable SkinCallback skinCallback) {
        // check cache
        Optional<Skin> s = getSkin(id);
        if (s.isPresent()) {
            if (skinCallback != null) {
                skinCallback.done(s.get());
            }
            return;
        }

        // fetch from mineskin
        mineskinClient.getSkin(id, skin -> {
            loadedSkins.add(skin);
            saveSkin(skin);
            if (skinCallback != null) {
                skinCallback.done(skin);
            }
        });
    }

    public void saveSkin(@Nonnull Skin skin) {
        try {
            if (skin.name.equals(""))
                throw new IllegalArgumentException("Skin has to have a name!");
            File file = new File(skinsFolder, skin.name + ".json");
            if (file.exists()) file.createNewFile();
            FileWriter fw = new FileWriter(file);
            gson.toJson(skin, fw);
            fw.close();
        } catch (Exception ex) {
            log.log(Level.WARNING, "Error while saving skin " + skin.name, ex);
        }
    }

    @Nullable
    public Skin loadSkin(@Nonnull String name) {
        try {
            FileReader fr = new FileReader(new File(skinsFolder, name + ".json"));
            Skin skin = gson.fromJson(fr, Skin.class);
            fr.close();
            loadedSkins.add(skin);
            return skin;
        } catch (Exception ex) {
            log.log(Level.WARNING, "could not load skin " + name, ex);
        }

        return null;
    }

    @Nullable
    public ItemStack getSkull(@Nonnull Skin skin) {
        return new ItemBuilder(Material.SKULL_ITEM).durability(3).name(skin.name).meta((itemMeta -> {
            try {
                HeadTextureChanger.applyTextureToMeta(itemMeta,
                        HeadTextureChanger.createProfile(skin.data.texture.value, skin.data.texture.signature));
            } catch (Exception e) {
                e.printStackTrace();
            }
        })).build();
    }
}
