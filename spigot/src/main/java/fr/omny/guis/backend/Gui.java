package fr.omny.guis.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.omny.guis.backend.GuiItemBuilder.OnClickHandler;
import fr.omny.guis.backend.head.GuiHeadFetcher;
import fr.omny.guis.backend.head.HeadFetcher;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class Gui implements InventoryHolder, GuiHeadFetcher {

	private Inventory inventory;
	private Component name;
	private Int2ObjectSortedMap<GuiItem> items = new Int2ObjectRBTreeMap<>();
	private Optional<GuiBuilder.InventoryHandler> handler;
	private ItemStack fillSideItem;
	private boolean fillSide;
	private int size;
	private Player player;
	@Getter
	private List<OnClickHandler> closesTasks = new ArrayList<>();

	public Gui(GuiBuilder guiBuilder) {
		this.handler = Optional.ofNullable(guiBuilder.getHandler());
		this.name = guiBuilder.getTitle();
		this.items = guiBuilder.getItems();
		this.size = guiBuilder.getRows() * 9;
		this.fillSide = guiBuilder.isFillSide();
		this.fillSideItem = guiBuilder.getFillSideItem();
		init();
	}

	public Gui(GuiBuilder guiBuilder, Player player) {
		this.handler = Optional.ofNullable(guiBuilder.getHandler());
		this.name = guiBuilder.getTitle();
		this.items = guiBuilder.getItems();
		this.size = guiBuilder.getRows() * 9;
		this.fillSide = guiBuilder.isFillSide();
		this.fillSideItem = guiBuilder.getFillSideItem();
		this.player = player;
		init();
		this.open();
	}

	public void processClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		this.handler.ifPresent(
				l -> l.onClose(this, player, event));
		this.closesTasks.forEach(task -> task.onClick(player, 0, ClickType.UNKNOWN));
	}

	private void init() {
		this.inventory = Bukkit.createInventory(null, fixSize(this.size), this.name);
		if (this.fillSide) {
			fillOnSide();
		}
		// Fill the inventory and return
		this.items.forEach((slot, item) -> {
			if (slot < Math.min(54, this.inventory.getSize())) {
				this.inventory.setItem(slot, item.getDisplay());
				// Get player head in async
				if (item.isPlayerHead()) {
					HeadFetcher.getHead(item, this);
				}
			}
		});

		this.items.forEach((s, item) -> {
			if (item.isCloseOnClick()) {
				item.getHandler().ifPresent(this.closesTasks::add);
			}
		});
	}

	public void open(Player player) {
		this.player = player;
		open();
	}

	private void open() {
		Objects.requireNonNull(this.player);
		player.openInventory(inventory);
		this.handler.ifPresent(hand -> hand.onOpen(this, this.player));
		GuiListener.getOpenGuis().put(player.getUniqueId(), this);
	}

	private void fillOnSide() {
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, this.fillSideItem.clone());
		}
		for (int i = 0; i < inventory.getSize(); i++) {
			if (i > 9 && i < inventory.getSize() - 9) {
				if ((i % 9) >= 1 && (i % 9) <= 7) {
					inventory.setItem(i, null);
				}
			}
		}
	}

	private int fixSize(int size) {
		// If size is undefined (0) then use the number of items
		if (size == 0) {
			size = items.keySet().size();
		}
		/*
		 * TEST sur ça prcq ça merde if (this.fillSide) { size += 9; }
		 */
		if (size > 0) {
			// Make sure size is a multiple of 9 and is 54 max.
			size = size + 8;
			size -= (size % 9);
			if (size > 54)
				size = 54;
		} else {
			return 9;
		}
		return size;
	}

	@Override
	public void setHead(GuiItem item) {
		// Update the panel item
		// Find panel item index in items and replace it once more
		// in inventory to
		// update it.
		this.items.int2ObjectEntrySet().stream()
				.filter(entry -> entry.getValue() == item)
				.mapToInt(Map.Entry::getKey).findFirst()
				.ifPresent(index ->
				// Update item inside inventory to change icon only if
				// item is inside panel.
				this.inventory.setItem(index, item.getDisplay()));

	}

}
