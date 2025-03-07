package dev.kleinbox.partnership.main

import dev.kleinbox.partnership.main.registries.*
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val MOD_ID = "partnership"
const val BRAND_COLOR: Int = 0xFF2D54

object Partnership : ModInitializer {

	val logger: Logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitialize() {
		BlockRegistries
		BlockEntityRegistries
		ItemRegistries
		GroupRegistries
		MenuRegistries
		RecipeRegistries
		EntityRegistries
		NetworkRegistries
		GameRuleRegistries
		CommandRegistries
	}
}