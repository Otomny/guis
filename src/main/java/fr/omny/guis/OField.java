package fr.omny.guis;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.Material;

import fr.omny.guis.attributes.ListOperation;
import fr.omny.guis.editors.stringifiers.DefaultStringifier;
import fr.omny.guis.editors.stringifiers.Stringifier;
import fr.omny.guis.utils.Utils;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OField {
	String value() default Utils.STRING_DEFAULT_VALUE;

	Material display() default Material.BOOK;

	String[] description() default {};

	Class<?> editor() default Object.class;

	Class<? extends Stringifier> stringifier() default DefaultStringifier.class;

	ListOperation[] listOperations() default {
			ListOperation.ADD, ListOperation.EDIT, ListOperation.REMOVE };
}
