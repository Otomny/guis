package fr.omny.guis.editors.stringifiers;

import fr.omny.guis.utils.ReflectionUtils;

public class DefaultStringifier implements Stringifier{

	@Override
	public String toString(Object object) {
		return ReflectionUtils.string(object);
	}
	
}
