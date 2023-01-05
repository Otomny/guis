package fr.omny.guis.editors;


import java.lang.reflect.Field;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.backend.Gui;
import fr.omny.guis.backend.GuiBuilder;
import fr.omny.guis.backend.GuiBuilder.InventoryHandler;
import fr.omny.guis.backend.GuiItem;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.utils.ReflectionUtils;

@OMainEditor
public class MaterialFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		if (!field.getType().isEnum())
			return false;
		return field.getType() == Material.class;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		var currentValue = Optional.ofNullable((Material) ReflectionUtils.get(toEdit, field));
		new GuiBuilder("Set " + field.getName()).fillSide(Material.BLUE_STAINED_GLASS_PANE)
				.item(13, new GuiItemBuilder().icon(currentValue.orElse(Material.STONE)).build())
				.handler(new InventoryHandler() {

					@Override
					public void onOpen(Gui gui, Player player) {}

					@Override
					public void onClose(Gui gui, Player player, InventoryCloseEvent event) {}

					@Override
					public void onClick(Gui gui, Player player, InventoryClickEvent event) {
						int slot = event.getRawSlot();
						event.setCancelled(true);
						if (slot >= 27) {
							if (event.getCurrentItem() != null) {
								Material type = event.getCurrentItem().getType();
								try {
									field.set(toEdit, type);
									edit(player, toEdit, field, fieldData, onClose);
								} catch (IllegalArgumentException | IllegalAccessException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}).item(9, GuiItem.back(onClose)).open(player);

	}

}
