package fr.omny.guis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.omny.guis.utils.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 *
 */
public @interface OClass {

  /**
   *
   * @return The title displayed in the player inventory
   */
  String value() default Utils.STRING_DEFAULT_VALUE;
}
