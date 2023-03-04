package fr.omny.guis.backend;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;


@Getter
/**
 * 
 */
public class GuiItemBuilder implements Cloneable {

	private ItemStack display = new ItemStack(Material.STONE);
	private OnClickHandler handler = (player, slot, click) -> true;
	private List<String> description = new ArrayList<>();
	private String displayName;
	private boolean hideEnchant = false;
	private boolean glow = false;

	/**
	 * 
	 */
	public GuiItemBuilder() {}

	private GuiItemBuilder(GuiItemBuilder guiItemBuilder) {
		this.display = guiItemBuilder.display.clone();
		this.handler = guiItemBuilder.handler;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public GuiItemBuilder icon(Material type) {
		this.display = new ItemStack(type);
		return this;
	}

	/**
	 * 
	 * @param glow
	 * @return
	 */
	public GuiItemBuilder glow(boolean glow) {
		this.glow = glow;
		return this;
	}

	/**
	 * 
	 * @param hideEnchant
	 * @return
	 */
	public GuiItemBuilder hideEnchantments(boolean hideEnchant) {
		this.hideEnchant = hideEnchant;
		return this;
	}

	/**
	 * 
	 * @param itemStack
	 * @return
	 */
	public GuiItemBuilder icon(ItemStack itemStack) {
		this.display = new ItemStack(itemStack);
		return this;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public GuiItemBuilder name(String name) {
		this.displayName = name;
		return this;
	}

	/**
	 * 
	 * @return
	 */
	public GuiItemBuilder breakLine() {
		return description("");
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public GuiItemBuilder description(String... description) {
		return description(List.of(description));
	}

	/**
	 * 
	 * @param description
	 * @return
	 */
	public GuiItemBuilder description(List<String> description) {
		this.description.addAll(description);
		return this;
	}

/**
	 * 
	 * @param condition
	 * @param description
	 * @return
	 */
	public GuiItemBuilder description(boolean condition, String... description) {
		return description(condition, List.of(description));
	}

	/**
	 * 
	 * @param condition
	 * @param description
	 * @return
	 */
	public GuiItemBuilder description(boolean condition, List<String> description) {
		if (condition)
			this.description.addAll(description);
		return this;
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	public GuiItemBuilder click(Optional<Runnable> handler) {
		this.handler = (player, slot, click) -> {
			handler.ifPresent(Runnable::run);
			return true;
		};
		return this;
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	public GuiItemBuilder click(Runnable handler) {
		this.handler = (player, slot, click) -> {
			handler.run();
			return true;
		};
		return this;
	}

	/**
	 * 
	 * @param handler
	 * @return
	 */
	public GuiItemBuilder click(OnClickHandler handler) {
		this.handler = handler;
		return this;
	}

	/**
	 * 
	 */
	public static interface OnClickHandler {

		/**
		 * 
		 * @param player
		 * @param slot
		 * @param click
		 * @return
		 */
		boolean onClick(Player player, int slot, ClickType click);
	}

	/**
	 * 
	 * @return
	 */
	public GuiItem build() {
		return new GuiItem(this);
	}

	@Override
	public GuiItemBuilder clone() {
		return new GuiItemBuilder(this);
	}
}
