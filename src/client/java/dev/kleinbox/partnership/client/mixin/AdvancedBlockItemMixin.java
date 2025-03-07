package dev.kleinbox.partnership.client.mixin;

import dev.kleinbox.partnership.main.item.extendable.AdvancedItemDescription;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AdvancedItemDescription.class)
public abstract class AdvancedBlockItemMixin {
    @SuppressWarnings({"MissingUnique", "unused"})
    private boolean isHoldingShift() {
        return Screen.hasShiftDown();
    }
}
