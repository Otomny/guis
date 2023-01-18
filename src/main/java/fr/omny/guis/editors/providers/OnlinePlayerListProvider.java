package fr.omny.guis.editors.providers;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OnlinePlayerListProvider implements ObjectInListProvider<Player> {

	@Override
	public List<Player> provide() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	@Override
	public Class<Player> type() {
		return Player.class;
	}

}
