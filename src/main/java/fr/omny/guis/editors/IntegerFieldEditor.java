package fr.omny.guis.editors;


import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;

@OMainEditor
public class IntegerFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		Class<?> klass = field.getType();
		return klass == int.class || klass == Integer.class;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		// TODO Auto-generated method stub

	}

}
