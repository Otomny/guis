package fr.omny.guis.backend;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import net.kyori.adventure.text.Component;

@Getter
public class Gui {

	private Inventory inventory;
	private Component name;
	private SortedMap<Integer, GuiItem> items = new TreeMap<>();
	private Optional<GuiBuilder.InventoryHandler> handler;
	private ItemStack fillSideItem;
	private boolean fillSide;
	private int size;
	private Player player;

	public Gui(GuiBuilder guiBuilder) {
		this.handler = Optional.ofNullable(guiBuilder.getHandler());
		this.name = guiBuilder.getTitle();
		this.items = guiBuilder.getItems();
		this.size = guiBuilder.getRows() * 7;
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

	private void init() {

		this.inventory = Bukkit.createInventory(null, fixSize(this.size), this.name);
		if (this.fillSide) {
			fillOnSide();
		}
		// Fill the inventory and return
		for (Map.Entry<Integer, GuiItem> en : this.items.entrySet()) {
			if (en.getKey() < 54) {
				inventory.setItem(en.getKey(), en.getValue().getDisplay());
				// // Get player head in async
				// if (en.getValue().isPlayerHead()) {
				// HeadGetter.getHead(en.getValue(), this);
				// }
			}
		}
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

}
