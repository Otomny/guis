package fr.omny.guis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.Material;

import fr.omny.guis.utils.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/**
 * Apply this annotation to any object that you want to edit with a GUI ingame
 * 
 */
public @interface OClass {

  /**
   *
   * @return The title displayed in the player inventory
   */
  String value() default Utils.STRING_DEFAULT_VALUE;

	/**
	 * 
	 * @return The material to fill the borders of the edit inventory with
	 */ 
	Material fillSide() default Material.BLUE_STAINED_GLASS_PANE;
}
