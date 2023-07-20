package fr.omny.guis;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.omny.guis.attributes.Itemable;
import fr.omny.guis.backend.GuiBuilder;
import fr.omny.guis.backend.GuiItem;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.backend.GuiListener;
import fr.omny.guis.backend.head.HeadFetcher;
import fr.omny.guis.editors.AutomaticFieldEditor;
import fr.omny.guis.editors.BooleanFieldEditor;
import fr.omny.guis.editors.DoubleFieldEditor;
import fr.omny.guis.editors.EnumFieldEditor;
import fr.omny.guis.editors.IntegerFieldEditor;
import fr.omny.guis.editors.ListEnumSelectFieldEditor;
import fr.omny.guis.editors.ListFieldEditor;
import fr.omny.guis.editors.ListSelectFieldEditor;
import fr.omny.guis.editors.LocationFieldEditor;
import fr.omny.guis.editors.MaterialFieldEditor;
import fr.omny.guis.editors.OClassFieldEditor;
import fr.omny.guis.editors.OMainEditor;
import fr.omny.guis.editors.ObjectInListFieldEditor;
import fr.omny.guis.editors.StringFieldEditor;
import fr.omny.guis.editors.stringifiers.Stringifier;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.guis.utils.Utils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 *
 */
public class OGui {

	@Getter
	private static Plugin plugin;

	private static final List<OFieldEditor> EDITORS = new ArrayList<>();

	private static final Map<Class<? extends Stringifier>, Stringifier> STRINGIFIERS = new HashMap<>();

	/**
	 * Register an editor for a type
	 * 
	 * @param editor the editor
	 */
	public static void register(OFieldEditor... editors) {
		EDITORS.addAll(List.of(editors));
	}

