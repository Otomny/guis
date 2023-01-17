package fr.omny.guis.attributes;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.omny.guis.OGui;
import fr.omny.guis.attributes.ordering.DefaultOrdering;
import fr.omny.guis.editors.stringifiers.Stringifier;
import fr.omny.guis.editors.stringifiers.TiteableStringifier;
import fr.omny.guis.utils.Utils;

public interface Ordering {

	final static Map<Class<? extends Ordering>, Ordering> instances = new HashMap<>();

	static void processOrdering(List<Object> list) {
		processOrdering(DefaultOrdering.class, null);
	}

	static void processOrdering(Class<? extends Ordering> ordering, List<Object> list) {
		processOrdering(ordering, list, TiteableStringifier.class);
	}

	static void processOrdering(Class<? extends Ordering> ordering, List<Object> list, Class<? extends Stringifier> stringifier) {
		if (!Utils.isMutable(list)) {
			OGui.getPlugin().getLogger().warning("Can't sort list because it is unmutable");
			return;
		}
		if (instances.containsKey(ordering)) {
			instances.get(ordering).sort(list, stringifier);
		} else {
			try {
				var instance = ordering.getConstructor().newInstance();
				instances.put(ordering, instance);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @param list
	 * @param stringifier
	 */
	void sort(List<Object> list, Class<? extends Stringifier> stringifier);

}
