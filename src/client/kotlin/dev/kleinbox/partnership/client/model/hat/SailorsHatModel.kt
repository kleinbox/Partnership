package dev.kleinbox.partnership.client.model.hat

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.kleinbox.partnership.main.MOD_ID
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import java.util.function.Function


class SailorsHatModel(val root: ModelPart) : Model(Function { resourceLocation: ResourceLocation -> RenderType.entityTranslucent(resourceLocation) }) {

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay)
    }

    fun setupAnim(humanoidModel: HumanoidModel<LivingEntity>) {
        root.setRotation(humanoidModel.hat.xRot, humanoidModel.hat.yRot, humanoidModel.hat.zRot)
        root.setPos(humanoidModel.hat.x, humanoidModel.hat.y, humanoidModel.hat.z)
    }

    companion object {
        val TEXTURE = ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/models/armor/sailors_hat.png")

        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            /*val bb_main = */partdefinition.addOrReplaceChild(
                "main",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -3.0f, -3.0f, 6.0f, 3.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -8.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}