package fr.omny.guis.backend;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
	private boolean playerHead;
	private boolean hideEnchant;
	private boolean glow;
	private Optional<String> playerHeadName = Optional.empty();
	private Optional<UUID> playerHeadId = Optional.empty();

	protected GuiItem(GuiItemBuilder itemBuilder) {
		this.handler = Optional.of(itemBuilder.getHandler());
		// build item
		this.display = itemBuilder.getDisplay();
		this.playerHead = itemBuilder.isPlayerHead();
		this.playerHeadName = itemBuilder.getPlayerHeadName();
		this.playerHeadId = itemBuilder.getPlayerHeadId();

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

		// Apply enchantments
		for (var enchantment : itemBuilder.getEnchantments().entrySet()) {
			this.display.addUnsafeEnchantment(enchantment.getKey(), enchantment.getValue());
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

	public void setHead(ItemStack itemStack) {
		// this.display = itemStack;
		// // Get the meta
		// var meta = display.getItemMeta();
		// if (meta != null) {
		// // Set flags to neaten up the view
		// meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		// meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		// meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
		// if (this.hideEnchant || this.glow) {
		// meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		// }
		// display.setItemMeta(meta);
		// }
		SkullMeta providedSkullMeta = (SkullMeta) itemStack.getItemMeta();
		SkullMeta skullMeta = (SkullMeta) this.display.getItemMeta();
		skullMeta.setPlayerProfile(providedSkullMeta.getPlayerProfile());
		this.display.setItemMeta(skullMeta);
	}
}
