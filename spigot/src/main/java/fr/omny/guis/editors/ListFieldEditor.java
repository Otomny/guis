package fr.omny.guis.editors;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import fr.omny.guis.ListOperation;
import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.OGui;
import fr.omny.guis.attributes.Ordering;
import fr.omny.guis.attributes.Updateable;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.backend.GuiListBuilder;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.guis.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ListFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		if (!List.class.isAssignableFrom(field.getType()))
			return false;
		Class<?> klass = ReflectionUtils.getTypeOfListField(field);
		return klass.isAnnotationPresent(OClass.class);
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		this.edit(0, player, toEdit, field, fieldData, onClose);
	}

	protected void edit(int page, Player player, Object toEdit, Field field, OField fieldData,
			Runnable onClose) {

		Set<ListOperation> listOperations = Set.of(fieldData.listOperations());
		Class<?> klass = ReflectionUtils.getTypeOfListField(field);

		try {
			ReflectionUtils.access(field, () -> {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) field.get(toEdit);
				Ordering.processOrderingUnknown(fieldData.ordering(), list);
				var guiBuilder = new GuiListBuilder<>(
						Component.text(Utils.orString(fieldData.value(), field.getName()), NamedTextColor.GRAY), list)
						.page(page)
						.itemCreation(obj -> new GuiItemBuilder().name("§e" + klass.getSimpleName())
								.icon(fieldData.display()).breakLine()
								.descriptionLegacy("§7§oValue: §e" + ReflectionUtils.string(obj))
								.descriptionLegacy("§eLeft click to edit", "§cRight click to remove")
								.click((p, slot, click) -> {
									if (click == ClickType.LEFT) {
										OGui.open(player, obj,
												() -> {
													if (obj instanceof Updateable updateable) {
														updateable.update();
													}
													edit(page, player, toEdit, field, fieldData, onClose);
												});
									} else if (click == ClickType.RIGHT) {
										list.remove(obj);
										edit(page, player, toEdit, field, fieldData, onClose);
									}
									return true;
								}))
						.pageChange(newPage -> edit(newPage, player, toEdit, field, fieldData, onClose))
						.close(onClose);

				if (listOperations.contains(ListOperation.ADD)) {
					guiBuilder.items(GuiListBuilder::isLastPage,
							new GuiItemBuilder().name("§aAdd item").icon(Material.LIME_CONCRETE)
									.descriptionLegacy("", "§7Click here to add an item").click((z, slot, click) -> {
										try {
											final Object createdElement = klass.getConstructor().newInstance();

											OGui.open(player, createdElement, () -> {
												list.add(createdElement);
												if (toEdit instanceof Updateable updateable) {
													updateable.fieldUpdate(field);
												}
												edit(page, player, toEdit, field, fieldData, onClose);
											});
										} catch (InstantiationException | IllegalAccessException
												| IllegalArgumentException | InvocationTargetException
												| NoSuchMethodException | SecurityException e) {
											e.printStackTrace();
										}
										return true;
									}).build());
				}

				guiBuilder.open(player);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
