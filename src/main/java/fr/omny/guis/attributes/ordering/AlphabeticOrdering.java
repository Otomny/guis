package fr.omny.guis.attributes.ordering;


import java.util.Collections;
import java.util.List;

import fr.omny.guis.attributes.Ordering;
import fr.omny.guis.attributes.Titeable;
import fr.omny.guis.editors.stringifiers.Stringifier;

public class AlphabeticOrdering implements Ordering {

	@Override
	public void sort(List<Object> list, Class<? extends Stringifier> stringifier) {
		Collections.sort(list, (o1, o2) -> {
			if (o1 instanceof Titeable t1 && o2 instanceof Titeable t2) {
				var s1 = t1.title();
				var s2 = t2.title();
				return s1.compareTo(s2);
			} else {
				var s1 = o1 == null ? "null" : o1.toString();
				var s2 = o2 == null ? "null" : o2.toString();
				return s1.compareTo(s2);
			}
		});
	}

}
