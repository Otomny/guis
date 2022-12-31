package fr.omny.guis.editors;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.OGui;
import fr.omny.guis.attributes.Itemable;
import fr.omny.guis.attributes.ListOperation;
import fr.omny.guis.backend.GuiBuilder;
import fr.omny.guis.backend.GuiItem;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.backend.GuiListBuilder;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.guis.utils.Utils;

public class ListOClassFieldEditor implements OFieldEditor {

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

	@SuppressWarnings("unchecked")
	protected void edit(int page, Player player, Object toEdit, Field field, OField fieldData,
			Runnable onClose) {

		try {
			ReflectionUtils.access(field, () -> {
				List<Object> list = (List<Object>) field.get(toEdit);
				new GuiListBuilder<>(Utils.replaceColor(Utils.orString(fieldData.value(), "&e" + field.getName())), list)
					.page(page)
					.pageChange(newPage -> {
						// something
					})
					.itemCreation(obj -> {
						return null;
					})
					.close(onClose)
					.open(player);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		final int ITEM_PAGE_COUNT = 3 * 7;
		Set<ListOperation> listOperations = Set.of(fieldData.listOperations());
		Class<?> klass = ReflectionUtils.getTypeOfListField(field);
		try {
			field.setAccessible(true);
			List<Object> list = (List<Object>) field.get(toEdit);
			field.setAccessible(false);

			int maxPageCount = list.size() / ITEM_PAGE_COUNT;
			if (page < 0) {
				edit(0, player, toEdit, field, fieldData, onClose);
			} else if (page > maxPageCount) {
				edit(maxPageCount, player, toEdit, field, fieldData, onClose);
			}
			int startPage = page * ITEM_PAGE_COUNT;
			int endPage = Math.min(list.size(), (page + 1) * ITEM_PAGE_COUNT);

			GuiBuilder guiBuilder = new GuiBuilder(
					Utils.replaceColor(Utils.orString(fieldData.value(), "&e" + field.getName()))).rows(5)
							.fillSide(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE));

			for (int i = startPage; i < endPage; i++) {
				final int index = i;
				var objectAt = list.get(index);
				String toString = objectAt == null ? "null" : objectAt.toString();

				GuiItemBuilder guiItemBuilder = objectAt instanceof Itemable item ? item.item()
						: new GuiItemBuilder().icon(new ItemStack(Material.PAPER, i + 1));

				guiBuilder.item(guiItemBuilder
						.name("§eÉlèment n°" + (i + 1)).description("", "§7Contenu : " + toString,
								"§c- Clique droit pour supprimer", "§a- Clique gauche pour éditer")
						.click((z, slot, click) -> {
							if (click == ClickType.LEFT) {
								// Edit
								OGui.open(player, list.get(index),
										() -> edit(page, player, toEdit, field, fieldData, onClose));
								return true;
							}
							if (click == ClickType.RIGHT) {
								throw new UnsupportedOperationException("Not implemented");
								// Supprimer
								// GuiAcceptItem.openAccept("§cSupprimer cet élément ?", player, () ->
								// // Cancel
								// openGui(player), () -> {
								// // Accept
								// list.remove(index);
								// openGui(player, page);
								// });
								// return true;
							}
							return true;
						}).build());
			}

			if (listOperations.contains(ListOperation.ADD)) {
				guiBuilder
						.item(new GuiItemBuilder().name("§aAjouter un élément").icon(Material.LIME_CONCRETE)
								.description("", "§7Cliquez pour ajouter un élément").click((z, slot, click) -> {
									// Add
									try {
										final Object createdElement = klass.getConstructor().newInstance();

										OGui.open(player, createdElement, () -> {
											list.add(createdElement);
											edit(page, player, toEdit, field, fieldData, onClose);
										});
									} catch (InstantiationException | IllegalAccessException
											| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
											| SecurityException e) {
										e.printStackTrace();
									}
									return true;
								}).build());
			}

			if (page > 0) {
				guiBuilder.item(39, new GuiItemBuilder().name("§7Previous page").icon(Material.ARROW)
						.click(() -> edit(page - 1, player, toEdit, field, fieldData, onClose)).build());
			}
			if (page < maxPageCount) {
				guiBuilder.item(41, new GuiItemBuilder().icon(Material.ARROW).name("§7Next page")
						.click(() -> edit(page + 1, player, toEdit, field, fieldData, onClose)).build());
			}

			guiBuilder.item(9, GuiItem.back(onClose));

			guiBuilder.open(player);
		} catch (Exception e) {
			e.printStackTrace();
			onClose.run();
		}

	}

}
