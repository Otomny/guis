package fr.omny.guis;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.omny.guis.backend.GuiBuilder;
import fr.omny.guis.backend.GuiItem;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.backend.GuiListener;
import fr.omny.guis.editors.IntegerFieldEditor;
import fr.omny.guis.editors.ListOClassFieldEditor;
import fr.omny.guis.editors.OMainEditor;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.guis.utils.Utils;
import lombok.Getter;

/**
 *
 */
public class OGui {

	@Getter
	private static Plugin plugin;

	private static final List<OFieldEditor> EDITORS = new ArrayList<>();

	/**
	 * Register an editor for a type
	 * 
	 * @param editor the editor
	 */
	public static void register(OFieldEditor... editors) {
		EDITORS.addAll(List.of(editors));
	}

	/**
	 * Register the OGui helpers function
	 * 
	 * @param plugin The plugin that register OGui
	 * @throws IllegalArgumentException if plugin is null
	 * @throws IllegalStateException    if a plugin has already been registered
	 */
	public static void register(Plugin plugin) {
		if (plugin == null) {
			throw new IllegalArgumentException("plugin provided is null");
		}
		if (OGui.plugin != null) {
			throw new IllegalStateException(
					String.format("OGui registered method called twice (passed %s, registered %s)",
							plugin.getName(), OGui.plugin.getName()));
		}
		OGui.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);

		plugin.getLogger().info("OGui loaded successfuly !");
		register(new IntegerFieldEditor(), new ListOClassFieldEditor());
	}

	/**
	 * @param player The player to show the inventory
	 * @param object The object to edit
	 */
	public static void open(Player player, Object object) {
		open(player, object, () -> player.closeInventory());
	}

	/**
	 * @param player  The player to show the inventory
	 * @param object  The object to edit
	 * @param onClose The function called when the inventory is closed
	 * @throws IllegalArgumentException If object is null
	 */
	public static void open(Player player, Object object, Runnable onClose) {
		if (object == null) {
			throw new IllegalArgumentException("Cannot edit null object");
		}
		if (!object.getClass().isAnnotationPresent(OClass.class)) {
			throw new IllegalAccessError(
					String.format("@OClass annotation is not present on %s", object.getClass().getName()));
		}
		var gui = new OGui(object, onClose);
		gui.open(player);
	}

	private Object toEdit;
	private Class<?> toEditClass;
	private OClass toEditData;
	private Runnable onClose;

	private OGui(Object toEdit, Runnable onClose) {
		this.toEdit = toEdit;
		this.toEditClass = toEdit.getClass();
		this.toEditData = toEditClass.getAnnotation(OClass.class);
		this.onClose = onClose;

		init();
	}

	private void init() {}

	private List<OFieldEditor> findForType(Field field) {
		return EDITORS.stream().filter(f -> f.accept(field)).toList();
	}

	private void open(Player player) {
		var guiBuilder = new GuiBuilder(Utils.orString(this.toEditData.value(), "Editing"))
				.fillSide(this.toEditData.fillSide());

		for (var entry : findFields(this.toEditClass).entrySet()) {
			var field = entry.getKey();
			var data = entry.getValue();
			var editors = findForType(field);
			String fieldName = Utils.replaceColor(Utils.orString(data.value(), "&e" + field.getName()));
			if (editors.isEmpty()) {
				plugin.getLogger().warning("No editors found for type " + field.getType() + " on field "
						+ field.getName() + " of class " + this.toEditClass.getSimpleName());
				guiBuilder.item(new GuiItemBuilder().icon(Material.BARRIER).name(fieldName)
						.description("§cUnable to found editor for type " + field.getType()).build());
				continue;
			}
			OFieldEditor editor = editors.size() == 1 ? editors.get(0)
					: editors.stream().filter(e -> e.getClass().isAnnotationPresent(OMainEditor.class))
							.findFirst().orElse(editors.get(0));

			guiBuilder.item(new GuiItemBuilder().name(fieldName).icon(data.display())
					.description(data.description())
					.click(() -> editor.edit(player, this.toEdit, field, data, () -> open(player))).build());
		}
		guiBuilder.item(9, GuiItem.back(onClose));
		guiBuilder.open(player);

	}

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
				OGui.plugin.getLogger().log(Level.SEVERE, "Error happened while getting field data");
				e.printStackTrace();
			}
		}
		return fields;
	}
}
