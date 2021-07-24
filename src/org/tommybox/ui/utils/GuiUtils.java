package org.tommybox.ui.utils;

import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

public class GuiUtils {
	private static Map<String, String> colorsMap = new HashMap<String, String>();
	static {
		colorsMap.put("aliceblue", "#f0f8ff");
		colorsMap.put("antiquewhite", "#faebd7");
		colorsMap.put("aqua", "#00ffff");
		colorsMap.put("aquamarine", "#7fffd4");
		colorsMap.put("azure", "#f0ffff");
		colorsMap.put("beige", "#f5f5dc");
		colorsMap.put("bisque", "#ffe4c4");
		colorsMap.put("black", "#000000");
		colorsMap.put("blanchedalmond", "#ffebcd");
		colorsMap.put("blue", "#0000ff");
		colorsMap.put("blueviolet", "#8a2be2");
		colorsMap.put("brown", "#a52a2a");
		colorsMap.put("burlywood", "#deb887");
		colorsMap.put("cadetblue", "#5f9ea0");
		colorsMap.put("chartreuse", "#7fff00");
		colorsMap.put("chocolate", "#d2691e");
		colorsMap.put("coral", "#ff7f50");
		colorsMap.put("cornflowerblue", "#6495ed");
		colorsMap.put("cornsilk", "#fff8dc");
		colorsMap.put("crimson", "#dc143c");
		colorsMap.put("cyan", "#00ffff");
		colorsMap.put("darkblue", "#00008b");
		colorsMap.put("darkcyan", "#008b8b");
		colorsMap.put("darkgoldenrod", "#b8860b");
		colorsMap.put("darkgray", "#a9a9a9");
		colorsMap.put("darkgreen", "#006400");
		colorsMap.put("darkkhaki", "#bdb76b");
		colorsMap.put("darkmagenta", "#8b008b");
		colorsMap.put("darkolivegreen", "#556b2f");
		colorsMap.put("darkorange", "#ff8c00");
		colorsMap.put("darkorchid", "#9932cc");
		colorsMap.put("darkred", "#8b0000");
		colorsMap.put("darksalmon", "#e9967a");
		colorsMap.put("darkseagreen", "#8fbc8f");
		colorsMap.put("darkslateblue", "#483d8b");
		colorsMap.put("darkslategray", "#2f4f4f");
		colorsMap.put("darkturquoise", "#00ced1");
		colorsMap.put("darkviolet", "#9400d3");
		colorsMap.put("deeppink", "#ff1493");
		colorsMap.put("deepskyblue", "#00bfff");
		colorsMap.put("dimgray", "#696969");
		colorsMap.put("dodgerblue", "#1e90ff");
		colorsMap.put("firebrick", "#b22222");
		colorsMap.put("floralwhite", "#fffaf0");
		colorsMap.put("forestgreen", "#228b22");
		colorsMap.put("fuchsia", "#ff00ff");
		colorsMap.put("gainsboro", "#dcdcdc");
		colorsMap.put("ghostwhite", "#f8f8ff");
		colorsMap.put("gold", "#ffd700");
		colorsMap.put("goldenrod", "#daa520");
		colorsMap.put("gray", "#808080");
		colorsMap.put("green", "#008000");
		colorsMap.put("greenyellow", "#adff2f");
		colorsMap.put("honeydew", "#f0fff0");
		colorsMap.put("hotpink", "#ff69b4");
		colorsMap.put("indianred", "#cd5c5c");
		colorsMap.put("indigo", "#4b0082");
		colorsMap.put("ivory", "#fffff0");
		colorsMap.put("khaki", "#f0e68c");
		colorsMap.put("lavender", "#e6e6fa");
		colorsMap.put("lavenderblush", "#fff0f5");
		colorsMap.put("lawngreen", "#7cfc00");
		colorsMap.put("lemonchiffon", "#fffacd");
		colorsMap.put("lightblue", "#add8e6");
		colorsMap.put("lightcoral", "#f08080");
		colorsMap.put("lightcyan", "#e0ffff");
		colorsMap.put("lightgoldenrodyellow", "#fafad2");
		colorsMap.put("lightgreen", "#90ee90");
		colorsMap.put("lightgrey", "#d3d3d3");
		colorsMap.put("lightpink", "#ffb6c1");
		colorsMap.put("lightsalmon", "#ffa07a");
		colorsMap.put("lightseagreen", "#20b2aa");
		colorsMap.put("lightskyblue", "#87cefa");
		colorsMap.put("lightslategray", "#778899");
		colorsMap.put("lightsteelblue", "#b0c4de");
		colorsMap.put("lightyellow", "#ffffe0");
		colorsMap.put("lime", "#00ff00");
		colorsMap.put("limegreen", "#32cd32");
		colorsMap.put("linen", "#faf0e6");
		colorsMap.put("magenta", "#ff00ff");
		colorsMap.put("maroon", "#800000");
		colorsMap.put("mediumaquamarine", "#66cdaa");
		colorsMap.put("mediumblue", "#0000cd");
		colorsMap.put("mediumorchid", "#ba55d3");
		colorsMap.put("mediumpurple", "#9370db");
		colorsMap.put("mediumseagreen", "#3cb371");
		colorsMap.put("mediumslateblue", "#7b68ee");
		colorsMap.put("mediumspringgreen", "#00fa9a");
		colorsMap.put("mediumturquoise", "#48d1cc");
		colorsMap.put("mediumvioletred", "#c71585");
		colorsMap.put("midnightblue", "#191970");
		colorsMap.put("mintcream", "#f5fffa");
		colorsMap.put("mistyrose", "#ffe4e1");
		colorsMap.put("moccasin", "#ffe4b5");
		colorsMap.put("navajowhite", "#ffdead");
		colorsMap.put("navy", "#000080");
		colorsMap.put("oldlace", "#fdf5e6");
		colorsMap.put("olive", "#808000");
		colorsMap.put("olivedrab", "#6b8e23");
		colorsMap.put("orange", "#ffa500");
		colorsMap.put("orangered", "#ff4500");
		colorsMap.put("orchid", "#da70d6");
		colorsMap.put("palegoldenrod", "#eee8aa");
		colorsMap.put("palegreen", "#98fb98");
		colorsMap.put("paleturquoise", "#afeeee");
		colorsMap.put("palevioletred", "#db7093");
		colorsMap.put("papayawhip", "#ffefd5");
		colorsMap.put("peachpuff", "#ffdab9");
		colorsMap.put("peru", "#cd853f");
		colorsMap.put("pink", "#ffc0cb");
		colorsMap.put("plum", "#dda0dd");
		colorsMap.put("powderblue", "#b0e0e6");
		colorsMap.put("purple", "#800080");
		colorsMap.put("red", "#ff0000");
		colorsMap.put("rosybrown", "#bc8f8f");
		colorsMap.put("royalblue", "#4169e1");
		colorsMap.put("saddlebrown", "#8b4513");
		colorsMap.put("salmon", "#fa8072");
		colorsMap.put("sandybrown", "#f4a460");
		colorsMap.put("seagreen", "#2e8b57");
		colorsMap.put("seashell", "#fff5ee");
		colorsMap.put("sienna", "#a0522d");
		colorsMap.put("silver", "#c0c0c0");
		colorsMap.put("skyblue", "#87ceeb");
		colorsMap.put("slateblue", "#6a5acd");
		colorsMap.put("slategray", "#708090");
		colorsMap.put("snow", "#fffafa");
		colorsMap.put("springgreen", "#00ff7f");
		colorsMap.put("steelblue", "#4682b4");
		colorsMap.put("tan", "#d2b48c");
		colorsMap.put("teal", "#008080");
		colorsMap.put("thistle", "#d8bfd8");
		colorsMap.put("tomato", "#ff6347");
		colorsMap.put("turquoise", "#40e0d0");
		colorsMap.put("violet", "#ee82ee");
		colorsMap.put("wheat", "#f5deb3");
		colorsMap.put("white", "#ffffff");
		colorsMap.put("whitesmoke", "#f5f5f5");
		colorsMap.put("yellow", "#ffff00");
		colorsMap.put("yellowgreen", "#9acd32");
	}

