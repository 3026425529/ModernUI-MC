/*
 * Modern UI.
 * Copyright (C) 2019-2022 BloCamLimb. All rights reserved.
 *
 * Modern UI is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Modern UI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Modern UI. If not, see <https://www.gnu.org/licenses/>.
 */

package icyllis.modernui.mc.text.mixin;

import icyllis.modernui.mc.text.ModernPreparedText;
import icyllis.modernui.mc.text.ModernTextRenderer;
import icyllis.modernui.mc.text.TextLayout;
import icyllis.modernui.mc.text.TextLayoutEngine;
import icyllis.modernui.mc.text.TextRenderType;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public abstract class MixinFontRenderer {

    @Redirect(method = "<init>", at = @At(value = "NEW",
            target = "(Lnet/minecraft/client/StringSplitter$WidthProvider;)Lnet/minecraft/client/StringSplitter;"))
    private StringSplitter modernUI$createSplitter(StringSplitter.WidthProvider widthProvider) {
        return new ModernStringSplitter(TextLayoutEngine.getInstance(), widthProvider);
    }

    @Inject(
            method = "prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modernUI$prepareFormattedText(FormattedCharSequence text, float x, float y, int color,
                                              boolean dropShadow, boolean includeEmpty, int backgroundColor,
                                              CallbackInfoReturnable<Font.PreparedText> cir) {
        if (TextLayoutEngine.sCurrentInWorldRendering) {
            return;
        }

        TextLayout layout = TextLayoutEngine.getInstance().lookupFormattedLayout(text);
        cir.setReturnValue(prepareModernText(layout, x, y, color, dropShadow, backgroundColor));
    }

    @Inject(
            method = "prepareText(Ljava/lang/String;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void modernUI$prepareString(String text, float x, float y, int color,
                                        boolean dropShadow, int backgroundColor,
                                        CallbackInfoReturnable<Font.PreparedText> cir) {
        if (TextLayoutEngine.sCurrentInWorldRendering) {
            return;
        }

        TextLayout layout = TextLayoutEngine.getInstance().lookupVanillaLayout(text);
        cir.setReturnValue(prepareModernText(layout, x, y, color, dropShadow, backgroundColor));
    }

    private static ModernPreparedText prepareModernText(TextLayout layout, float x, float y, int color,
                                                        boolean dropShadow, int backgroundColor) {
        int mode = ModernTextRenderer.sAllowSDFTextIn2D
                ? TextRenderType.MODE_SDF_FILL
                : TextRenderType.MODE_NORMAL;
        return layout.prepareTextWithDensity(
                x, y, color, dropShadow, mode, 1.0f, backgroundColor, 0.0f, 0.0f);
    }
}
