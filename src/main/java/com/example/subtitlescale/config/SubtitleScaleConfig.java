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
import java.util.Locale;

public final class SubtitleScaleConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger("SubtitleScaleConfig");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;
    private static final float DEFAULT_SCALE = 1.25f;
    private static final int MIN_OFFSET = -400;
    private static final int MAX_OFFSET = 400;
    private static final int DEFAULT_OFFSET_X = 0;
    private static final int DEFAULT_OFFSET_Y = 0;
    private static final float[] QUICK_SCALE_PRESETS = {1.0f, 1.25f, 1.5f};
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("subtitlescale");
    private static final Path CONFIG_PATH = CONFIG_DIR.resolve("config.json");
    private static final Path LEGACY_CONFIG_PATH = CONFIG_DIR.resolve("telemetry.json");

    private static float scale = DEFAULT_SCALE;
    private static int offsetX = DEFAULT_OFFSET_X;
    private static int offsetY = DEFAULT_OFFSET_Y;
    private static Profile profile = Profile.DEFAULT;

    private SubtitleScaleConfig() {
    }

    public static float getScale() {
        return scale;
    }

    public static void setScale(float value) {
        scale = clamp(value);
    }

    public static int getOffsetX() {
        return offsetX;
    }

    public static void setOffsetX(int value) {
        offsetX = clampOffset(value);
    }

    public static int getOffsetY() {
        return offsetY;
    }

    public static void setOffsetY(int value) {
        offsetY = clampOffset(value);
    }

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile value) {
        profile = value == null ? Profile.DEFAULT : value;
    }

    public static float cycleQuickScalePreset(int direction) {
        int currentIndex = findClosestQuickPresetIndex(scale);
        int nextIndex = Math.floorMod(currentIndex + direction, QUICK_SCALE_PRESETS.length);
        setScale(QUICK_SCALE_PRESETS[nextIndex]);
        return scale;
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            ConfigData data = new ConfigData();
            data.scale = scale;
            data.offsetX = offsetX;
            data.offsetY = offsetY;
            data.profile = profile.toConfigValue();
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
                resetToDefaults();
                save();
                return;
            }

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data == null) {
                    resetToDefaults();
                } else {
                    load(data);
                }
            } catch (JsonParseException e) {
                LOGGER.warn("Invalid SubtitleScale config JSON, resetting to defaults.", e);
                resetToDefaults();
                save();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load SubtitleScale config at {}", CONFIG_PATH, e);
            resetToDefaults();
        }
    }

    public static void deleteLegacyConfigFile() {
        try {
            Files.deleteIfExists(LEGACY_CONFIG_PATH);
        } catch (IOException e) {
            LOGGER.warn("Failed to delete legacy config file at {}", LEGACY_CONFIG_PATH, e);
        }
    }

    private static void load(ConfigData data) {
        scale = clamp(data.scale);
        offsetX = clampOffset(data.offsetX);
        offsetY = clampOffset(data.offsetY);
        profile = Profile.fromConfigValue(data.profile);
    }

    private static void resetToDefaults() {
        scale = DEFAULT_SCALE;
        offsetX = DEFAULT_OFFSET_X;
        offsetY = DEFAULT_OFFSET_Y;
        profile = Profile.DEFAULT;
    }

    private static int findClosestQuickPresetIndex(float value) {
        int index = 0;
        float distance = Float.MAX_VALUE;
        for (int i = 0; i < QUICK_SCALE_PRESETS.length; i++) {
            float currentDistance = Math.abs(QUICK_SCALE_PRESETS[i] - value);
            if (currentDistance < distance) {
                distance = currentDistance;
                index = i;
            }
        }
        return index;
    }

    private static float clamp(float value) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, value));
    }

    private static int clampOffset(int value) {
        return Math.max(MIN_OFFSET, Math.min(MAX_OFFSET, value));
    }

    private static final class ConfigData {
        float scale = DEFAULT_SCALE;
        int offsetX = DEFAULT_OFFSET_X;
        int offsetY = DEFAULT_OFFSET_Y;
        String profile = Profile.DEFAULT.toConfigValue();
    }

    public enum Profile {
        DEFAULT("Default", 1.25f, 0, 0),
        CINEMATIC("Cinematic", 1.0f, -120, -36),
        ACCESSIBILITY_LARGE("Accessibility Large", 1.5f, 0, -12);

        private final String displayName;
        private final float scale;
        private final int offsetX;
        private final int offsetY;

        Profile(String displayName, float scale, int offsetX, int offsetY) {
            this.displayName = displayName;
            this.scale = scale;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public String displayName() {
            return displayName;
        }

        public float scale() {
            return scale;
        }

        public int offsetX() {
            return offsetX;
        }

        public int offsetY() {
            return offsetY;
        }

        public String toConfigValue() {
            return name();
        }

        public static Profile fromConfigValue(String value) {
            if (value == null || value.isBlank()) {
                return DEFAULT;
            }

            try {
                return valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                return DEFAULT;
            }
        }
    }
}
