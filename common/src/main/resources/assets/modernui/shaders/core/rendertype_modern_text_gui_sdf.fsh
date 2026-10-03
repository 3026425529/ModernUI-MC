#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:dynamictransforms.glsl>

uniform sampler2D Sampler0;

layout(location = 2) in vec4 vertexColor;
layout(location = 3) in vec2 texCoord0;
layout(location = 0) out vec4 fragColor;

void main() {
    vec4 texColor = textureLod(Sampler0, texCoord0, 0.0);
    float distance = texColor.a - 127.0 / 255.0 + 0.04;
    texColor.a = clamp(distance / fwidth(distance) + 0.5, 0.0, 1.0);

    vec4 color = texColor * vertexColor * ColorModulator;
    if (color.a < 0.01) discard;
    fragColor = color;
}
