package icyllis.modernui.mc.mixin;

import com.mojang.renderpearl.backend.api.GpuDeviceBackend;
import com.mojang.renderpearl.frontend.FrontendGpuDevice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FrontendGpuDevice.class)
public interface AccessFrontendGpuDevice {
    @Accessor("backend")
    GpuDeviceBackend modernui$getBackend();
}
