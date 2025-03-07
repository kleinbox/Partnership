package dev.kleinbox.partnership.client.registries

import dev.kleinbox.partnership.client.screen.BoatyardScreen
import dev.kleinbox.partnership.main.registries.MenuRegistries
import net.minecraft.client.gui.screens.MenuScreens

object ScreenRegistries {
    init {
        MenuScreens.register(MenuRegistries.BOATYARD, ::BoatyardScreen)
    }
}