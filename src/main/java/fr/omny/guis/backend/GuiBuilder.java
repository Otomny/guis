package fr.omny.guis.backend;

import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

@Getter
public class GuiBuilder {

  public static final int MAX_ROWS = 7;

  private int rows = 3;
  private boolean fillSide = false;
  private ItemStack fillSideItem = new ItemStack(Material.STONE);
  private SortedMap<Integer, GuiItem> items = new TreeMap<>();
  private String title;
  private InventoryHandler handler;

  /**
   *
   * @param title The title of the inventory
   */
  public GuiBuilder(String title) { this.title = title; }

  /**
   *
   * @param rows The number of rows of this inventory
   * @throws IllegalArgumentException If rows > 7
   * @return
   */
  public GuiBuilder rows(int rows) {
    if (rows > MAX_ROWS)
      throw new IllegalArgumentException("Row count cannot exceed 7");
    this.rows = rows;
    return this;
  }

  /**
   *
   * @param item The item to be displayed at slot
   * @return
   */
  public GuiBuilder item(GuiItem item) { return item(nextSlot(), item); }

  public GuiBuilder item(int slot, GuiItem guiItem) {
    if (guiItem == null) {
      return this;
    }
    this.items.put(slot, guiItem);
    return this;
  }

  public GuiBuilder handler(InventoryHandler handler) {
    this.handler = handler;
    return this;
  }

  public GuiBuilder fillSide(Material material) {
    return fillSide(new ItemStack(material));
  }

  public GuiBuilder fillSide(ItemStack itemStack) {
    if (itemStack == null) {
      this.fillSide = false;
    } else {
      this.fillSide = true;
      this.fillSideItem = itemStack;
    }
    return this;
  }

  public int nextSlot() {
    if (!this.fillSide) {
      return this.items.isEmpty() ? 0 : this.items.lastKey() + 1;
    }
    if (this.items.isEmpty()) {
      return 10;
    }
    int nextSlot = this.items.lastKey() + 1;
    if (nextSlot % 9 == 0) {
      nextSlot += 1;
    }
    if ((nextSlot + 1) % 9 == 0) {
      nextSlot += 2;
    }
    return nextSlot;
  }

  public static interface InventoryHandler {

    void onOpen(Gui gui, Player player, InventoryOpenEvent event);

    void onClose(Gui gui, Player player, InventoryCloseEvent event);

    void onClick(Gui gui, Player player, InventoryClickEvent event);
  }

  public Gui build() { return new Gui(this); }
}
