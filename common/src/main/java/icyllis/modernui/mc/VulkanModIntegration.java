package icyllis.modernui.mc;

import com.mojang.renderpearl.api.textures.GpuTexture;
import icyllis.arc3d.vulkan.VulkanImage;
import icyllis.arc3d.vulkan.VulkanBackendContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class VulkanModIntegration {
    private VulkanModIntegration() {}
    public static VulkanBackendContext wrapContext() { return null; }
    public static void replaceMainImageViewWithSwizzle(Object textureView, short swizzle) {}
    public static GpuTexture wrapTextureImageFromArc3D(VulkanImage image) { return null; }
    public static void syncImageLayoutFromArc3D(GpuTexture texture, VulkanImage image) {}
    public static void syncImageLayoutFromVulkan(GpuTexture texture, VulkanImage image) {}
    public static boolean sameImage(GpuTexture texture, VulkanImage image) { return false; }
    public static void addFrameOp(Runnable runnable) {}
}
