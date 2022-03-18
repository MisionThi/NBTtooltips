package net.mision_thi.nbttooltips.mixin;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.mision_thi.nbttooltips.NBTtooltipsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.glfw.GLFW;

@Mixin(ItemStack.class)
public abstract class mision_thi_TooltipChanger {

	@Shadow public abstract String toString();


	@Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true)
	protected void injectEditTooltipmethod(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<ArrayList<Text>> info) {
		// Check if the key is pressed
		MinecraftClient client = MinecraftClient.getInstance();
		boolean isShiftPressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT );

		if (context.isAdvanced() && isShiftPressed == Boolean.TRUE) {
			// Create item stack
			ItemStack itemStack = ( ItemStack ) ( Object ) this;

			// Get the list return value
			ArrayList<Text> list = info.getReturnValue();
			ArrayList<Text> temp = new ArrayList<Text>();

			// If it has nbt remove the last part from the list and replace it with the new part
			if (itemStack.hasNbt()) {

				temp.add((new TranslatableText("item.nbt_tags", itemStack.getNbt().getKeys().size())).formatted(Formatting.DARK_GRAY));
				//System.out.println(list);
				//System.out.println(list.indexOf(temp.get(0)));
				int indexInsertLocation = list.indexOf(temp.get(0));

				// Calculate index of last element
				int index = indexInsertLocation;

				// Delete last element by passing index
				list.remove(index);

				// Get the nbt list that we want to show
				String nbtList = String.valueOf(itemStack.getNbt());

				// Setup symbols to check for
				Pattern p = Pattern.compile("[{}:\"\\[\\],']", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(nbtList);

				// Create new literalText
				MutableText mutableText = new LiteralText("");
				mutableText.append(new TranslatableText("item.nbt_tags.nbttooltips").formatted(Formatting.DARK_GRAY));

				// Start the loop
				int lineStep = 50;
				int lineLimit = 50;
				int removedCharters = 0;
				Boolean singleQuotationMark = Boolean.FALSE;
				Boolean lineAdded = Boolean.FALSE;
				String lastString = "";

				int lastIndex = 0;
				int count = 0;

				while (m.find()) {
					lineAdded = Boolean.FALSE;
					count = count+1;
					// Check for the single quotation marks, in between the ' everything has to become green
					if (nbtList.charAt(m.start()) == '\'') {
						if (singleQuotationMark.equals(Boolean.FALSE)) { // If false color only the quotation mark
							mutableText.append(new LiteralText(String.valueOf(nbtList.charAt(m.start()))).formatted(Formatting.WHITE));
							singleQuotationMark = Boolean.TRUE;
						}
						else { // Else color the quotation mark and make the rest green
							mutableText.append(new LiteralText(nbtList.substring(lastIndex+1,m.start())).formatted(Formatting.GREEN));
							mutableText.append(new LiteralText(String.valueOf(nbtList.charAt(m.start()))).formatted(Formatting.WHITE));
							singleQuotationMark = Boolean.FALSE;
						}
						lastString = String.valueOf(nbtList.charAt(m.start()));
						lastIndex = m.start();
					}

					// When it's not in between ' then let the rest of the formatting work
					if (singleQuotationMark == Boolean.FALSE) {
						// Check the opening bracket and square bracket
						if (nbtList.charAt(m.start()) == '{' || nbtList.charAt(m.start()) == '[' ) {
							mutableText.append(new LiteralText(String.valueOf(nbtList.charAt(m.start()))).formatted(Formatting.WHITE));
							lastString = String.valueOf(nbtList.charAt(m.start()));
							lastIndex = m.start();
						}

						// Check the closing brackets and square brackets
						if (nbtList.charAt(m.start()) == '}' || nbtList.charAt(m.start()) == ']' || nbtList.charAt(m.start()) == ',') {
							if (nbtList.charAt(m.start()-1) == 's' || nbtList.charAt(m.start()-1) == 'S' ||
									nbtList.charAt(m.start()-1) == 'b' || nbtList.charAt(m.start()-1) == 'B' ||
									nbtList.charAt(m.start()-1) == 'l' || nbtList.charAt(m.start()-1) == 'L' ||
									nbtList.charAt(m.start()-1) == 'f' || nbtList.charAt(m.start()-1) == 'F'
							) {
								mutableText.append(new LiteralText(nbtList.substring(lastIndex+1,m.start()-1)).formatted(Formatting.GOLD));
								mutableText.append(new LiteralText(nbtList.substring(m.start()-1,m.start())).formatted(Formatting.RED));

							}
							else {
								mutableText.append(new LiteralText(nbtList.substring(lastIndex+1,m.start())).formatted(Formatting.GOLD));
							}


							mutableText.append(new LiteralText(String.valueOf(nbtList.charAt(m.start()))).formatted(Formatting.WHITE));
							if (nbtList.charAt(m.start()) == ',') { mutableText.append(new LiteralText(" ").formatted(Formatting.WHITE)); }
							lastString = String.valueOf(nbtList.charAt(m.start()));
							lastIndex = m.start();
						}

						// Colour the :
						if (nbtList.charAt(m.start()) == ':') {
							if (!lastString.equals("\"")) {
								mutableText.append(new LiteralText(nbtList.substring(lastIndex+1,m.start())).formatted(Formatting.AQUA));

								mutableText.append(new LiteralText(String.valueOf(nbtList.charAt(m.start()))).formatted(Formatting.WHITE));
								mutableText.append(new LiteralText(" ").formatted(Formatting.WHITE));
								lastString = String.valueOf(nbtList.charAt(m.start()));
								lastIndex = m.start();
							}

						}
						// Colour the "
						if (nbtList.charAt(m.start()) == '"') {
							if (lastString.equals("\"")){

								// Check if the string is way too long
								if (m.start() - lineLimit > lineStep ){
									mutableText.append(new LiteralText("....").formatted(Formatting.YELLOW));
									removedCharters += m.start() - lineLimit;
								}
								else {
									mutableText.append(new LiteralText(nbtList.substring(lastIndex+1,m.start())).formatted(Formatting.GREEN));
								}

								mutableText.append(new LiteralText(String.valueOf(nbtList.charAt(m.start()))).formatted(Formatting.WHITE));
							}
							else {
								mutableText.append(new LiteralText(String.valueOf(nbtList.charAt(m.start()))).formatted(Formatting.WHITE));
							}
							lastString = String.valueOf(nbtList.charAt(m.start()));
							lastIndex = m.start();

						}
					}

					// Checks if the current location is higher than the limit
					if (m.start() - removedCharters >= lineLimit) {
						if (nbtList.charAt(m.start()) == '}' || nbtList.charAt(m.start()) == ']' || nbtList.charAt(m.start()) == ',') {

							if (lastString.equals("'")) {
								mutableText.append(new LiteralText(nbtList.substring(lastIndex+1,m.start())).formatted(Formatting.GREEN));
								lastIndex = m.start();
							}

							// Add the text to the list
							list.add(indexInsertLocation,mutableText);
							indexInsertLocation += 1;
							mutableText = new LiteralText("     ");
							lineAdded = Boolean.TRUE;
							lineLimit = lineLimit + lineStep;
						}

					}
				}

				// Add the new element
				if (lineAdded.equals(Boolean.FALSE)) {
					list.add(indexInsertLocation,mutableText);
				}

				// Return the list
				info.setReturnValue(list);
			}
		}
	}

}