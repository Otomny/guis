package fr.omny.guis.backend;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import lombok.Getter;

@Getter
public class Gui {

  private Inventory inventory;
  private String name;
  private SortedMap<Integer, GuiItem> items = new TreeMap<>();
  private Optional<GuiBuilder.InventoryHandler> handler;

  public Gui(GuiBuilder guiBuilder) {
    this.handler = Optional.ofNullable(guiBuilder.getHandler());
  }

  public void open(Player player) {}
}
