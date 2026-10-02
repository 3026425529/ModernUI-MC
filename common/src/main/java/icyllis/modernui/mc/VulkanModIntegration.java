package icyllis.modernui.mc;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class VulkanModIntegration {
    private VulkanModIntegration() {}
    public static Object wrapContext() { return null; }
    public static void replaceMainImageViewWithSwizzle(Object textureView, short swizzle) {}
    public static Object wrapTextureImageFromArc3D(Object image) { return null; }
    public static void syncImageLayoutFromArc3D(Object texture, Object image) {}
    public static void syncImageLayoutFromVulkan(Object texture, Object image) {}
    public static boolean sameImage(Object texture, Object image) { return false; }
    public static void addFrameOp(Runnable runnable) {}
}
