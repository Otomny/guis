package fr.omny.guis.backend;

import org.bukkit.Material;
import org.bukkit.util.Consumer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GuiBaked {

	/**
	 * Open a GUI to choose enum values
	 * 
	 * @param <E>       The enum type
	 * @param enumClass The enum class
	 * @param callback  the callback invoked when selecting
	 * @return
	 */
	public static <E extends Enum<E>> GuiBuilder selectEnum(Class<? extends E> enumClass, Consumer<E> callback) {
		return selectEnum(enumClass, () -> {
		}, callback);
	}

	/**
	 * Open a GUI to choose enum values
	 * 
	 * @param <E>       The enum type
	 * @param enumClass The enum class
	 * @param callback  the callback invoked when selecting
	 * @return
	 */
	public static <E extends Enum<E>> GuiBuilder selectEnum(Class<? extends E> enumClass, Runnable onCancel,
			Consumer<E> callback) {
		GuiBuilder guiBuilder = new GuiBuilder(Component.text(enumClass.getSimpleName(), NamedTextColor.GOLD))
				.fillSide(Material.LIME_STAINED_GLASS_PANE)
				.rows(5);
		for (E value : enumClass.getEnumConstants()) {
			guiBuilder.item(new GuiItemBuilder()
					.icon(Material.BOOK)
					.name(value.toString())
					.breakLine()
					.descriptionLegacy("§7Click here to select")
					.click(() -> callback.accept(value))
					.build());
		}
		guiBuilder.item(9, GuiItem.back(onCancel));
		return guiBuilder;
	}

	private GuiBaked() {
	}

}
