package net.mision_thi.nbttooltips.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.mision_thi.nbttooltips.tooltips.TooltipChanger;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

@Mixin(ItemStack.class)
public abstract class mision_thi_TooltipChanger {

	@Shadow public abstract String toString();

	@Shadow @Final private static Logger LOGGER;

	@Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
	protected void injectEditTooltipmethod(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<ArrayList<Text>> info) {

		boolean isShiftPressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT );

		// If the advanced tooltips are on and the shift key is pressed the method is run.
		if (context.isAdvanced() && isShiftPressed == Boolean.TRUE) {
			// initialise the needed data
			MinecraftClient client = MinecraftClient.getInstance();
			ItemStack itemStack = ( ItemStack ) ( Object ) this;
			ArrayList<Text> list = info.getReturnValue();

			/*
				Before calling the main method from the `tooltip changer` class.
				We check if the item even has custom NBT.
			 */
			if (itemStack.hasNbt()) {
				TooltipChanger tooltipMain = new TooltipChanger();
				info.setReturnValue(tooltipMain.Main(client, itemStack, list));
			}

		}
	}
}