package fr.omny.guis.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class ReflectionUtils {

  /**
   *
   * @param field
   * @return True if the field is private
   */
  public static boolean isHidden(Field field) {
    return Modifier.isPrivate(field.getModifiers()) ||
        Modifier.isProtected(field.getModifiers());
  }

  /**
   * Helper function to edit field, and eliminate boiler plate of set accessible 
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

    access(field, () -> {
        field.set(field, fieldValue);
    });
  }
}
