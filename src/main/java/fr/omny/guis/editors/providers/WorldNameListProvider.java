package fr.omny.guis.editors.providers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldNameListProvider implements ObjectInListProvider<String> {

	@Override
	public List<String> provide() {
		return Bukkit.getWorlds().stream().map(World::getName).toList();
	}

	@Override
	public Class<String> type() {
		return String.class;
	}

}
