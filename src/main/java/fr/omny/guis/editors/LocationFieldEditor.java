package fr.omny.guis.editors;


import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.utils.ReflectionUtils;

public class LocationFieldEditor implements OFieldEditor {

	@Override
	public boolean allowNullValues() {
		return true;
	}

	@Override
	public boolean accept(Field field) {
		return field.getType() == Location.class;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		var location = player.getLocation();
		try {
			ReflectionUtils.access(field, () -> {
				field.set(toEdit, location);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		onClose.run();
	}

}
