package com.example.subtitlescale.telemetry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.net.URI;

public final class TelemetryBootstrap {
    private static final long RATE_LIMIT_MS = 86_400_000L;
    private static final String LOG_PREFIX = "SubtitleScale: Telemetry";

    private TelemetryBootstrap() {
    }

    public static void initAndMaybeSend() {
        try {
            TelemetryConfig config = TelemetryConfig.loadOrCreate();
            if (!config.enabled) {
                log("not sent (disabled)");
                return;
            }

            long now = System.currentTimeMillis();
            if (now - config.lastSent < RATE_LIMIT_MS) {
                log("not sent (rate limited)");
                return;
            }

            String minecraftVersion = FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(mod -> mod.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
            log("detected Minecraft version " + minecraftVersion);

            JsonObject payload = new JsonObject();
            payload.addProperty("mc", minecraftVersion);
            payload.addProperty("e", "c");
            payload.addProperty("l", "fabric");

            JsonArray mods = new JsonArray();
            mods.add("subtitlescale");
            payload.add("m", mods);

            if (TelemetryConfig.SEND_CLIENT_ID) {
                String clientId = config.clientId == null ? "" : config.clientId.trim();
                if (!clientId.isEmpty()) {
                    payload.addProperty("cid", clientId);
                }
            }

            TelemetrySender.send(URI.create(TelemetryConfig.ENDPOINT), payload);

            config.lastSent = now;
            config.save();
            log("send scheduled");
        } catch (Exception ignored) {
            log("not sent (bootstrap failure)");
        }
    }

    private static void log(String message) {
        System.out.println(LOG_PREFIX + " " + message);
    }
}
