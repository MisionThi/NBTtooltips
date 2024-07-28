package net.mision_thi.nbttooltips.tooltips;

import net.minecraft.component.ComponentChanges;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mision_thi.nbttooltips.config.ModConfigs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.mision_thi.nbttooltips.NBTtooltipsMod.client;

public class TooltipChanger {
    /**
     * implemented in mixins to make this equal to any owner. used by NBT_OPS_UNLIMITED
     */
    public static final RegistryEntryOwner<?> ALL_EQUALITY_OWNER = new RegistryEntryOwner<>() {
        @Override
        public boolean ownerEquals(RegistryEntryOwner<Object> other) {
            return true;
        }
    };
    /**
     * for encoding unlimited data into NbtElement for getNBT methods
     */
    private static final RegistryOps<NbtElement> NBT_OPS_UNLIMITED = RegistryOps.of(NbtOps.INSTANCE, new RegistryOps.RegistryInfoGetter() {
        private final RegistryOps.RegistryInfo<?> INFO = new RegistryOps.RegistryInfo<>(ALL_EQUALITY_OWNER, null, null);

        @Override
        public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
            //noinspection unchecked
            return Optional.of((RegistryOps.RegistryInfo<T>) INFO);
        }

    });

    public static void Main(ItemStack itemStack, List<Text> list) {

        /*
            Recreate the normal NBT text.
            With that NBT text recreated, we can find the index of the text.
            After we found it we remove it and keep the index stored in the `index` variable.
        */
        Text findText = Text.translatable("item.components", itemStack.getComponents().size()).formatted(Formatting.DARK_GRAY);
        int index = list.indexOf(findText);
        if (index == -1) index = Math.max(0, list.size() - 1);
        else list.remove(index);

        // build the nbt text
        NbtTextBuilder builder = new NbtTextBuilder();
        builder.append(Text.translatable("item.nbt_tags.nbttooltips").formatted(Formatting.DARK_GRAY));
        builder.buildElement(ComponentChanges.CODEC.encodeStart(NBT_OPS_UNLIMITED, itemStack.getComponentChanges()).getOrThrow());
        list.addAll(index, builder.build());
    }

    /**
     * @author aMelonRind
     */
    private static class NbtTextBuilder {
        private static final Set<List<String>> textPaths = Set.of(
                List.of("minecraft:custom_data", "display", "Name"),
                List.of("minecraft:custom_data", "display", "Lore", "[]"),
                List.of("minecraft:custom_name"),
                List.of("minecraft:lore", "[]")
        );

        private final int SPACE_WIDTH = client.textRenderer.getWidth(" ");
        // config
        private final int lineStep = Math.min(ModConfigs.LINE_LIMIT * 6, client.getWindow().getScaledWidth() - 80);
        private final Formatting stringColour = colour(ModConfigs.STRING_COLOUR);
        private final Formatting quotationColour = colour(ModConfigs.QUOTATION_COLOUR);
        private final Formatting separationColour = colour(ModConfigs.SEPARATION_COLOUR);
        private final Formatting integerColour = colour(ModConfigs.INTEGER_COLOUR);
        private final Formatting typeColour = colour(ModConfigs.TYPE_COLOUR);
        private final Formatting fieldColour = colour(ModConfigs.FIELD_COLOUR);
        private final Formatting lstringColour = colour(ModConfigs.LSTRING_COLOUR);

        private final Text SINGLE_QUOTE = Text.literal("'").formatted(quotationColour);
        private final Text DOUBLE_QUOTE = Text.literal("\"").formatted(quotationColour);
        private final Text LSTRING = Text.empty().append(DOUBLE_QUOTE).append(Text.literal("....").formatted(lstringColour)).append(DOUBLE_QUOTE);

        private final Text INDENT = Text.literal("     ");
        private final Text SPACE = Text.literal(" ").formatted(separationColour);
        private final Text BRACKET_START = Text.literal("{").formatted(separationColour);
        private final Text BRACKET_END = Text.literal("}").formatted(separationColour);
        private final Text SQUARE_BRACKET_START = Text.literal("[").formatted(separationColour);
        private final Text SQUARE_BRACKET_END = Text.literal("]").formatted(separationColour);
        private final Text SEPARATOR = Text.literal(",").formatted(separationColour);
        private final Text COLON = Text.literal(":").formatted(separationColour);
        private final Text SEMICOLON = Text.literal(";").formatted(separationColour);

        private final Text BYTE = Text.literal("b").formatted(typeColour);
        private final Text SHORT = Text.literal("s").formatted(typeColour);
        private final Text INTEGER = Text.literal("I").formatted(typeColour);
        private final Text LONG = Text.literal("L").formatted(typeColour);
        private final Text FLOAT = Text.literal("f").formatted(typeColour);

        private final Stack<String> stack = new Stack<>();
        private final List<List<Text>> list = new ArrayList<>();
        private List<Text> currentLine; // should always be list[-1]
        private int column = 0;
        private boolean doSpace = false;
        private boolean ignoreNextSeparator = false;
        @Nullable
        private List<Text> groups = null;

        @Contract(pure = true)
        private static Formatting colour(@NotNull String colour) {
            return switch (colour) {
                case "black" -> Formatting.BLACK;
                case "dark_blue" -> Formatting.DARK_BLUE;
                case "dark_green" -> Formatting.DARK_GREEN;
                case "dark_aqua" -> Formatting.DARK_AQUA;
                case "dark_red" -> Formatting.DARK_RED;
                case "dark_purple" -> Formatting.DARK_PURPLE;
                case "gold" -> Formatting.GOLD;
                case "gray" -> Formatting.GRAY;
                case "dark_gray" -> Formatting.DARK_GRAY;
                case "blue" -> Formatting.BLUE;
                case "green" -> Formatting.GREEN;
                case "aqua" -> Formatting.AQUA;
                case "red" -> Formatting.RED;
                case "light_purple" -> Formatting.LIGHT_PURPLE;
                case "yellow" -> Formatting.YELLOW;
                default -> Formatting.WHITE;
            };
        }

        private static @NotNull Text joinTexts(@NotNull List<Text> texts) {
            MutableText text = Text.empty();
            for (Text sub : texts) text.append(sub);
            return text;
        }

        private NbtTextBuilder() {
            nextLine();
        }

        private void nextLine() {
            list.add(currentLine = new ArrayList<>());
            column = 0;
        }

        private void groupStart() {
            groups = new ArrayList<>();
        }

        private void groupEnd() {
            if (groups != null) {
                Text text = joinTexts(groups);
                groups = null;
                append(text);
            }
        }

        private NbtTextBuilder appendNoLineBreak(Text text) {
            return append(text, false);
        }

        private NbtTextBuilder append(Text text) {
            return append(text, true);
        }

        private NbtTextBuilder append(Text text, boolean checkLineBreak) {
            if (groups != null) {
                groups.add(text);
                return this;
            }
            int length = client.textRenderer.getWidth(text);
            if (checkLineBreak && column + (doSpace ? SPACE_WIDTH : 0) + length >= lineStep) {
                nextLine();
                currentLine.add(INDENT);
            }
            if (doSpace) {
                if (column != 0) {
                    currentLine.add(SPACE);
                    column += SPACE_WIDTH;
                }
                doSpace = false;
            }
            currentLine.add(text);
            column += length;
            return this;
        }

        private NbtTextBuilder appendRaw(String str, Formatting format) {
            return append(Text.literal(str).formatted(format));
        }

        private void appendField(String str) {
            appendRaw(str, fieldColour).appendNoLineBreak(COLON).space();
        }

        private void arrayHeader(Text type) {
            appendNoLineBreak(SQUARE_BRACKET_START)
                    .appendNoLineBreak(type)
                    .appendNoLineBreak(SEMICOLON)
                    .space()
                    .ignoreNextSeparator();
        }

        private void appendString(String str) {
            str = NbtString.escape(str);
            if (client.textRenderer.getWidth(str) >= lineStep) append(LSTRING);
            else {
                Text quote = str.charAt(0) == '\'' ? SINGLE_QUOTE : DOUBLE_QUOTE;
                append(Text.empty()
                        .append(quote)
                        .append(Text.literal(str.substring(1, str.length() - 1)).formatted(stringColour))
                        .append(quote)
                );
            }
        }

        private NbtTextBuilder space() {
            if (groups == null) {
                doSpace = true;
            } else {
                groups.add(SPACE);
            }
            return this;
        }

        private void ignoreNextSeparator() {
            ignoreNextSeparator = true;
        }

        private NbtTextBuilder separator() {
            if (ignoreNextSeparator) {
                ignoreNextSeparator = false;
                return this;
            }
            return appendNoLineBreak(SEPARATOR).space();
        }

        private void appendNumber(String str) {
            append(Text.literal(str).formatted(integerColour));
        }

        private void appendNumber(String str, Text unit) {
            append(Text.empty().append(str).formatted(integerColour).append(unit));
        }

        private void append(byte num) {
            appendNumber(Byte.toString(num), BYTE);
        }

        private void append(short num) {
            appendNumber(Short.toString(num), SHORT);
        }

        private void append(int num) {
            appendNumber(Integer.toString(num));
        }

        private void append(long num) {
            appendNumber(Long.toString(num), LONG);
        }

        private void append(float num) {
            appendNumber(Float.toString(num), FLOAT);
        }

        private void append(double num) {
            appendNumber(Double.toString(num));
        }

        public void buildElement(NbtElement element) {
            if (element == null) {
                appendRaw("null", typeColour);
                return;
            }
            switch (element.getType()) {
                case NbtElement.BYTE_TYPE -> append(((NbtByte) element).byteValue());
                case NbtElement.SHORT_TYPE -> append(((NbtShort) element).shortValue());
                case NbtElement.INT_TYPE -> append(((NbtInt) element).intValue());
                case NbtElement.LONG_TYPE -> append(((NbtLong) element).longValue());
                case NbtElement.FLOAT_TYPE -> append(((NbtFloat) element).floatValue());
                case NbtElement.DOUBLE_TYPE -> append(((NbtDouble) element).doubleValue());
                case NbtElement.NUMBER_TYPE -> appendNumber(((AbstractNbtNumber) element).numberValue().toString());
                case NbtElement.STRING_TYPE -> appendString(textPaths.contains(stack)
                        ? element.asString().replaceAll("(?<=[,{])\"(?:bold|italic|underlined|strikethrough|obfuscated)\":false,", "").replaceAll(",\"underlined\":false(?=})", "")
                        : element.asString());
                case NbtElement.COMPOUND_TYPE -> {
                    NbtCompound compound = (NbtCompound) element;
                    appendNoLineBreak(BRACKET_START).ignoreNextSeparator();
                    for (String key : compound.getKeys()) {
                        NbtElement e = compound.get(key);
                        separator();
                        if (e instanceof AbstractNbtNumber) groupStart();
                        appendField(key.replaceFirst("^minecraft:", "mc:"));
                        stack.push(key);
                        buildElement(e);
                        stack.pop();
                        groupEnd();
                    }
                    append(BRACKET_END);
                }
                case NbtElement.LIST_TYPE -> {
                    appendNoLineBreak(SQUARE_BRACKET_START).ignoreNextSeparator();
                    stack.push("[]");
                    for (NbtElement e : (NbtList) element) {
                        separator().buildElement(e);
                    }
                    stack.pop();
                    append(SQUARE_BRACKET_END);
                }
                case NbtElement.BYTE_ARRAY_TYPE -> {
                    arrayHeader(BYTE);
                    for (NbtByte e : (NbtByteArray) element) {
                        separator().append(e.byteValue());
                    }
                    append(SQUARE_BRACKET_END);
                }
                case NbtElement.INT_ARRAY_TYPE -> {
                    arrayHeader(INTEGER);
                    for (NbtInt e : (NbtIntArray) element) {
                        separator().append(e.intValue());
                    }
                    append(SQUARE_BRACKET_END);
                }
                case NbtElement.LONG_ARRAY_TYPE -> {
                    arrayHeader(LONG);
                    for (NbtLong e : (NbtLongArray) element) {
                        separator().append(e.longValue());
                    }
                    append(SQUARE_BRACKET_END);
                }
                case NbtElement.END_TYPE -> appendRaw("end", typeColour);
                default -> appendRaw(element.asString(), Formatting.RED);
            }
        }

        public List<Text> build() {
            return list.stream().map(NbtTextBuilder::joinTexts).toList();
        }

    }

}
