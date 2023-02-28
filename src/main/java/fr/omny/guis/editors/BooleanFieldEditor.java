package fr.omny.guis.editors;

import java.lang.reflect.Field;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.attributes.Updateable;
import fr.omny.guis.backend.GuiBuilder;
import fr.omny.guis.backend.GuiItem;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.utils.Utils;

public class BooleanFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		return field.getType() == Boolean.class || field.getType() == boolean.class;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		GuiBuilder guiBuilder = new GuiBuilder(Utils.orString(fieldData.value(), field.getName()))
				.rows(3)
				.fillSide(fieldData.fillSide());

		try {
			field.setAccessible(true);
			boolean value = field.getBoolean(toEdit);
			guiBuilder.item(13, new GuiItemBuilder()
					.icon(value ? Material.LIME_CONCRETE : Material.RED_CONCRETE)
					.name("§7Value: " + (value ? "§aTrue" : "§cFalse"))
					.breakLine()
					.description("§7Click to set value to " + (value ? "§cFalse" : "§aTrue"))
					.click(() -> {
						try {
							field.setBoolean(toEdit, !value);
							if (toEdit instanceof Updateable updateable) {
								updateable.fieldUpdate(field);
								updateable.update();
							}
							edit(player, toEdit, field, fieldData, onClose);
						} catch (Exception e) {
							e.printStackTrace();
							onClose.run();
						}
					}).build());

			guiBuilder.item(9, GuiItem.back(onClose));
		} catch (Exception e) {
			e.printStackTrace();
			onClose.run();
		}

	}

	@Override
	public boolean allowNullValues() {
		return false;
	}

}
