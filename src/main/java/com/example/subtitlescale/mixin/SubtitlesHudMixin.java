package com.example.subtitlescale.mixin;

import com.example.subtitlescale.config.SubtitleScaleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SubtitleOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubtitleOverlay.class)
public class SubtitlesHudMixin {
    @Unique
    private boolean subtitlescale$pushed;

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"))
    private void subtitlescale$pushSubtitleGroupScale(GuiGraphics context, CallbackInfo ci) {
        float scale = SubtitleScaleConfig.getScale();
        int offsetX = SubtitleScaleConfig.getOffsetX();
        int offsetY = SubtitleScaleConfig.getOffsetY();
        if (scale == 1.0f && offsetX == 0 && offsetY == 0) {
            return;
        }

        context.pose().pushMatrix();
        if (scale != 1.0f) {
            Minecraft client = Minecraft.getInstance();
            int w = client.getWindow().getGuiScaledWidth();
            int h = client.getWindow().getGuiScaledHeight();

            context.pose().translate(w, h);
            context.pose().scale(scale, scale);
            context.pose().translate(-w, -h);
        }

        if (offsetX != 0 || offsetY != 0) {
            context.pose().translate(offsetX, offsetY);
        }

        subtitlescale$pushed = true;
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("RETURN"))
    private void subtitlescale$popSubtitleGroupScale(GuiGraphics context, CallbackInfo ci) {
        if (!subtitlescale$pushed) {
            return;
        }

        context.pose().popMatrix();
        subtitlescale$pushed = false;
    }
}
