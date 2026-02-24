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
    private static final String LOG_PREFIX = "SubtitleScale: Telemetry";

    private TelemetrySender() {
    }

    public static void send(final URI telemetryEndpoint, final JsonObject payload) {
        try {
            if (telemetryEndpoint == null || payload == null) {
                log("not sent (invalid request data)");
                return;
            }

            HttpRequest request = HttpRequest.newBuilder(telemetryEndpoint)
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("User-Agent", USER_AGENT)
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                .build();

            HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .whenComplete((response, throwable) -> {
                    try {
                        if (throwable != null) {
                            String message = throwable.getMessage();
                            log("not sent (" + (message == null ? "request failed" : message) + ")");
                            return;
                        }

                        if (response == null) {
                            log("not sent (empty response)");
                            return;
                        }

                        int statusCode = response.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            log("sent (status " + statusCode + ")");
                        } else {
                            log("not sent (status " + statusCode + ")");
                        }
                    } catch (Exception ignored) {
                    }
                });
        } catch (Exception ignored) {
            log("not sent (request build failed)");
        }
    }

    private static void log(String message) {
        System.out.println(LOG_PREFIX + " " + message);
    }
}
