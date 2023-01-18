package fr.omny.guis.editors.providers;

import java.util.List;

public class AutomaticProvider implements ObjectInListProvider<Object>{

	@Override
	public List<Object> provide() {
		return List.of();
	}

	@Override
	public Class<Object> type() {
		return Object.class;
	}
	
}
