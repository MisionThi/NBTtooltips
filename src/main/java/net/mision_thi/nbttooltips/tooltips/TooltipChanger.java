package net.mision_thi.nbttooltips.tooltips;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.mision_thi.nbttooltips.config.ModConfigs;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TooltipChanger {
    public ArrayList<Text> Main(MinecraftClient client, ItemStack itemStack, ArrayList<Text> list) {
        // Initialise the needed variables
        ArrayList<Text> temp = new ArrayList<Text>();

        /*
            Recreate the normal NBT text.
            With that NBT text recreated, we can find the index of the text.
            After we found it we remove it and keep the index stored in the `index` variable.
        */
        temp.add(MutableText.of(new TranslatableTextContent("item.nbt_tags", itemStack.getNbt().getKeys().size())).formatted(Formatting.DARK_GRAY));
        int index = list.indexOf(temp.get(0));
        int indexInsertLocation = list.indexOf(temp.get(0));
        list.remove(index);

        /*
            Get the NBT list that we want to show.
            And we set the symbols up where we look for, so we can detect what to give which color.
         */
        String nbtList = String.valueOf(itemStack.getNbt());
        Pattern p = Pattern.compile("[{}:\"\\[\\],']", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(nbtList);

        // Create new literalText, which we will be adding to the list.
        MutableText mutableText = MutableText.of(new LiteralTextContent(""));
        mutableText.append(MutableText.of(new TranslatableTextContent("item.nbt_tags.nbttooltips")).formatted(Formatting.DARK_GRAY));


        /*  **Loop through the NBT data**
         *
         */
        int lineStep = ModConfigs.LINE_LIMIT; // config
        Formatting stringColour = Colour(ModConfigs.STRING_COLOUR);
        Formatting quotationColour = Colour(ModConfigs.QUOTATION_COLOUR);
        Formatting separationColour = Colour(ModConfigs.SEPARATION_COLOUR);
        Formatting integerColour = Colour(ModConfigs.INTEGER_COLOUR);
        Formatting typeColour = Colour(ModConfigs.TYPE_COLOUR);
        Formatting fieldColour = Colour(ModConfigs.FIELD_COLOUR);
        Formatting lstringColour = Colour(ModConfigs.LSTRING_COLOUR);

        int lineLimit = lineStep;
        int removedCharters = 0;
        int lastIndex = 0;
        Boolean singleQuotationMark = Boolean.FALSE;
        Boolean lineAdded = Boolean.FALSE;
        String lastString = "";


        while (m.find()) {
            lineAdded = Boolean.FALSE;

            /*  **Checking for single quotation marks**
             *  After that we check if the last char was a single quotation mark we can check that by checking the variable "singleQuotationMark".
             *  We do this so the data between the single quotation marks will get the "stringColour".
             *  And we make sure that the single quotation marks get the "quotationColour".
             */
            if (nbtList.charAt(m.start()) == '\'') {
                if (singleQuotationMark.equals(Boolean.FALSE)) { // If false color only the quotation mark
                    mutableText.append(MutableText.of(new LiteralTextContent(String.valueOf(nbtList.charAt(m.start())))).formatted(quotationColour));
                    singleQuotationMark = Boolean.TRUE;
                }
                else { // Else color the quotation mark and make the rest green
                    mutableText.append(MutableText.of(new LiteralTextContent(nbtList.substring(lastIndex+1,m.start()))).formatted(stringColour));
                    mutableText.append(MutableText.of(new LiteralTextContent(String.valueOf(nbtList.charAt(m.start())))).formatted(quotationColour));
                    singleQuotationMark = Boolean.FALSE;
                }
                lastString = String.valueOf(nbtList.charAt(m.start()));
                lastIndex = m.start();
            }


            /*  **Checking if the text is not between the single quotation mark**
             *  1). When the text is not between the single quotation marks the normal formatting will get to work.
             *  2). We check if the char that is found is an opening bracket.
             *  3). The closing brackets and the comma (these are also the chars that decide when we go to the next line.).
             *  4). Now we check for the colon.
             *  5). And lastly we check for the double quotation marks.
             */
            if (singleQuotationMark == Boolean.FALSE) {

                /*  2). We check if the char that is found is an opening bracket.
                        Adds the found char and gives it the "separationColour"
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '{' || nbtList.charAt(m.start()) == '[' ) {
                    mutableText.append(MutableText.of(new LiteralTextContent(String.valueOf(nbtList.charAt(m.start())))).formatted(separationColour));
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();
                }

                /*  3). The closing brackets and the comma (these are also the chars that decide when we go to the next line.).
                        If the char before the found char is a char that indicates what type of variable the text in front of it is.
                            Then: Add the text before the char that indicates the type and give it the "integerColour".
                                  After that add the char type and give it the "typeColour".
                            Else: Give all the text in front of the count char the "integerColour".
                        Now we add the found char with the "separationColour" (includes comma's)
                        If the char was a comma add a space (this way it's more readable)
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '}' || nbtList.charAt(m.start()) == ']' || nbtList.charAt(m.start()) == ',') {
                    if (nbtList.charAt(m.start()-1) == 's' || nbtList.charAt(m.start()-1) == 'S' ||
                            nbtList.charAt(m.start()-1) == 'b' || nbtList.charAt(m.start()-1) == 'B' ||
                            nbtList.charAt(m.start()-1) == 'l' || nbtList.charAt(m.start()-1) == 'L' ||
                            nbtList.charAt(m.start()-1) == 'f' || nbtList.charAt(m.start()-1) == 'F'
                    ) {
                        mutableText.append(MutableText.of(new LiteralTextContent(nbtList.substring(lastIndex+1,m.start()-1))).formatted(integerColour));
                        mutableText.append(MutableText.of(new LiteralTextContent(nbtList.substring(m.start()-1,m.start()))).formatted(typeColour));

                    }
                    else {
                        mutableText.append(MutableText.of(new LiteralTextContent(nbtList.substring(lastIndex+1,m.start()))).formatted(integerColour));
                    }

                    mutableText.append(MutableText.of(new LiteralTextContent(String.valueOf(nbtList.charAt(m.start())))).formatted(separationColour));

                    if (nbtList.charAt(m.start()) == ',') { mutableText.append(MutableText.of(new LiteralTextContent(" ")).formatted(separationColour)); }
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();
                }

                /*  4). Now we check for the colon. (:)
                        If the last string doesn't equal the double quotation mark. (when it is between it should only get the "stringColour")
                            Then: Add the text in front of the colon and give it the "fieldColour".
                                  Add the found char and give it the "separationColour".
                                  Add a space so it's easier to read.
                                  Stores the lastString and lastIndex

                 */
                if (nbtList.charAt(m.start()) == ':') { // 4).
                    if (!lastString.equals("\"")) {
                        mutableText.append(MutableText.of(new LiteralTextContent(nbtList.substring(lastIndex+1,m.start()))).formatted(fieldColour));

                        mutableText.append(MutableText.of(new LiteralTextContent(String.valueOf(nbtList.charAt(m.start())))).formatted(separationColour));
                        mutableText.append(MutableText.of(new LiteralTextContent(" ")).formatted(separationColour));
                        lastString = String.valueOf(nbtList.charAt(m.start()));
                        lastIndex = m.start();
                    }

                }
                /*  5). And lastly we check for the double quotation marks.
                        If the last char was a " too.
                            Then: If the string between the columns is longer then the linesStep
                                Then: Convert the string between the quotation marks to .... "lstringColour"
                                      And add the removed chars to the "removeCharters" variable.
                                Else: Add the string and give it the "stringColour"
                            Else: Only add the " since it's the first one.
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '"') {
                    if (lastString.equals("\"")){

                        // Check if the string is way too long
                        if (m.start() - lineLimit > lineStep ){
                            mutableText.append(MutableText.of(new LiteralTextContent("....")).formatted(lstringColour));
                            removedCharters += m.start() - lineLimit;
                        }
                        else {
                            mutableText.append(MutableText.of(new LiteralTextContent(nbtList.substring(lastIndex+1,m.start()))).formatted(stringColour));
                        }

                        mutableText.append(MutableText.of(new LiteralTextContent(String.valueOf(nbtList.charAt(m.start())))).formatted(quotationColour));
                    }
                    else {
                        mutableText.append(MutableText.of(new LiteralTextContent(String.valueOf(nbtList.charAt(m.start())))).formatted(quotationColour));
                    }
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();

                }
            }


            /*  **Checks if the current index is higher than the limit**
             *  1). We check if the current index is higher than the current line limit.
             *  2). We check if the found char is a closing bracket or comma, since we only go to the next line with those chars.
             *  3). In there we check if we currently are in a single quotation mark area, if so we colour the first part "stringColour".
             *  4). The last thing we do is adding the text to the list and making sure that we go to the next line
             */
            if (m.start() - removedCharters >= lineLimit) { // 1).
                if (nbtList.charAt(m.start()) == '}' || nbtList.charAt(m.start()) == ']' || nbtList.charAt(m.start()) == ',') { // 2).

                    if (lastString.equals("'")) { // 3).
                        mutableText.append(MutableText.of(new LiteralTextContent(nbtList.substring(lastIndex+1,m.start()))).formatted(stringColour));
                        lastIndex = m.start();
                    }

                    // 4).
                    list.add(indexInsertLocation,mutableText);
                    indexInsertLocation += 1;
                    mutableText = MutableText.of(new LiteralTextContent("     "));
                    lineAdded = Boolean.TRUE;
                    lineLimit = lineLimit + lineStep;
                }
            }
        }

        /*  Add the last line
         *  If the line was added just before this.
         *      Then: don't add to the list since otherwise we just add an empty line.
         *      Else: we add the last part to the list too.
         */
        if (lineAdded.equals(Boolean.FALSE)) {
            list.add(indexInsertLocation,mutableText);
        }

        /*  Return the final list

         */
        return list;
    }

    private static Formatting Colour(String colour) {

        if (colour.equals("black")) { return Formatting.BLACK; }
        if (colour.equals("dark_blue")) { return Formatting.DARK_BLUE; }
        if (colour.equals("dark_green")) { return Formatting.DARK_GREEN; }
        if (colour.equals("dark_aqua")) { return Formatting.DARK_AQUA; }
        if (colour.equals("dark_red")) { return Formatting.DARK_RED; }
        if (colour.equals("dark_purple")) { return Formatting.DARK_PURPLE; }
        if (colour.equals("gold")) { return Formatting.GOLD; }
        if (colour.equals("gray")) { return Formatting.GRAY; }
        if (colour.equals("dark_gray")) { return Formatting.DARK_GRAY; }
        if (colour.equals("blue")) { return Formatting.BLUE; }
        if (colour.equals("green")) { return Formatting.GREEN; }
        if (colour.equals("aqua")) { return Formatting.AQUA; }
        if (colour.equals("red")) { return Formatting.RED; }
        if (colour.equals("light_purple")) { return Formatting.LIGHT_PURPLE; }
        if (colour.equals("yellow")) { return Formatting.YELLOW; }
        if (colour.equals("white")) { return Formatting.WHITE; }

        return Formatting.WHITE;
    }

}
