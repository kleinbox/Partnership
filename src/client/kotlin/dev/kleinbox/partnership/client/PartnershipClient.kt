package dev.kleinbox.partnership.client

import dev.kleinbox.partnership.client.model.entity.KayakModel
import dev.kleinbox.partnership.client.model.entity.LifebuoyModel
import dev.kleinbox.partnership.client.registries.ScreenRegistries
import dev.kleinbox.partnership.client.renderer.armor.HatRenderer
import dev.kleinbox.partnership.client.renderer.blockentity.MarineCannonBlockEntityRenderer
import dev.kleinbox.partnership.client.renderer.entity.KayakRenderer
import dev.kleinbox.partnership.client.renderer.entity.LifebuoyRenderer
import dev.kleinbox.partnership.main.Partnership
import dev.kleinbox.partnership.main.registries.BlockEntityRegistries
import dev.kleinbox.partnership.main.registries.BlockRegistries
import dev.kleinbox.partnership.main.registries.EntityRegistries.CANNONBALL
import dev.kleinbox.partnership.main.registries.EntityRegistries.KAYAK
import dev.kleinbox.partnership.main.registries.EntityRegistries.LIFEBUOY
import dev.kleinbox.partnership.main.registries.ItemRegistries
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ThrownItemRenderer

object PartnershipClient : ClientModInitializer {

	override fun onInitializeClient() {
		// WAKE UP
		ScreenRegistries

		Partnership.proxy = ProxyClient()

		// Client
		blockRendering()
		entityRendering()
		blockEntityRenderer()
		customArmorRenderer()
	}

	private fun blockRendering() {
		BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.METAL_SCAFFOLDING, RenderType.cutout())
		BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.BUOY, RenderType.cutout())
		BlockRenderLayerMap.INSTANCE.putBlock(BlockRegistries.ANCHOR, RenderType.cutout())
	}

	private fun entityRendering() {
		EntityRendererRegistry.register(KAYAK, ::KayakRenderer)
		EntityModelLayerRegistry.registerModelLayer(KayakModel.LAYER_LOCATION, KayakModel::createBodyLayer)

		EntityRendererRegistry.register(LIFEBUOY, ::LifebuoyRenderer)
		EntityModelLayerRegistry.registerModelLayer(LifebuoyModel.LAYER_LOCATION, LifebuoyModel::createBodyLayer)

		EntityRendererRegistry.register(CANNONBALL) { context: EntityRendererProvider.Context -> ThrownItemRenderer(context) }
	}

	private fun blockEntityRenderer() {
		BlockEntityRenderers.register(BlockEntityRegistries.MARINE_CANNON, ::MarineCannonBlockEntityRenderer)
	}

	private fun customArmorRenderer() {
		ArmorRenderer.register(HatRenderer(), ItemRegistries.CAPTAINS_HAT)
		ArmorRenderer.register(HatRenderer(), ItemRegistries.SAILORS_HAT)
	}
}