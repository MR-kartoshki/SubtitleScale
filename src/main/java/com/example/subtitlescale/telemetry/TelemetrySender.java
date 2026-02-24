package com.example.subtitlescale.telemetry;

import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class TelemetrySender {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .build();

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private static final String USER_AGENT = "SubtitleScale/telemetry";

    private TelemetrySender() {
    }

    public static void send(final URI telemetryEndpoint, final JsonObject payload) {
        try {
            if (telemetryEndpoint == null || payload == null) {
                return;
            }

            HttpRequest request = HttpRequest.newBuilder(telemetryEndpoint)
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("User-Agent", USER_AGENT)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();

            HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .handle((response, throwable) -> null);
        } catch (Exception ignored) {
        }
    }
}
