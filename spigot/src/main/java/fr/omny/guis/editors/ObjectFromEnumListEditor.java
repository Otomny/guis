package fr.omny.guis.editors;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;

public class ObjectFromEnumListEditor implements OFieldEditor{

	@Override
	public boolean accept(Field field) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		// TODO Auto-generated method stub
		
	}
	
}