	/**
	 * Find editor with the class of the editor
	 * 
	 * @param klass The class of the editor
	 * @return Possibly the editor, empty if not found
	 */
	public static Optional<OFieldEditor> getEditor(Class<? extends OFieldEditor> klass) {
		return EDITORS.stream().filter(e -> e.getClass().equals(klass)).findFirst();
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
			throw new IllegalStateException(String.format("OGui registered method called twice (passed %s, registered %s)",
					plugin.getName(), OGui.plugin.getName()));
		}
		try {
			OGui.plugin = plugin;

			Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
			new HeadFetcher(plugin);

			plugin.getLogger().info("OGui loaded successfuly !");
			register(new IntegerFieldEditor(), new DoubleFieldEditor(), new StringFieldEditor(), new ListFieldEditor(),
					new EnumFieldEditor(), new ListEnumSelectFieldEditor(), new OClassFieldEditor(),
					new MaterialFieldEditor(), new ObjectInListFieldEditor(), new ListSelectFieldEditor(),
					new LocationFieldEditor(), new BooleanFieldEditor());
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error while loading OGui", e);
		}

	}

	/**
	 * @param o
	 * @param stringifierClass
	 * @return
	 */
	public static String stringify(Object o, Class<? extends Stringifier> stringifierClass) {
		if (!STRINGIFIERS.containsKey(stringifierClass)) {
			try {
				STRINGIFIERS.put(stringifierClass, Stringifier.class.cast(stringifierClass.getConstructor().newInstance()));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		return STRINGIFIERS.get(stringifierClass).toString(o);
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

	private void init() {
	}

	private List<OFieldEditor> findForType(Field field) {
		var fieldData = field.getAnnotation(OField.class);
		if (fieldData.editor() != AutomaticFieldEditor.class && OFieldEditor.class.isAssignableFrom(fieldData.editor())) {

			var fieldEditor = EDITORS.stream().filter(editor -> editor.getClass() == fieldData.editor()).findFirst()
					.orElse(null);
			if (fieldEditor != null)
				return List.of(fieldEditor);
		}

		return EDITORS.stream().filter(f -> f.accept(field)).toList();
	}

	private void open(Player player) {
		var guiBuilder = new GuiBuilder(Utils.orString(this.toEditData.value(), "Editing"))
				.fillSide(this.toEditData.fillSide());

		for (var entry : findFields(this.toEditClass).entrySet()) {
			var field = entry.getKey();
			var data = entry.getValue();
			var editors = findForType(field);
			Object value = ReflectionUtils.get(this.toEdit, field);
			Component fieldName = Component.text(Utils.orString(data.value(), field.getName()), NamedTextColor.YELLOW);
			if (editors.isEmpty()) {
				plugin.getLogger().warning("No editors found for type " + field.getType() + " on field " + field.getName()
						+ " of class " + this.toEditClass.getSimpleName());
				guiBuilder.item(new GuiItemBuilder().icon(Material.BARRIER).name(fieldName)
						.descriptionLegacy("§cUnable to found editor for type " + field.getType()).build());
				continue;
			}
			OFieldEditor editor = editors.size() == 1 ? editors.get(0)
					: editors.stream().filter(e -> e.getClass().isAnnotationPresent(OMainEditor.class)).findFirst()
							.orElse(editors.stream().sorted(Comparator.comparingInt(OFieldEditor::priority).reversed())
									.findFirst()
									.orElse(null));

			var guiItemBuilder = value instanceof Itemable item ? item.item(player) : new GuiItemBuilder();
			guiBuilder.item(guiItemBuilder.name(fieldName).icon(data.display())
					.descriptionLegacy("§7§oValue: §e" + stringify(value, data.stringifier())).breakLine()
					.descriptionLegacy(data.description()).click(() -> {
						if (value == null && !editor.allowNullValues()) {
							player.playSound(player, Sound.ENTITY_VILLAGER_NO, SoundCategory.PLAYERS, 1.0f, 0);
							player.sendMessage("§cYou can't edit a null field, you must initialize it to a default value first.");
						} else {
							editor.edit(player, this.toEdit, field, data, () -> open(player));
						}
					}).build());
		}
		for (var entry : findMethods(this.toEditClass).entrySet()) {
			var method = entry.getKey();
			var data = entry.getValue();
			// String methodName = Utils.replaceColor(Utils.orString(data.value(), "&e" + method.getName()));
			Component methodName = Component.text(Utils.orString(data.value(), method.getName()+"()"), NamedTextColor.GOLD);
			guiBuilder.item(new GuiItemBuilder().name(methodName).icon(data.icon()).breakLine()
					.descriptionLegacy(data.description()).click(() -> {
						method.setAccessible(true);
						try {
							ReflectionUtils.callWithInject(method, this.toEdit, player);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}).build());
		}
		guiBuilder.item(9, GuiItem.back(onClose));
		guiBuilder.open(player);

	}

	private Map<Method, OMethod> findMethods(Class<?> klass) {
		Map<Method, OMethod> methods = new HashMap<>();

		// Support nested gui edit
		if (klass.getSuperclass() != Object.class && klass.getSuperclass().isAnnotationPresent(OClass.class)) {
			methods.putAll(findMethods(klass.getSuperclass()));
		}

		// Look for all methods (public, private, protected and even package)
		for (Method method : klass.getDeclaredMethods()) {
			// In case the method is private, make is accessible
			try {
				ReflectionUtils.access(method, () -> {
					if (method.isAnnotationPresent(OMethod.class)) {
						methods.put(method, method.getAnnotation(OMethod.class));
					}
				});
			} catch (Exception e) {
				OGui.plugin.getLogger().log(Level.SEVERE, "Error happened while getting method data");
				e.printStackTrace();
			}
		}
		return methods;
	}

	/**
	 * Field all fields to display
	 */
	private Map<Field, OField> findFields(Class<?> klass) {
		Map<Field, OField> fields = new HashMap<>();

		// Support nested gui edit
		if (klass.getSuperclass() != Object.class && klass.getSuperclass().isAnnotationPresent(OClass.class)) {
			fields.putAll(findFields(klass.getSuperclass()));
		}

		// Look for all fields (public, private, protected and even package)
		for (Field field : klass.getDeclaredFields()) {
			// In case the field is private, make is accessible
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
