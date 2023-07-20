package fr.omny.guis.editors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.attributes.Ordering;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.backend.GuiListBuilder;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.guis.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ListEnumSelectFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		if (!List.class.isAssignableFrom(field.getType()))
			return false;
		Class<?> klass = ReflectionUtils.getTypeOfListField(field);
		return klass.isEnum();
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		edit(0, player, toEdit, field, fieldData, onClose);
	}

	protected void edit(int page, Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		try {
			Class<?> enumClass = ReflectionUtils.getTypeOfListField(field);
			List<Object> enumValues = new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
			Ordering.processOrdering(fieldData.ordering(), enumValues);
			@SuppressWarnings("unchecked")
			List<Object> selected = (List<Object>) ReflectionUtils.get(toEdit, field);
			var guiBuilder = new GuiListBuilder<>(
					Component.text(Utils.orString(fieldData.value(), field.getName()), NamedTextColor.GRAY), enumValues)
					.page(page).itemCreation(obj -> {
						boolean contains = selected.contains(obj);
						return new GuiItemBuilder().icon(contains ? Material.DIAMOND : Material.NAME_TAG)
								.name((contains ? "§b" : "§7") + obj.toString()).breakLine()
								.descriptionLegacy("§7- Right click to add", "§7- Left click to remove").click((p, slot, click) -> {
									if (click == ClickType.LEFT) {
										if (contains) {
											selected.remove(obj);
										}
									} else if (click == ClickType.RIGHT) {
										if (!contains) {
											selected.add(obj);
										}
									}
									edit(page, player, toEdit, field, fieldData, onClose);
									return true;
								});
					}).pageChange(newPage -> edit(newPage, player, toEdit, field, fieldData, onClose)).close(onClose);

			guiBuilder.open(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
