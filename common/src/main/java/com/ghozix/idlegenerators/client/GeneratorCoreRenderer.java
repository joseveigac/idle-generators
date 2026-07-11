package com.ghozix.idlegenerators.client;

import com.ghozix.idlegenerators.IdleGenerators;
import com.ghozix.idlegenerators.generator.GeneratorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
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
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.sprite == null) return;
        float t = (state.gameTime % LOOP_TICKS) + state.partialTick;
        float angle = -t * (360.0F / LOOP_TICKS);
        float bob = Mth.sin(t * Mth.TWO_PI / LOOP_TICKS) / 16.0F;

        poseStack.pushPose();
        poseStack.translate(0.5F, 7.0F / 16.0F + bob, 0.5F); // centre of core (y 4..10)
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        collector.submitModelPart(core, poseStack,
                RenderTypes.entityCutout(TextureAtlas.LOCATION_BLOCKS),
                state.lightCoords, OverlayTexture.NO_OVERLAY, state.sprite);
        poseStack.popPose();
    }
}
