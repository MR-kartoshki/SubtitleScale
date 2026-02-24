package com.example.subtitlescale.modmenu;

import com.example.subtitlescale.config.SubtitleScaleConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public final class SubtitleScaleModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return SubtitleScaleModMenu::createConfigScreen;
    }

    private static Screen createConfigScreen(Screen parent) {
        AtomicReference<Float> scaleValue = new AtomicReference<>(SubtitleScaleConfig.getScale());
        int currentPercent = Math.round(SubtitleScaleConfig.getScale() * 100.0f);

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("SubtitleScale"))
            .setSavingRunnable(() -> {
                SubtitleScaleConfig.setScale(scaleValue.get());
                SubtitleScaleConfig.save();
            });

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
        ConfigEntryBuilder entries = builder.entryBuilder();

        general.addEntry(entries
            .startIntSlider(Component.literal("Subtitle Scale"), currentPercent, 50, 200)
            .setDefaultValue(125)
            .setTextGetter(value -> Component.literal(String.format(Locale.ROOT, "%.2fx", value / 100.0f)))
            .setSaveConsumer(value -> scaleValue.set(value / 100.0f))
            .build());

        return builder.build();
    }
}
