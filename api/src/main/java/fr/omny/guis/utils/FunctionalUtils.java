package fr.omny.guis.utils;

/**
 * Helpers for functional programming in java
 */
public class FunctionalUtils {

	private FunctionalUtils() {}

	/**
	 * 
	 */
	public static void voidReturn() {

	}

	/**
	 * Test if a string is a numeric value
	 * @param str String
	 * @return True if str is numeric, false otherwise
	 */
	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Test if a string is an integer value
	 * @param str String
	 * @return True if str is integer, false otherwise
	 */
	public static boolean isInt(String str) {
		if (str == null) {
			return false;
		}
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
