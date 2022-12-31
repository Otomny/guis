package fr.omny.guis.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
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

		access(field, () -> field.set(field, fieldValue));
	}
}
