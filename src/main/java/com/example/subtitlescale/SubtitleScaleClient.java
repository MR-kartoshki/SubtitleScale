package com.example.subtitlescale;

import com.example.subtitlescale.config.SubtitleScaleConfig;
import com.example.subtitlescale.telemetry.TelemetryBootstrap;
import net.fabricmc.api.ClientModInitializer;

public final class SubtitleScaleClient implements ClientModInitializer {
    public static final String MOD_ID = "subtitlescale";

    @Override
    public void onInitializeClient() {
        SubtitleScaleConfig.loadOrCreate();
        TelemetryBootstrap.initAndMaybeSend();
    }
}
