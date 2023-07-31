package fr.omny.guis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.omny.guis.utils.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
/**
 * Categorize a field as editable in a GUI
 */
public @interface OField {

	/**
	 * The displayed item title in the inventory
	 * 
	 * @return
	 */
	String value() default Utils.STRING_DEFAULT_VALUE;

	/**
	 * The displayed item type in the inventory
	 * 
	 * @return
	 */
	String display() default "BOOK";

	/**
	 * In case of opening inventory for field edition, return the material used to
	 * fill sides
	 * 
	 * @return
	 */
	String fillSide() default "WHITE_STAINED_GLASS_PANE";

	/**
	 * The displayed item lore in the inventory
	 * 
	 * @return
	 */
	String[] description() default {};

	/**
	 * Custom editor for the object type
	 * 
	 * @return
	 */
	Class<?> editor() default Class.class;

	/**
	 * The function to use in order to transform a string in an object
	 * to be displayed in the inventory
	 * 
	 * @return
	 */
	Class<?> stringifier() default Class.class;

	/**
	 * Operations permited for a list (empty = only viewing)
	 * 
	 * @return
	 */
	ListOperation[] listOperations() default {
			ListOperation.ADD, ListOperation.EDIT, ListOperation.REMOVE };

	/**
	 * Ordering of the displayed elements
	 * 
	 * @return
	 */
	Class<?> ordering() default Class.class;

	/**
	 * Provide a list where the object can be selected from
	 * 
	 * @return
	 */
	OProvider provider() default @OProvider(provider = AutomaticProvider.class);
}
