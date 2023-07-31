package fr.omny.guis.editors.providers;


import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

import fr.omny.guis.ObjectInListProvider;

public class WorldListProvider implements ObjectInListProvider<World> {

	@Override
	public List<World> provide() {
		return Bukkit.getWorlds();
	}

	@Override
	public Class<World> type() {
		return World.class;
	}

}
