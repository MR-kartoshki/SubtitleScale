package com.example.subtitlescale.modmenu;

import com.example.subtitlescale.config.SubtitleScaleConfig;
import com.example.subtitlescale.telemetry.TelemetryConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class SubtitleScaleModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SubtitleScaleModMenu::createConfigScreen;
    }

    private static Screen createConfigScreen(Screen parent) {
        TelemetryConfig telemetryConfig = TelemetryConfig.loadOrCreate();

        AtomicReference<SubtitleScaleConfig.Profile> profileValue = new AtomicReference<>(SubtitleScaleConfig.getProfile());
        AtomicInteger scalePercentValue = new AtomicInteger(Math.round(SubtitleScaleConfig.getScale() * 100.0f));
        AtomicInteger xOffsetValue = new AtomicInteger(SubtitleScaleConfig.getOffsetX());
        AtomicInteger yOffsetValue = new AtomicInteger(SubtitleScaleConfig.getOffsetY());
        AtomicBoolean telemetryEnabled = new AtomicBoolean(telemetryConfig.enabled);

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("SubtitleScale"))
            .setSavingRunnable(() -> {
                SubtitleScaleConfig.Profile selectedProfile = profileValue.get();
                SubtitleScaleConfig.setProfile(selectedProfile);
                if (selectedProfile == SubtitleScaleConfig.Profile.DEFAULT) {
                    SubtitleScaleConfig.setScale(scalePercentValue.get() / 100.0f);
                    SubtitleScaleConfig.setOffsetX(xOffsetValue.get());
                    SubtitleScaleConfig.setOffsetY(yOffsetValue.get());
                } else {
                    SubtitleScaleConfig.setScale(selectedProfile.scale());
                    SubtitleScaleConfig.setOffsetX(selectedProfile.offsetX());
                    SubtitleScaleConfig.setOffsetY(selectedProfile.offsetY());
                }
                SubtitleScaleConfig.save();

                telemetryConfig.enabled = telemetryEnabled.get();
                telemetryConfig.save();
            });

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
        ConfigCategory telemetry = builder.getOrCreateCategory(Component.literal("Telemetry"));
        ConfigEntryBuilder entries = builder.entryBuilder();

        general.addEntry(entries
            .startEnumSelector(Component.literal("Profile"), SubtitleScaleConfig.Profile.class, profileValue.get())
            .setDefaultValue(SubtitleScaleConfig.Profile.DEFAULT)
            .setEnumNameProvider(value -> Component.literal(((SubtitleScaleConfig.Profile) value).displayName()))
            .setSaveConsumer(profileValue::set)
            .build());

        general.addEntry(entries
            .startIntSlider(Component.literal("Subtitle Scale"), scalePercentValue.get(), 50, 200)
            .setDefaultValue(125)
            .setTextGetter(value -> Component.literal(String.format(Locale.ROOT, "%.2fx", value / 100.0f)))
            .setSaveConsumer(scalePercentValue::set)
            .build());

        general.addEntry(entries
            .startIntSlider(Component.literal("Subtitle X Offset"), xOffsetValue.get(), -400, 400)
            .setDefaultValue(0)
            .setTextGetter(value -> Component.literal(String.format(Locale.ROOT, "%+d px", value)))
            .setSaveConsumer(xOffsetValue::set)
            .build());

        general.addEntry(entries
            .startIntSlider(Component.literal("Subtitle Y Offset"), yOffsetValue.get(), -400, 400)
            .setDefaultValue(0)
            .setTextGetter(value -> Component.literal(String.format(Locale.ROOT, "%+d px", value)))
            .setSaveConsumer(yOffsetValue::set)
            .build());

        general.addEntry(entries
            .startTextDescription(Component.literal("Quick preset cycle: F3 + and F3 -"))
            .build());

        telemetry.addEntry(entries
            .startBooleanToggle(Component.literal("Enable telemetry"), telemetryConfig.enabled)
            .setDefaultValue(true)
            .setSaveConsumer(telemetryEnabled::set)
            .build());

        return builder.build();
    }
}
