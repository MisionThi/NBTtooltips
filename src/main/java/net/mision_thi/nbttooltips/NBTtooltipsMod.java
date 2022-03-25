package net.mision_thi.nbttooltips;

import net.fabricmc.api.ModInitializer;
import net.mision_thi.nbttooltips.config.ModConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NBTtooltipsMod implements ModInitializer {
	public static final String MOD_ID = "NBTtooltips";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();
	}
}