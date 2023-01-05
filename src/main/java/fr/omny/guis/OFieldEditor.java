package fr.omny.guis;


import java.lang.reflect.Field;

import org.bukkit.entity.Player;

/**
 * Represent an editor for a type
 */
public interface OFieldEditor {

	/**
	 * Assert if a type can be edited with this Editor
	 * 
	 * @param klass The field of the object
	 * @return True if the type is editable, False otherwise
	 */
	boolean accept(Field field);

	/**
	 * 
	 * @return If this editor allow null values by default
	 */
	default boolean allowNullValues(){
		return false;
	}

	/**
	 * @param player    The player to show inventory to
	 * @param toEdit    The objet to edit
	 * @param field     The field to edit
	 * @param fieldData The annotation data of the field to edit
	 * @param onClose   Task to execute when the player close the inventory
	 */
	void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose);

}
