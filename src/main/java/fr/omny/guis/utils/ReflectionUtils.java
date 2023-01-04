package fr.omny.guis.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ReflectionUtils {

	/**
	 * @param field
	 * @return True if the field is private
	 */
	public static boolean isHidden(Field field) {
		return Modifier.isPrivate(field.getModifiers()) || Modifier.isProtected(field.getModifiers());
	}

	/**
	 * @return
	 */
	public static <T> Optional<T> get(Object obj, String fieldName) {
		var klass = obj.getClass();
		try {
			var field = klass.getDeclaredField(fieldName);
			if (isHidden(field)) {
				field.setAccessible(true);
			}
			@SuppressWarnings("unchecked")
			T value = (T) field.get(obj);
			if (isHidden(field)) {
				field.setAccessible(false);
			}
			return Optional.of(value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e) {
			return Optional.empty();
		}
	}

	/**
	 * @param owner
	 * @param field
	 * @return
	 */
	public static Object get(Object owner, Field field) {
		Objects.requireNonNull(owner);
		Objects.requireNonNull(field);

		var hidden = isHidden(field);
		if (hidden) {
			field.setAccessible(true);
		}
		try {
			Object fieldValue = field.get(owner);
			return fieldValue;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (hidden) {
			field.setAccessible(false);
		}
		return null;
	}

	/**
	 * @return
	 */
	public static Optional<Field> getField(Object obj, String fieldName) {
		var klass = obj.getClass();
		try {
			return Optional.of(klass.getField(fieldName));
		} catch (NoSuchFieldException | SecurityException e) {
			return Optional.empty();
		}
	}

	/**
	 * Get the tpye of the list hold by the field
	 * 
	 * @param field The field
	 * @return The type of the list
	 */
	public static Class<?> getTypeOfListField(Field field) {
		return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
	}

	/**
	 * Helper function to edit field, and eliminate boiler plate of set accessible
	 * 
	 * @param field
	 * @param task
	 * @throws Exception
	 */
	public static void access(Field field, ThrowableTask task) throws Exception {
		Objects.requireNonNull(task);
		Objects.requireNonNull(field);

		var hidden = isHidden(field);
		if (hidden) {
			field.setAccessible(true);
		}
		task.work();
		if (hidden) {
			field.setAccessible(false);
		}
	}

	public static void set(Object owner, Field field, Object fieldValue) throws Exception {
		Objects.requireNonNull(owner);
		Objects.requireNonNull(field);
		Objects.requireNonNull(fieldValue);

		access(field, () -> field.set(owner, fieldValue));
	}

	/**
	 * Stringify an object with reflection
	 * 
	 * @param object the object
	 * @return the string
	 */
	public static String string(Object object) {
		// Null cases
		if (object == null)
			return "null";
		Class<?> klass = object.getClass();
		// Enum case
		if (klass.isEnum()) {
			return object.toString();
		}

		List<Class<?>> primitives = List.of(Integer.class, Double.class, Float.class, Long.class,
				Character.class, Byte.class, Short.class, String.class, StringBuffer.class,
				StringBuilder.class);
		// "Primitives" type
		if (primitives.stream().anyMatch(p -> p.isAssignableFrom(klass))) {
			return object.toString();
		}

		// If it's a collection, only display size and implementation
		if (Collection.class.isAssignableFrom(klass)) {
			@SuppressWarnings("unchecked")
			Collection<Object> list = (List<Object>) object;
			return "Size: " + list.size() + ", Type : " + klass.getSimpleName();
		}

		// Generic "json"-like display
		var str = new StringBuilder("{");
		for (var field : klass.getDeclaredFields()) {
			if (Modifier.isFinal(field.getModifiers()))
				continue;
			try {
				access(field, () -> str.append(field.getName() + ": " + field.get(object) + ", "));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (str.length() > 1)
			str.replace(str.length() - 2, str.length(), "");

		str.append("}");
		return str.toString();
	}
}
