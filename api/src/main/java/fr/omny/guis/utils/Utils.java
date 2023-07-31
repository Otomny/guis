package fr.omny.guis.utils;

import java.util.Collection;
import java.util.Collections;

import net.kyori.adventure.text.Component;

/**
 * Utils class
 */
public class Utils {

	private Utils() {
	}

	/**
	 * Represent a string default value
	 */
	public static final String STRING_DEFAULT_VALUE = "__none__";

	/**
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isMutable(Collection<?> collection) {
		try {
			collection.addAll(Collections.emptyList());
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		}
	}

	/**
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static String orString(String value, String defaultValue) {
		return value.equals(Utils.STRING_DEFAULT_VALUE) ? defaultValue : value;
	}

	/**
	 * Ensure that the given class is a subclass of the given type
	 * 
	 * @param <T>   The type
	 * @param clazz The class
	 * @param type  The type
	 * @return The class
	 * @throws IllegalArgumentException If the class is not a subclass of the type
	 */
	public static <T> Class<? extends T> ensureClass(Class<?> clazz, Class<T> type) throws IllegalArgumentException {
		if (type.isAssignableFrom(clazz))
			return clazz.asSubclass(type);
		throw new IllegalArgumentException(clazz + " is not a subclass of " + type);
	}

	/**
	 * 
	 * 
	 * @param value        The value
	 * @param defaultValue The default value
	 * @return The value if not empty, else the default value
	 */
	public static Component orComponent(Component value, Component defaultValue) {
		return value.equals(Component.empty()) ? defaultValue : value;
	}

	/**
	 * @param val
	 * @return
	 */
	public static double round(double val) {
		return Math.round(val * 1000) / 1000;
	}

	public record Tuple2<U, V>(U key, V value) {
	}

	public record Tuple3<U, V, W>(U key, V value1, W value2) {
	}

	public record Tuple4<U, V, W, X>(U key, V value, W value2, X value3) {
	}
}
