package fr.omny.guis.editors.providers;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface OProvider {
	
	Class<? extends ObjectInListProvider<?>> provider() default AutomaticProvider.class;

}
