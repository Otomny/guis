package fr.omny.guis.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import fr.omny.guis.fields.ItemField;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ItemFieldCreateEvent extends Event{

	public static final HandlerList HANDLER_LIST = new HandlerList();

	/**
	 * @return the handlerList
	 */
	public static HandlerList getHandlerList() {
			return HANDLER_LIST;
	}

	private final ItemField itemField;
	@Setter
	private ItemStack result;

	/**
	 * @param itemField
	 * @param result
	 */
	public ItemFieldCreateEvent(ItemField itemField, ItemStack result) {
		this.itemField = itemField;
		this.result = result;
	}

	/**
	 * @param isAsync
	 * @param itemField
	 * @param result
	 */
	public ItemFieldCreateEvent(boolean isAsync, ItemField itemField, ItemStack result) {
		super(isAsync);
		this.itemField = itemField;
		this.result = result;
	}





	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}
	
}
