package fr.omny.guis;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.Material;

import fr.omny.guis.utils.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OMethod {

	/**
	 * @return The title displayed in the player inventory
	 */
	String value() default Utils.STRING_DEFAULT_VALUE;

	/**
	 * @return The icon to show in the player inventory
	 */
	Material icon() default Material.NAME_TAG;

	/**
	 * @return The description
	 */
	String[] description() default {};

	/**
	 * @return If this method can be called asynchronously
	 */
	boolean async() default false;

}
