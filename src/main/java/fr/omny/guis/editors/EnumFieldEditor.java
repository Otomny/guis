package fr.omny.guis.editors;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.backend.GuiListBuilder;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.guis.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class EnumFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		return field.getType().isEnum();
	}

	@Override
	public boolean allowNullValues() {
		return true;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		edit(0, player, toEdit, field, fieldData, onClose);
	}

	protected void edit(int page, Player player, Object toEdit, Field field, OField fieldData,
			Runnable onClose) {
		try {
			List<Object> enumValues = Arrays.asList(field.getType().getEnumConstants());
			Object selectedValue = ReflectionUtils.get(toEdit, field);
			var guiBuilder = new GuiListBuilder<>(
					Component.text(Utils.orString(fieldData.value(), field.getName()), NamedTextColor.GRAY), enumValues)
					.page(page).itemCreation(obj -> {
						boolean select = selectedValue == obj;
						return new GuiItemBuilder().icon(select ? Material.DIAMOND : Material.NAME_TAG)
								.name((select ? "§b" : "§7") + obj.toString()).breakLine()
								.click((p, slot, click) -> {
									// select this
									try {
										ReflectionUtils.set(toEdit, field, obj);
										edit(page, player, toEdit, field, fieldData, onClose);
									} catch (Exception e) {
										e.printStackTrace();
									}
									return true;
								});
					}).pageChange(newPage -> edit(newPage, player, toEdit, field, fieldData, onClose))
					.close(onClose);

			guiBuilder.open(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
