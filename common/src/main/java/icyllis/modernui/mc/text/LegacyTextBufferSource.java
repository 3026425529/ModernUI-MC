package icyllis.modernui.mc.text;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Compatibility bridge for legacy text drawing helpers. Minecraft 26.3 uses
 * prepared GUI render states for active text rendering.
 */
@FunctionalInterface
public interface LegacyTextBufferSource {
    VertexConsumer getBuffer(RenderType renderType);
}