	/**
	 * BMP (Windows or OS/2 Bitmap)
	 * ICO (Windows Icon)
	 * JPEG
	 * GIF
	 * PNG
	 * TIFF
	 */
	private static final String[] SUPPORTED_IMAGE_FORMATS = new String[] { "bmp", "ico", "jpeg", "jpg", "gif", "png", "tiff" };

	public static boolean isImageFormatSupported(String type) {
		for (String inageFormat : SUPPORTED_IMAGE_FORMATS)
			if (type.toLowerCase(Locale.ENGLISH).contains(inageFormat))
				return true;
		return false;
	}

	/**
	 * Converts a hex string to a color.
	 * 
	 * @param rgba (E.g. #63ECB1FC or #s63ECB1)
	 * @return Color
	 */
	public static Color rgbaToSwtColorFromIntArray(int[] rgba) {
		switch (rgba.length) {
		case 3:
			return new Color( //
					rgba[0], //
					rgba[1], //
					rgba[2]);//
		case 4:
			return new Color( //
					rgba[0], //
					rgba[1], //
					rgba[2], //
					rgba[3]);//
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Converts a hex string to a color.
	 * 
	 * @param rgba (E.g. #63ECB1FC or #63ECB1)
	 * @return Color
	 */
	public static Color rgbaToSwtColorFromHtmlHex(String rgba) {
		switch (rgba.length()) {
		case 7:
			return new Color( //
					Integer.valueOf(rgba.substring(1, 3), 16), //
					Integer.valueOf(rgba.substring(3, 5), 16), //
					Integer.valueOf(rgba.substring(5, 7), 16));//
		case 9:
			return new Color( //
					Integer.valueOf(rgba.substring(1, 3), 16), //
					Integer.valueOf(rgba.substring(3, 5), 16), //
					Integer.valueOf(rgba.substring(5, 7), 16), //
					Integer.valueOf(rgba.substring(7, 9), 16));//
		}
		return new Color(0, 0, 0, 0); // the default
	}

	/**
	 * Converts a hex string to a color.
	 * 
	 * @param rgba (E.g. #63ECB1FC or #63ECB1)
	 * @return Color
	 */
	public static Color rgbaToSwtColorFromHtmlColorName(String colorName) {
		return rgbaToSwtColorFromHtmlHex(colorsMap.get(colorName));
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isFullScreenSupported() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported();
	}

}
