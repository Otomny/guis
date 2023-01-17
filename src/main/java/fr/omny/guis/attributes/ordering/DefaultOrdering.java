package fr.omny.guis.attributes.ordering;


import java.util.List;

import fr.omny.guis.attributes.Ordering;
import fr.omny.guis.editors.stringifiers.Stringifier;

public class DefaultOrdering implements Ordering {


	@Override
	public void sort(List<Object> list, Class<? extends Stringifier> stringifier) {}

}
