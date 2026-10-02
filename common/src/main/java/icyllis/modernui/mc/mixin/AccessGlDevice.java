package icyllis.modernui.mc.mixin;

import com.mojang.renderpearl.backend.opengl.FrameBufferCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "com.mojang.renderpearl.backend.opengl.GlDevice")
public interface AccessGlDevice {
    @Accessor("frameBufferCache")
    FrameBufferCache modernui$getFrameBufferCache();
}
