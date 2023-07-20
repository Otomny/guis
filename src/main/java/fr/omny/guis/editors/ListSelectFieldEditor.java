package fr.omny.guis.editors;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.attributes.Ordering;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.backend.GuiListBuilder;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.guis.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ListSelectFieldEditor implements OFieldEditor {

	@Override
	public boolean allowNullValues() {
		return false;
	}

	@Override
	public boolean accept(Field field) {
		if (!List.class.isAssignableFrom(field.getType()))
			return false;
		Class<?> klass = ReflectionUtils.getTypeOfListField(field);
		return ObjectInListFieldEditor.findProvider(klass).isPresent();
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		edit(0, player, toEdit, field, fieldData, onClose);
	}

	private void edit(int page, Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		Class<?> type = field.getType();
		var optionalProvider = ObjectInListFieldEditor.findProvider(type);
		if (optionalProvider.isEmpty()) {
			player.sendMessage("§cNo provider found for type " + type.getSimpleName());
			onClose.run();
			return;
		}
		var provider = optionalProvider.get();
		// Should be a list due to check at line 26
		@SuppressWarnings("unchecked")
		List<Object> listValue = (List<Object>) ReflectionUtils.get(toEdit, field);
		Ordering.processOrdering(fieldData.ordering(), listValue);
		new GuiListBuilder<>(Component.text(Utils.orString(fieldData.value(), field.getName()), NamedTextColor.GRAY),
				provider.provide()).page(page).itemCreation(obj -> {
					boolean selected = listValue.contains(obj);
					return new GuiItemBuilder().name((selected ? "§b" : "§e") + provider.asString(obj))
							.icon(selected ? Material.DIAMOND : fieldData.display()).breakLine()
							.descriptionLegacy("§7§oValue: §e" + provider.asString(obj)).click((p, slot, click) -> {
								try {
									if (selected) {
										listValue.remove(obj);
									} else {
										listValue.add(obj);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								edit(page, player, toEdit, field, fieldData, onClose);
								return true;
							});
				}).pageChange(newPage -> edit(newPage, player, toEdit, field, fieldData, onClose)).close(onClose).open(player);
	}
}
