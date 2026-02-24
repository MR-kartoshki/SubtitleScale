package com.example.subtitlescale.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SubtitleScaleConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("SubtitleScaleConfig");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;
    private static final float DEFAULT_SCALE = 1.25f;
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("subtitlescale");
    private static final Path CONFIG_PATH = CONFIG_DIR.resolve("config.json");

    private static float scale = DEFAULT_SCALE;

    private SubtitleScaleConfig() {
    }

    public static float getScale() {
        return scale;
    }

    public static void setScale(float value) {
        scale = clamp(value);
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            ConfigData data = new ConfigData();
            data.scale = scale;
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save SubtitleScale config at {}", CONFIG_PATH, e);
        }
    }

    public static void loadOrCreate() {
        try {
            Files.createDirectories(CONFIG_DIR);

            if (!Files.exists(CONFIG_PATH)) {
                scale = DEFAULT_SCALE;
                save();
                return;
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data == null) {
                    scale = DEFAULT_SCALE;
                } else {
                    scale = clamp(data.scale);
                }
            } catch (JsonParseException e) {
                LOGGER.warn("Invalid SubtitleScale config JSON, resetting to defaults.", e);
                scale = DEFAULT_SCALE;
                save();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load SubtitleScale config at {}", CONFIG_PATH, e);
            scale = DEFAULT_SCALE;
        }
    }

    private static float clamp(float value) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, value));
    }

    private static final class ConfigData {
        float scale = DEFAULT_SCALE;
    }
}
