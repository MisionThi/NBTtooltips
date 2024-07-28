package net.mision_thi.nbttooltips.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.mision_thi.nbttooltips.tooltips.TooltipChanger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryEntry.Reference.class)
public class MixinRegistryEntryReference {

    @Inject(method = "ownerEquals", at = @At("RETURN"), cancellable = true)
    public void overrideOwnerEquals(RegistryEntryOwner<?> owner, CallbackInfoReturnable<Boolean> cir) {
        if (owner == TooltipChanger.ALL_EQUALITY_OWNER) {
            cir.setReturnValue(true);
        }
    }

}
