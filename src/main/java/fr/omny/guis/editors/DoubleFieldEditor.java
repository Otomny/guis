package fr.omny.guis.editors;


import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.attributes.Updateable;
import fr.omny.guis.backend.sign.SignGUIBuilder;
import fr.omny.guis.utils.FunctionalUtils;
import fr.omny.guis.utils.ReflectionUtils;

@OMainEditor
public class DoubleFieldEditor implements OFieldEditor {
	@Override
	public boolean accept(Field field) {
		Class<?> klass = field.getType();
		return klass == double.class || klass == Double.class;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		new SignGUIBuilder(FunctionalUtils::isNumeric).onClose(signValue -> {
			try {
				ReflectionUtils.access(field, () -> {
					field.setDouble(toEdit, Double.parseDouble(signValue));
					if (toEdit instanceof Updateable updateable) {
						updateable.fieldUpdate(field);
					}
				});
				onClose.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).title("Set " + field.getName()).open(player);
	}
}
