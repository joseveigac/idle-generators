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
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Replica of animation.generator_entity.rotate from Bedrock: the 6x6x6 core
 * rotates -360 deg every 2 s and bobs ±1 px. The frame is the static model
 * (generator_frame); this renderer draws only the core.
 */
@Environment(EnvType.CLIENT)
public class GeneratorCoreRenderer implements BlockEntityRenderer<GeneratorBlockEntity, GeneratorCoreRenderer.State> {
    private static final int LOOP_TICKS = 40; // 2 s, matching Bedrock animation

    private final ModelPart core;
    private final SpriteGetter sprites;
    private final Map<String, TextureAtlasSprite> spriteCache = new HashMap<>();

    public static class State extends BlockEntityRenderState {
        public long gameTime;
        public float partialTick;
        public TextureAtlasSprite sprite;
        public float[] shape;
    }

    public GeneratorCoreRenderer(BlockEntityRendererProvider.Context ctx) {
        this.sprites = ctx.sprites();
        MeshDefinition mesh = new MeshDefinition();
        // Same box-UV as the Bedrock geo: texOffs(35,11), 6x6x6 cube centred at origin
        mesh.getRoot().addOrReplaceChild("core",
                CubeListBuilder.create().texOffs(35, 11).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.ZERO);
        this.core = LayerDefinition.create(mesh, 64, 64).bakeRoot().getChild("core");
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(GeneratorBlockEntity be, State state, float partialTick,
                                   Vec3 offset, ModelFeatureRenderer.CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTick, offset, crumbling);
        if (be.getLevel() == null) return;
        state.gameTime = be.getLevel().getGameTime();
        state.partialTick = partialTick;
        state.sprite = spriteCache.computeIfAbsent(be.type().blockId(), id -> sprites.get(
                new SpriteId(TextureAtlas.LOCATION_BLOCKS,
                        Identifier.fromNamespaceAndPath(IdleGenerators.MOD_ID, "block/" + id))));
        state.shape = GeneratorCoreShapes.get(be.type().blockId());
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.sprite == null) return;
        float t = (state.gameTime % LOOP_TICKS) + state.partialTick;
        float angle = -t * (360.0F / LOOP_TICKS);
        float bob = Mth.sin(t * Mth.TWO_PI / LOOP_TICKS) / 16.0F;

        poseStack.pushPose();
        if (state.shape != null) {
            // 3D core (Bedrock generator_entity_<key>): boxes are in block pixels, rotate about the block's vertical axis
            float[] shape = state.shape;
            TextureAtlasSprite sprite = state.sprite;
            int light = state.lightCoords;
            poseStack.translate(0.5F, bob, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS),
                    (pose, buffer) -> emitBoxes(pose, buffer, shape, sprite, light, OverlayTexture.NO_OVERLAY));
        } else {
            poseStack.translate(0.5F, 7.0F / 16.0F + bob, 0.5F); // centre of core (y 4..10)
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            collector.submitModelPart(core, poseStack,
                    RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS),
                    state.lightCoords, OverlayTexture.NO_OVERLAY, state.sprite);
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
