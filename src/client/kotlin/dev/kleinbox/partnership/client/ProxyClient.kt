package dev.kleinbox.partnership.client

import dev.kleinbox.partnership.client.screen.MarineCannonScreen
import dev.kleinbox.partnership.main.Proxy
import dev.kleinbox.partnership.main.block.cannon.MarineCannonBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos

class ProxyClient : Proxy() {
    override fun openMarineScreen(pos: BlockPos) {
        val client = Minecraft.getInstance()
        if (client.level?.getBlockEntity(pos) is MarineCannonBlockEntity) {
            Minecraft.getInstance().setScreen(MarineCannonScreen(pos))
        }
    }
}