package fr.omny.guis.backend;

import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@Getter
public class GuiItem {

	/**
	 * @param click
	 * @return
	 */
	public static GuiItem back(Runnable click) {
		return back(Optional.of(click));
	}

	/**
	 * @param click
	 * @return
	 */
	public static GuiItem back(Optional<Runnable> click) {
		return back(Component.text("Back", NamedTextColor.GRAY), click);
	}

	/**
	 * @param text
	 * @param click
	 * @return
	 */
	public static GuiItem back(Component text, Optional<Runnable> click) {
		return new GuiItemBuilder().name(text).icon(Material.ARROW).click(click)
				.build();
	}

	private ItemStack display;
	private Optional<GuiItemBuilder.OnClickHandler> handler = Optional.empty();

	protected GuiItem(GuiItemBuilder itemBuilder) {
		this.handler = Optional.of(itemBuilder.getHandler());
		// build item
		this.display = itemBuilder.getDisplay();

		ItemMeta itemMeta = this.display.getItemMeta();

		// Hide all attributes
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		itemMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);

		// Apply display name
		if (itemBuilder.getDisplayName() != null) {
			itemMeta.displayName(itemBuilder.getDisplayName());

		}
		// Apply description
		if (!itemBuilder.getDescription().isEmpty()) {
			itemMeta.lore(itemBuilder.getDescription());
		}
		// Apply hide enchants
		if (itemBuilder.isHideEnchant() || itemBuilder.isGlow()) {
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		this.display.setItemMeta(itemMeta);

	}

	protected void applyMeta(Consumer<ItemMeta> metaConsumer) {
		if (this.display != null) {
			ItemMeta meta = this.display.getItemMeta();
			metaConsumer.accept(meta);
			this.display.setItemMeta(meta);
		}
	}
}
