package net.mision_thi.nbttooltips;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.mision_thi.nbttooltips.config.ModConfigs;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NBTtooltipsMod implements ModInitializer {
	public static final MinecraftClient client = MinecraftClient.getInstance();
	public static final String MOD_ID = "NBTtooltips";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final KeyBinding KEYBIND = KeyBindingHelper.registerKeyBinding(new KeyBinding("nbttooltips.keybind", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, KeyBinding.INVENTORY_CATEGORY));

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();
	}
}