package net.mision_thi.nbttooltips.config;


import com.mojang.datafixers.util.Pair;
import net.mision_thi.nbttooltips.NBTtooltipsMod;

import static net.mision_thi.nbttooltips.NBTtooltipsMod.LOGGER;


public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static int LINE_LIMIT;
    public static String STRING_COLOUR;
    public static String QUOTATION_COLOUR;
    public static String SEPARATION_COLOUR;
    public static String INTEGER_COLOUR;
    public static String TYPE_COLOUR;
    public static String FIELD_COLOUR;
    public static String LSTRING_COLOUR;


    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(NBTtooltipsMod.MOD_ID + "_config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("nbttooltips.line.length", 50), "int");
        configs.addKeyValuePair(new Pair<>("nbttooltips.stringColour", "green"), "String");
        configs.addKeyValuePair(new Pair<>("nbttooltips.quotationColour", "white"), "String");
        configs.addKeyValuePair(new Pair<>("nbttooltips.separationColour", "white"), "String");
        configs.addKeyValuePair(new Pair<>("nbttooltips.integerColour", "gold"), "String");
        configs.addKeyValuePair(new Pair<>("nbttooltips.typeColour", "red"), "String");
        configs.addKeyValuePair(new Pair<>("nbttooltips.fieldColour", "aqua"), "String");
        configs.addKeyValuePair(new Pair<>("nbttooltips.lstringColour", "yellow"), "String");


    }

    private static void assignConfigs() {
        LINE_LIMIT = CONFIG.getOrDefault("nbttooltips.line.length", 50);
        STRING_COLOUR = CONFIG.getOrDefault("nbttooltips.stringColour", "green");
        QUOTATION_COLOUR = CONFIG.getOrDefault("nbttooltips.quotationColour", "white");
        SEPARATION_COLOUR = CONFIG.getOrDefault("nbttooltips.separationColour", "white");
        INTEGER_COLOUR = CONFIG.getOrDefault("nbttooltips.integerColour", "gold");
        TYPE_COLOUR = CONFIG.getOrDefault("nbttooltips.typeColour", "red");
        FIELD_COLOUR = CONFIG.getOrDefault("nbttooltips.fieldColour", "aqua");
        LSTRING_COLOUR = CONFIG.getOrDefault("nbttooltips.lstringColour", "yellow");

        LOGGER.info("All " + configs.getConfigsList().size() + " have been assigned properly");
    }
}
