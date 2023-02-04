package net.mision_thi.nbttooltips;

import net.fabricmc.api.ModInitializer;
import net.mision_thi.nbttooltips.config.ModConfigs;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NBTtooltipsMod implements ModInitializer {
	public static final String MOD_ID = "NBTtooltips";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final KeyBinding KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding("nbttooltips.keybind", GLFW.GLFW_KEY_LEFT_SHIFT, KeyBinding.INVENTORY_CATEGORY));

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();
	}
}