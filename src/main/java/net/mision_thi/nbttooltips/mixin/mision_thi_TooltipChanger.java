package net.mision_thi.nbttooltips.mixin;

import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.*;
import net.mision_thi.nbttooltips.NBTtooltipsMod;
import net.mision_thi.nbttooltips.tooltips.TooltipChanger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.mision_thi.nbttooltips.NBTtooltipsMod.client;

@Mixin(ItemStack.class)
public abstract class mision_thi_TooltipChanger {

	@Shadow public abstract boolean isEmpty();

	@Unique private boolean shouldShow = false;

	@Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/tooltip/TooltipType;isCreative()Z"))
	protected boolean forceVisible(TooltipType instance) {
		int code = InputUtil.fromTranslationKey(NBTtooltipsMod.KEYBIND.getBoundKeyTranslationKey()).getCode();
		shouldShow = instance.isAdvanced() && InputUtil.isKeyPressed(client.getWindow().getHandle(), code);
		return shouldShow || instance.isCreative();
	}

	@Inject(method = "getTooltip", at = @At("RETURN"))
	protected void injectEditTooltipmethod(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info) {

		// If the advanced tooltips are on and the shift key is pressed the method is run.
		if (shouldShow && !isEmpty()) {
			ItemStack itemStack = (ItemStack) (Object) this;

			/*
				Before calling the main method from the `tooltip changer` class.
				We check if the item even has custom NBT.
			 */
			if (!itemStack.getComponentChanges().isEmpty()) {
				TooltipChanger.Main(itemStack, info.getReturnValue());
			}

		}
	}
}