package com.ghozix.idlegenerators.client;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.generator.GeneratorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

/**
 * Réplica de animation.generator_entity.rotate de Bedrock: el núcleo de 6x6x6
 * gira -360° cada 2 s y flota ±1 px. El marco es el modelo estático
 * (generator_frame); este renderer solo dibuja el núcleo.
 */
@Environment(EnvType.CLIENT)
public class GeneratorCoreRenderer implements BlockEntityRenderer<GeneratorBlockEntity> {
    private static final int LOOP_TICKS = 40; // 2 s, como la animación Bedrock

    private final ModelPart core;
    private final Map<String, Material> materials = new HashMap<>();

    public GeneratorCoreRenderer() {
        MeshDefinition mesh = new MeshDefinition();
        // Mismo box-UV que el geo de Bedrock: texOffs(35,11), cubo 6x6x6 centrado
        // en el origen para poder rotarlo sobre su centro.
        mesh.getRoot().addOrReplaceChild("core",
                CubeListBuilder.create().texOffs(35, 11).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.ZERO);
        this.core = LayerDefinition.create(mesh, 64, 64).bakeRoot().getChild("core");
    }

    @Override
    public void render(GeneratorBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (be.getLevel() == null) return;
        float t = (be.getLevel().getGameTime() % LOOP_TICKS) + partialTick;
        float angle = -t * (360.0F / LOOP_TICKS);                       // -360° / 2 s
        float bob = Mth.sin(t * Mth.TWO_PI / LOOP_TICKS) / 16.0F;       // ±1 px

        Material material = materials.computeIfAbsent(be.type().blockId(), id ->
                new Material(TextureAtlas.LOCATION_BLOCKS,
                        ResourceLocation.fromNamespaceAndPath(IdleGenerators.MOD_ID, "block/" + id)));
        float[] shape = GeneratorCoreShapes.get(be.type().blockId());

        poseStack.pushPose();
        if (shape != null) {
            // Núcleo 3D (generator_entity_<key> de Bedrock): cajas en píxeles del bloque, gira sobre el eje vertical del bloque
            poseStack.translate(0.5F, bob, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            emitBoxes(poseStack.last(), bufferSource.getBuffer(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS)),
                    shape, material.sprite(), packedLight, packedOverlay);
        } else {
            VertexConsumer buffer = material.buffer(bufferSource, RenderType::entityCutout);
            poseStack.translate(0.5F, 7.0F / 16.0F + bob, 0.5F); // centro del núcleo (y 4..10)
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            core.render(poseStack, buffer, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }

    /**
     * Emits the boxes of a {@link GeneratorCoreShapes} table. UVs follow the item model:
     * side faces read u along +X (north/south) or +Z (east/west) and v top to bottom,
     * up/down read u along +X and v along +Z.
     */
    static void emitBoxes(PoseStack.Pose pose, VertexConsumer buffer, float[] boxes,
                          TextureAtlasSprite sprite, int light, int overlay) {
        for (int i = 0; i < boxes.length; i += GeneratorCoreShapes.STRIDE) {
            float x0 = boxes[i] / 16F, y0 = boxes[i + 1] / 16F, z0 = boxes[i + 2] / 16F;
            float x1 = boxes[i + 3] / 16F, y1 = boxes[i + 4] / 16F, z1 = boxes[i + 5] / 16F;
            // north (-Z)
            face(pose, buffer, sprite, light, overlay, boxes, i + 6, 0, 0, -1,
                    x0, y0, z0, 0, 1, x0, y1, z0, 0, 0, x1, y1, z0, 1, 0, x1, y0, z0, 1, 1);
            // south (+Z)
            face(pose, buffer, sprite, light, overlay, boxes, i + 10, 0, 0, 1,
                    x0, y0, z1, 0, 1, x1, y0, z1, 1, 1, x1, y1, z1, 1, 0, x0, y1, z1, 0, 0);
            // east (+X)
            face(pose, buffer, sprite, light, overlay, boxes, i + 14, 1, 0, 0,
                    x1, y0, z0, 0, 1, x1, y1, z0, 0, 0, x1, y1, z1, 1, 0, x1, y0, z1, 1, 1);
            // west (-X)
            face(pose, buffer, sprite, light, overlay, boxes, i + 18, -1, 0, 0,
                    x0, y0, z0, 0, 1, x0, y0, z1, 1, 1, x0, y1, z1, 1, 0, x0, y1, z0, 0, 0);
            // up (+Y)
            face(pose, buffer, sprite, light, overlay, boxes, i + 22, 0, 1, 0,
                    x0, y1, z0, 0, 0, x0, y1, z1, 0, 1, x1, y1, z1, 1, 1, x1, y1, z0, 1, 0);
            // down (-Y)
            face(pose, buffer, sprite, light, overlay, boxes, i + 26, 0, -1, 0,
                    x0, y0, z0, 0, 0, x1, y0, z0, 1, 0, x1, y0, z1, 1, 1, x0, y0, z1, 0, 1);
        }
    }

    /** One quad, corners counter-clockwise from outside; (s, t) in 0..1 across the face's UV rectangle. */
    private static void face(PoseStack.Pose pose, VertexConsumer buffer, TextureAtlasSprite sprite, int light, int overlay,
                             float[] boxes, int uv, float nx, float ny, float nz,
                             float ax, float ay, float az, float as, float at,
                             float bx, float by, float bz, float bs, float bt,
                             float cx, float cy, float cz, float cs, float ct,
                             float dx, float dy, float dz, float ds, float dt) {
        float u = boxes[uv], v = boxes[uv + 1], w = boxes[uv + 2], h = boxes[uv + 3];
        vertex(pose, buffer, sprite, light, overlay, ax, ay, az, u + w * as, v + h * at, nx, ny, nz);
        vertex(pose, buffer, sprite, light, overlay, bx, by, bz, u + w * bs, v + h * bt, nx, ny, nz);
        vertex(pose, buffer, sprite, light, overlay, cx, cy, cz, u + w * cs, v + h * ct, nx, ny, nz);
        vertex(pose, buffer, sprite, light, overlay, dx, dy, dz, u + w * ds, v + h * dt, nx, ny, nz);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, TextureAtlasSprite sprite, int light, int overlay,
                               float x, float y, float z, float px, float py, float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(sprite.getU(px / 64F), sprite.getV(py / 64F))
                .setOverlay(overlay)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
