package fr.omny.guis.editors;


import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;

public class AutomaticFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		return false;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		throw new RuntimeException("Error: can't use the AutomaticFieldEditor editor");
	}

}
