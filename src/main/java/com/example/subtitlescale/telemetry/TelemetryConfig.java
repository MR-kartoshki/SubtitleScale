package com.example.subtitlescale.telemetry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class TelemetryConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir()
        .resolve("subtitlescale")
        .resolve("telemetry.json");

    public boolean enabled = true;
    public String endpoint = "https://140.86.211.122.sslip.io/ingest";
    public String clientId = "";
    public long lastSent = 0L;
    public boolean sendClientId = false;

    public static TelemetryConfig loadOrCreate() {
        TelemetryConfig defaults = new TelemetryConfig();

        try {
            createParentDirectory();

            if (!Files.exists(CONFIG_PATH)) {
                ensureClientId(defaults);
                defaults.save();
                return defaults;
            }

            TelemetryConfig loaded;
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                loaded = GSON.fromJson(reader, TelemetryConfig.class);
            }

            TelemetryConfig config = loaded != null ? loaded : defaults;
            if (config.endpoint == null) {
                config.endpoint = "";
            }
            if (config.clientId == null) {
                config.clientId = "";
            }

            if (ensureClientId(config)) {
                config.save();
            }

            return config;
        } catch (Exception ignored) {
            try {
                createParentDirectory();
                ensureClientId(defaults);
                defaults.save();
            } catch (Exception ignoredInner) {
            }
            return defaults;
        }
    }

    public void save() {
        try {
            createParentDirectory();
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception ignored) {
        }
    }

    private static void createParentDirectory() throws Exception {
        Path parent = CONFIG_PATH.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    private static boolean ensureClientId(TelemetryConfig config) {
        if (config.clientId == null || config.clientId.isBlank()) {
            config.clientId = UUID.randomUUID().toString();
            return true;
        }
        return false;
    }
}
