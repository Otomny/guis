package fr.omny.guis.backend;


import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.omny.guis.utils.Utils;
import lombok.Getter;

@Getter
public class GuiItem {

	/**
	 * @param click
	 * @return
	 */
	public static GuiItem back(Runnable click) {
		return back("&7Back", click);
	}

	/**
	 * @param text
	 * @param click
	 * @return
	 */
	public static GuiItem back(String text, Runnable click) {
		return new GuiItemBuilder().name(Utils.replaceColor(text)).icon(Material.ARROW).click(click)
				.build();
	}

	private ItemStack display;
	private Optional<GuiItemBuilder.OnClickHandler> handler = Optional.empty();

	protected GuiItem(GuiItemBuilder itemBuilder) {
		this.handler = Optional.of(itemBuilder.getHandler());
		// build item
		this.display = itemBuilder.getDisplay();

		// Hide all attributes
		applyMeta(itemMeta -> {
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);

			itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		});

		// Apply display name
		if (itemBuilder.getDisplayName() != null) {
			applyMeta(itemMeta -> {
				itemMeta.setDisplayName(Utils.replaceColor(itemBuilder.getDisplayName()));
				itemMeta.setLocalizedName(Utils.replaceColor(itemBuilder.getDisplayName()));
			});
		}
		// Apply description
		if (!itemBuilder.getDescription().isEmpty()) {
			applyMeta(itemMeta -> itemMeta.setLore(Utils.replaceColor(itemBuilder.getDescription())));
		}
		// Apply hide enchants
		if (itemBuilder.isHideEnchant() || itemBuilder.isGlow()) {
			applyMeta(itemMeta -> itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
		}
	}

	protected void applyMeta(Consumer<ItemMeta> metaConsumer) {
		if (this.display != null) {
			ItemMeta meta = this.display.getItemMeta();
			metaConsumer.accept(meta);
			this.display.setItemMeta(meta);
		}
	}
}
