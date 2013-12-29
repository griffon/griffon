/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.swing;

import java.awt.Color;

/**
 * Defines all Java, CSS and HTML colors.
 *
 * @author Andres Almiray
 * @since 1.3.0
 */
public enum Colors {
    BLACK(Color.BLACK),
    BLUE(Color.BLUE),
    CYAN(Color.CYAN),
    DARK_GRAY(Color.DARK_GRAY),
    GRAY(Color.GRAY),
    GREEN(Color.GREEN),
    LIGHT_GRAY(Color.LIGHT_GRAY),
    MAGENTA(Color.MAGENTA),
    ORANGE(Color.ORANGE),
    PINK(Color.PINK),
    RED(Color.RED),
    WHITE(Color.WHITE),
    YELLOW(Color.YELLOW),
    // css colors
    FUCHSIA(new Color(255, 0, 255)),
    SILVER(new Color(192, 192, 192)),
    OLIVE(new Color(50, 50, 0)),
    PURPLE(new Color(50, 0, 50)),
    MAROON(new Color(50, 0, 0)),
    AQUA(new Color(0, 255, 255)),
    LIME(new Color(0, 255, 0)),
    TEAL(new Color(0, 50, 50)),
    NAVY(new Color(0, 0, 50)),
    // html colors
    ALICE_BLUE(new Color(240, 248, 255)),
    ANTIQUE_WHITE(new Color(250, 235, 215)),
    AQUAMARINE(new Color(127, 255, 212)),
    AZURE(new Color(240, 255, 255)),
    BAKERS_CHOCOLATE(new Color(92, 51, 23)),
    BEIGE(new Color(245, 245, 220)),
    BISQUE(new Color(255, 228, 196)),
    BLANCHED_ALMOND(new Color(255, 235, 205)),
    BLUE_VIOLET(new Color(138, 43, 226)),
    BRASS(new Color(181, 166, 66)),
    BRIGHT_GOLD(new Color(217, 217, 25)),
    BRONZE(new Color(140, 120, 83)),
    BROWN(new Color(165, 42, 42)),
    BURLY_WOOD(new Color(222, 184, 135)),
    CADET_BLUE(new Color(95, 158, 160)),
    CHARTREUSE(new Color(127, 255, 0)),
    CHOCOLATE(new Color(210, 105, 30)),
    COOL_COPPER(new Color(217, 135, 25)),
    COPPER(new Color(184, 115, 51)),
    CORAL(new Color(255, 127, 80)),
    CORNFLOWER_BLUE(new Color(100, 149, 237)),
    CORNSILK(new Color(255, 248, 220)),
    CRIMSON(new Color(220, 20, 60)),
    DARK_BLUE(new Color(0, 0, 139)),
    DARK_BROWN(new Color(92, 64, 51)),
    DARK_CYAN(new Color(0, 139, 139)),
    DARK_GOLDEN_ROD(new Color(184, 134, 11)),
    DARK_GREEN(new Color(0, 100, 0)),
    DARK_GREEN_COPPER(new Color(74, 118, 110)),
    DARK_KHAKI(new Color(189, 183, 107)),
    DARK_MAGENTA(new Color(139, 0, 139)),
    DARK_OLIVE_GREEN(new Color(85, 107, 47)),
    DARK_ORANGE(new Color(255, 140, 0)),
    DARK_ORCHID(new Color(153, 50, 204)),
    DARK_PURPLE(new Color(135, 31, 120)),
    DARK_RED(new Color(139, 0, 0)),
    DARK_SALMON(new Color(233, 150, 122)),
    DARK_SEA_GREEN(new Color(143, 188, 143)),
    DARK_SLATE_BLUE(new Color(72, 61, 139)),
    DARK_SLATE_GRAY(new Color(47, 79, 79)),
    DARK_TAN(new Color(151, 105, 79)),
    DARK_TURQUOISE(new Color(0, 206, 209)),
    DARK_VIOLET(new Color(148, 0, 211)),
    DARK_WOOD(new Color(133, 94, 66)),
    DEEP_PINK(new Color(255, 20, 147)),
    DEEP_SKY_BLUE(new Color(0, 191, 255)),
    DIM_GRAY(new Color(105, 105, 105)),
    DODGER_BLUE(new Color(30, 144, 255)),
    DUSTY_ROSE(new Color(133, 99, 99)),
    FADED_BROWN(new Color(245, 204, 176)),
    FELDSPAR(new Color(209, 146, 117)),
    FIRE_BRICK(new Color(178, 34, 34)),
    FLORAL_WHITE(new Color(255, 250, 240)),
    FOREST_GREEN(new Color(34, 139, 34)),
    GAINSBORO(new Color(220, 220, 220)),
    GHOST_WHITE(new Color(248, 248, 255)),
    GOLD(new Color(255, 215, 0)),
    GOLDEN_ROD(new Color(218, 165, 32)),
    GREEN_COPPER(new Color(82, 127, 118)),
    GREEN_YELLOW(new Color(173, 255, 47)),
    HONEY_DEW(new Color(240, 255, 240)),
    HOT_PINK(new Color(255, 105, 180)),
    HUNTER_GREEN(new Color(33, 94, 33)),
    INDIAN_RED(new Color(205, 92, 92)),
    INDIGO(new Color(75, 0, 130)),
    IVORY(new Color(255, 255, 240)),
    KHAKI(new Color(240, 230, 140)),
    LAVENDER(new Color(230, 230, 250)),
    LAVENDER_BLUSH(new Color(255, 240, 245)),
    LAWN_GREEN(new Color(124, 252, 0)),
    LEMON_CHIFFON(new Color(255, 250, 205)),
    LIGHT_BLUE(new Color(173, 216, 230)),
    LIGHT_CORAL(new Color(240, 128, 128)),
    LIGHT_CYAN(new Color(224, 255, 255)),
    LIGHT_GOLDEN_ROD_YELLOW(new Color(250, 250, 210)),
    LIGHT_GREEN(new Color(144, 238, 144)),
    LIGHT_PINK(new Color(255, 182, 193)),
    LIGHT_SALMON(new Color(255, 160, 122)),
    LIGHT_SEA_GREEN(new Color(32, 178, 170)),
    LIGHT_SKY_BLUE(new Color(135, 206, 250)),
    LIGHT_SLATE_BLUE(new Color(132, 112, 255)),
    LIGHT_SLATE_GRAY(new Color(119, 136, 153)),
    LIGHT_STEEL_BLUE(new Color(176, 196, 222)),
    LIGHT_WOOD(new Color(233, 194, 166)),
    LIGHT_YELLOW(new Color(255, 255, 224)),
    LIME_GREEN(new Color(50, 205, 50)),
    LINEN(new Color(250, 240, 230)),
    MANDARIN_ORANGE(new Color(228, 120, 51)),
    MEDIUM_AQUA_MARINE(new Color(102, 205, 170)),
    MEDIUM_BLUE(new Color(0, 0, 205)),
    MEDIUM_GOLDEN_ROD(new Color(234, 234, 174)),
    MEDIUM_ORCHID(new Color(186, 85, 211)),
    MEDIUM_PURPLE(new Color(147, 112, 216)),
    MEDIUM_SEA_GREEN(new Color(60, 179, 113)),
    MEDIUM_SLATE_BLUE(new Color(123, 104, 238)),
    MEDIUM_SPRING_GREEN(new Color(0, 250, 154)),
    MEDIUM_TURQUOISE(new Color(72, 209, 204)),
    MEDIUM_VIOLET_RED(new Color(199, 21, 133)),
    MEDIUM_WOOD(new Color(166, 128, 100)),
    MIDNIGHT_BLUE(new Color(25, 25, 112)),
    MINT_CREAM(new Color(245, 255, 250)),
    MISTY_ROSE(new Color(255, 228, 225)),
    MOCCASIN(new Color(255, 228, 181)),
    NAVAJO_WHITE(new Color(255, 222, 173)),
    NAVY_BLUE(new Color(35, 35, 142)),
    NEON_BLUE(new Color(77, 77, 255)),
    NEON_PINK(new Color(255, 110, 199)),
    NEW_MIDNIGHT_BLUE(new Color(0, 0, 156)),
    NEW_TAN(new Color(235, 199, 158)),
    OLD_GOLD(new Color(207, 181, 59)),
    OLD_LACE(new Color(253, 245, 230)),
    OLIVE_DRAB(new Color(107, 142, 35)),
    ORANGE_RED(new Color(255, 69, 0)),
    ORCHID(new Color(218, 112, 214)),
    PALE_GOLDEN_ROD(new Color(238, 232, 170)),
    PALE_GREEN(new Color(152, 251, 152)),
    PALE_TURQUOISE(new Color(175, 238, 238)),
    PALE_VIOLET_RED(new Color(216, 112, 147)),
    PAPAYA_WHIP(new Color(255, 239, 213)),
    PEACH_PUFF(new Color(255, 218, 185)),
    PERU(new Color(205, 133, 63)),
    PLUM(new Color(221, 160, 221)),
    POWDER_BLUE(new Color(176, 224, 230)),
    QUARTZ(new Color(217, 217, 243)),
    RICH_BLUE(new Color(89, 89, 171)),
    ROSY_BROWN(new Color(188, 143, 143)),
    ROYAL_BLUE(new Color(65, 105, 225)),
    SADDLE_BROWN(new Color(139, 69, 19)),
    SALMON(new Color(250, 128, 114)),
    SANDY_BROWN(new Color(244, 164, 96)),
    SCARLET(new Color(140, 23, 23)),
    SEA_GREEN(new Color(46, 139, 87)),
    SEA_SHELL(new Color(255, 245, 238)),
    SEMI_SWEET_CHOCOLATE(new Color(107, 66, 38)),
    SIENNA(new Color(160, 82, 45)),
    SKY_BLUE(new Color(135, 206, 235)),
    SLATE_BLUE(new Color(106, 90, 205)),
    SLATE_GRAY(new Color(112, 128, 144)),
    SNOW(new Color(255, 250, 250)),
    SPICY_PINK(new Color(255, 28, 174)),
    SPRING_GREEN(new Color(0, 255, 127)),
    STEEL_BLUE(new Color(70, 130, 180)),
    SUMMER_SKY(new Color(56, 176, 222)),
    TAN(new Color(210, 180, 140)),
    THISTLE(new Color(216, 191, 216)),
    TOMATO(new Color(255, 99, 71)),
    TURQUOISE(new Color(64, 224, 208)),
    VERY_LIGHT_GREY(new Color(205, 205, 205)),
    VIOLET(new Color(238, 130, 238)),
    VIOLET_RED(new Color(208, 32, 144)),
    WHEAT(new Color(245, 222, 179)),
    WHITE_SMOKE(new Color(245, 245, 245)),
    YELLOW_GREEN(new Color(154, 205, 50));

    private final Color color;

    Colors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
