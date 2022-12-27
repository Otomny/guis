package fr.omny.guis.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.InventoryView;

import lombok.Getter;

public class GuiListener implements Listener {

  @Getter private static Map<UUID, Gui> openGuis = new HashMap<>();

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClick(InventoryClickEvent event) {
    Player player = (Player)event.getWhoClicked();
    InventoryView view = event.getView();

    // Open the inventory panel that this player has open (they can only ever
    // have one)
    if (openGuis.containsKey(player.getUniqueId()) &&
        openGuis.get(player.getUniqueId())
            .getInventory()
            .equals(event.getInventory())) {
      // Cancel the event. If they don't want it to be cancelled then the click
      // handler(s) should
      // uncancel it. If gui was from our environment, then cancel event anyway.
      event.setCancelled(true);

      // Check the name of the panel
      if (view.getTitle().equals(
              openGuis.get(player.getUniqueId()).getName())) {
        // Close inventory if clicked outside and if setting is true
        if (event.getSlotType().equals(SlotType.OUTSIDE)) {
          openGuis.remove(player.getUniqueId());
          player.closeInventory();
          return;
        }

        // Get the panel itself
        Gui gui = openGuis.get(player.getUniqueId());
        // Check that they clicked on a specific item
        GuiItem pi = gui.getItems().get(event.getRawSlot());
        if (pi != null) {
          pi.getHandler().ifPresent(handler -> {
            // Execute the handler's onClick method and optionally cancel the
            // event if the handler returns true
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f,
                             1f);
            event.setCancelled(
                handler.onClick(player, event.getSlot(), event.getClick()));
          });
        }
        // If there is a listener, then run it and refresh the panel
        gui.getHandler().ifPresent(l -> l.onClick(gui, player, event));
      } else {

        // Wrong name - delete this panel
        openGuis.remove(player.getUniqueId());
        player.closeInventory();
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryClose(InventoryCloseEvent event) {
    if (openGuis.containsKey(event.getPlayer().getUniqueId())) {
      Gui gui = openGuis.get(event.getPlayer().getUniqueId());
      // Run any close inventory methods
      gui.getHandler().ifPresent(
          l -> l.onClose(gui, (Player)event.getPlayer(), event));
      openGuis.remove(event.getPlayer().getUniqueId());
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onLogOut(PlayerQuitEvent event) {
    openGuis.remove(event.getPlayer().getUniqueId());
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPluginDisable(PluginDisableEvent event) {
    closeAllPanels();
  }

  /**
   * Closes all open BentoBox panels
   *
   * @since 1.5.0
   */
  public static void closeAllPanels() {
    // Use stream clones to avoid concurrent modification exceptions
    openGuis.values()
        .stream()
        .collect(Collectors.toList())
        .forEach(gui
                 -> gui.getInventory()
                        .getViewers()
                        .stream()
                        .collect(Collectors.toList())
                        .forEach(HumanEntity::closeInventory));
  }
}
