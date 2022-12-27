package fr.omny.guis;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.omny.guis.backend.GuiListener;
import fr.omny.guis.utils.FunctionalUtils;
import fr.omny.guis.utils.ReflectionUtils;

/**
 *
 */
public class OGui {

  private static Plugin plugin;

  /**
   * Register the OGui helpers function
   * @param plugin The plugin that register OGui
   * @throws IllegalArgumentException if plugin is null
   * @throws IllegalStateException if a plugin has already been registered
   */
  public static void register(Plugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("plugin provided is null");
    }
    if (OGui.plugin != null) {
      throw new IllegalStateException(String.format(
          "OGui registered method called twice (passed %s, registered %s)",
          plugin.getName(), OGui.plugin.getName()));
    }
    OGui.plugin = plugin;

    Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);

    plugin.getLogger().info("OGui loaded successfuly !");
  }

  /**
   *
   * @param player The player to show the inventory
   * @param object The object to edit
   */
  public static void open(Player player, Object object) {
    open(player, object, FunctionalUtils::voidReturn);
  }

  /**
   *
   * @param player The player to show the inventory
   * @param object The object to edit
   * @param onClose The function called when the inventory is closed
   * @throws IllegalArgumentException If object is null
   */
  public static void open(Player player, Object object, Runnable onClose) {
    if (object == null) {
      throw new IllegalArgumentException("Cannot edit null object");
    }
    if (!object.getClass().isAnnotationPresent(OClass.class)) {
      throw new IllegalAccessError(
          String.format("@OClass annotation is not present on %s",
                        object.getClass().getName()));
    }
    var gui = new OGui(object, onClose);
    gui.open(player);
  }

  private Object toEdit;
  private Class<?> toEditClass;
  private Runnable onClose;

  private OGui(Object toEdit, Runnable onClose) {
    this.toEdit = toEdit;
    this.toEditClass = toEdit.getClass();
    this.onClose = onClose;

    init();
  }

  private void init() { var fields = findFields(this.toEditClass); }

  private void open(Player player) {}

  /**
   * Field all fields to display
   */
  private Map<Field, OField> findFields(Class<?> klass) {
    Map<Field, OField> fields = new HashMap<>();

    // Support nested gui edit
    if (klass.getSuperclass() != Object.class) {
      if (klass.getSuperclass().isAnnotationPresent(OClass.class)) {
        fields.putAll(findFields(klass.getSuperclass()));
      }
    }

    // Look for all fields (public, private, protected and even package)
    for (Field field : klass.getDeclaredFields()) {
      // In case the field is kinda private, make is accessible
      try {
        ReflectionUtils.access(field, () -> {
          if (field.isAnnotationPresent(OField.class)) {
            fields.put(field, field.getAnnotation(OField.class));
          }
        });
      } catch (Exception e) {
        OGui.plugin.getLogger()
          .log(Level.SEVERE, "Error happened while getting field data");
        e.printStackTrace();
      }
    }
    return fields;
  }
}
