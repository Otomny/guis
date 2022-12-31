package fr.omny.guis.utils;


import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;

/**
 * Utils class
 */
public class Utils {

	private Utils() {}

	/**
	 * Represent a string default value
	 */
	public static final String STRING_DEFAULT_VALUE = "__none__";

	/**
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static String orString(String value, String defaultValue) {
		return value.equals(Utils.STRING_DEFAULT_VALUE) ? defaultValue : value;
	}

	public static double round(double val) {
		return Math.round(val * 1000) / 1000;
	}

	/**
	 * @param text
	 * @return
	 */
	public static String replaceColor(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	/**
	 * @param texts
	 * @return
	 */
	public static List<String> replaceColor(List<String> texts) {
		return texts.stream().map(Utils::replaceColor).collect(Collectors.toList());
	}
}
