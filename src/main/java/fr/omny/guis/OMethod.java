package fr.omny.guis;


import org.bukkit.Material;

import fr.omny.guis.utils.Utils;

public @interface OMethod {

	/**
	 * @return The title displayed in the player inventory
	 */
	String value() default Utils.STRING_DEFAULT_VALUE;

	/**
	 * @return The icon to show in the player inventory
	 */
	Material icon() default Material.ANVIL;

	/**
	 * @return The description
	 */
	String[] description() default {};

	/**
	 * @return If this method can be called asynchronously
	 */
	boolean async() default false;

}
