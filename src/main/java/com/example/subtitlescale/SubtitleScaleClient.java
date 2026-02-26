package com.example.subtitlescale;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.example.subtitlescale.config.SubtitleScaleConfig;
import com.example.subtitlescale.telemetry.TelemetryBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public final class SubtitleScaleClient implements ClientModInitializer {
    public static final String MOD_ID = "subtitlescale";
    private static boolean plusComboDownLastTick;
    private static boolean minusComboDownLastTick;

    @Override
    public void onInitializeClient() {
        SubtitleScaleConfig.loadOrCreate();
        registerPresetCycleHotkeys();
        TelemetryBootstrap.initAndMaybeSend();
    }

    private static void registerPresetCycleHotkeys() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                plusComboDownLastTick = false;
                minusComboDownLastTick = false;
                return;
            }

            Window window = client.getWindow();
            boolean f3Down = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_F3);
            boolean plusComboDown = f3Down && (
                InputConstants.isKeyDown(window, GLFW.GLFW_KEY_EQUAL)
                    || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_KP_ADD)
            );
            boolean minusComboDown = f3Down && (
                InputConstants.isKeyDown(window, GLFW.GLFW_KEY_MINUS)
                    || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_KP_SUBTRACT)
            );

            if (client.screen == null) {
                if (plusComboDown && !plusComboDownLastTick) {
                    cyclePreset(client, 1);
                }
                if (minusComboDown && !minusComboDownLastTick) {
                    cyclePreset(client, -1);
                }
            }

            plusComboDownLastTick = plusComboDown;
            minusComboDownLastTick = minusComboDown;
        });
    }

    private static void cyclePreset(Minecraft client, int direction) {
        float newScale = SubtitleScaleConfig.cycleQuickScalePreset(direction);
        SubtitleScaleConfig.save();
        client.gui.setOverlayMessage(
            Component.literal(String.format(Locale.ROOT, "Subtitle scale preset: %.2fx", newScale)),
            false
        );
    }
}
