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
        VertexConsumer buffer = material.buffer(bufferSource, RenderType::entityCutout);

        poseStack.pushPose();
        poseStack.translate(0.5F, 7.0F / 16.0F + bob, 0.5F); // centro del núcleo (y 4..10)
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        core.render(poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
