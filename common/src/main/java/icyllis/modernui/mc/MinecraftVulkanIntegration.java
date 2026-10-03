/*
 * Modern UI.
 * Copyright (C) 2019-2026 BloCamLimb. All rights reserved.
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
 */

package icyllis.modernui.mc;

import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.backend.vulkan.VulkanDevice;
import icyllis.arc3d.engine.ContextOptions;
import icyllis.arc3d.vulkan.VulkanBackendContext;
import icyllis.arc3d.vulkan.VulkanMemoryAllocator;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import java.lang.reflect.Field;

@ApiStatus.Internal
public final class MinecraftVulkanIntegration {
    private MinecraftVulkanIntegration() {
    }

    public static boolean isMinecraftVulkan(GpuDevice device) {
        try {
            return backend(device) instanceof VulkanDevice;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return false;
        }
    }

    /**
     * Shares Minecraft's already-created Vulkan instance, device, graphics queue and allocator
     * with Arc3D. Neither Arc3D nor this bridge owns or destroys Minecraft's Vulkan handles.
     */
    public static boolean initialize(GpuDevice device, ContextOptions options) {
        VkPhysicalDeviceFeatures2 features = null;
        try {
            Object backend = backend(device);
            if (!(backend instanceof VulkanDevice vulkan)) {
                throw new IllegalStateException("Minecraft is not using its native Vulkan backend");
            }

            Object physicalDevice = fieldValue(vulkan, "physicalDevice");
            var vkPhysicalDevice = (org.lwjgl.vulkan.VkPhysicalDevice)
                    fieldValue(physicalDevice, "vkPhysicalDevice");

            VulkanBackendContext context = new VulkanBackendContext();
            context.mInstance = vulkan.instance().vkInstance();
            context.mPhysicalDevice = vkPhysicalDevice;
            context.mDevice = vulkan.vkDevice();
            var queue = vulkan.graphicsQueue();
            context.mQueue = queue.vkQueue();
            context.mGraphicsQueueIndex = queue.queueFamilyIndex();

            features = VkPhysicalDeviceFeatures2.calloc().sType$Default();
            VK11.vkGetPhysicalDeviceFeatures2(vkPhysicalDevice, features);
            context.mDeviceFeatures2 = features;
            context.mDeviceFeatures = features.features();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.malloc(stack);
                VK10.vkGetPhysicalDeviceProperties(vkPhysicalDevice, properties);
                context.mMaxAPIVersion = properties.apiVersion();
            }

            context.mMemoryAllocator = new VulkanMemoryAllocator(vulkan.vma(), true);
            return CoreInit.initialize(context, options);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not access Minecraft's Vulkan device handles", e);
        } finally {
            if (features != null) {
                features.free();
            }
        }
    }

    private static Object backend(GpuDevice device) throws ReflectiveOperationException {
        Object value = fieldValue(device, "backend");
        if (value == null) {
            throw new IllegalStateException("Minecraft GPU device has no backend");
        }
        return value;
    }

    private static Object fieldValue(Object instance, String name) throws ReflectiveOperationException {
        for (Class<?> type = instance.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(instance);
            } catch (NoSuchFieldException ignored) {
                // Search the superclass; the public API exposes only the backend interface.
            }
        }
        throw new NoSuchFieldException(instance.getClass().getName() + "." + name);
    }

    private static final class CoreInit {
        private static boolean initialize(VulkanBackendContext context, ContextOptions options) {
            return icyllis.modernui.core.Core.initVulkan(context, options);
        }
    }
}
