package fr.omny.guis.editors.stringifiers;

import java.util.function.Function;

import fr.omny.guis.utils.ReflectionUtils;

public class DefaultStringifier implements Stringifier {

	public static Function<Object, String> STRINGIFIER = ReflectionUtils::string;

	@Override
	public String toString(Object object) {
		return STRINGIFIER.apply(object);
	}

}
