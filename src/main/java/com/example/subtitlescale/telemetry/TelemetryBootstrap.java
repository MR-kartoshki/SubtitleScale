package com.example.subtitlescale.telemetry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;

public final class TelemetryBootstrap {
    private static final long RATE_LIMIT_MS = 86_400_000L;

    private TelemetryBootstrap() {
    }

    public static void initAndMaybeSend() {
        try {
            TelemetryConfig config = TelemetryConfig.loadOrCreate();
            if (!config.enabled) {
                return;
            }

            String endpoint = config.endpoint == null ? "" : config.endpoint.trim();
            if (endpoint.isEmpty()) {
                return;
            }

            long now = System.currentTimeMillis();
            if (now - config.lastSent < RATE_LIMIT_MS) {
                return;
            }

            String minecraftVersion = FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(mod -> mod.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");

            JsonObject payload = new JsonObject();
            payload.addProperty("mc", minecraftVersion);
            payload.addProperty("e", "c");
            payload.addProperty("l", "fabric");

            JsonArray mods = new JsonArray();
            mods.add("subtitlescale");
            payload.add("m", mods);

            if (config.sendClientId) {
                String clientId = config.clientId == null ? "" : config.clientId.trim();
                if (!clientId.isEmpty()) {
                    payload.addProperty("cid", clientId);
                }
            }

            TelemetrySender.send(URI.create(endpoint), payload);

            config.lastSent = now;
            config.save();
        } catch (Exception ignored) {
        }
    }
}
