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
public class IntegerFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		Class<?> klass = field.getType();
		return klass == int.class || klass == Integer.class;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		new SignGUIBuilder(FunctionalUtils::isInt).onClose(signValue -> {
			try {
				ReflectionUtils.access(field, () -> {
					field.setInt(toEdit, Integer.parseInt(signValue));
					if (toEdit instanceof Updateable updateable) {
						updateable.fieldUpdate(field);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).title("Set " + field.getName()).open(player);
	}

}
