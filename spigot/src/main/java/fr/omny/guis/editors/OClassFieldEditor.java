package fr.omny.guis.editors;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.OGui;
import fr.omny.guis.utils.ReflectionUtils;

public class OClassFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		Class<?> klass = field.getType();
		return klass.isAnnotationPresent(OClass.class);
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		Object obj = ReflectionUtils.get(toEdit, field);
		OGui.open(player, obj, onClose);
	}

	@Override
	public int priority() {
		return 5;
	}

}
