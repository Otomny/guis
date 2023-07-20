package fr.omny.guis.editors;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.backend.GuiListBuilder;
import fr.omny.guis.editors.providers.AutomaticProvider;
import fr.omny.guis.editors.providers.EnchantmentListProvider;
import fr.omny.guis.editors.providers.ObjectInListProvider;
import fr.omny.guis.editors.providers.OnlinePlayerListProvider;
import fr.omny.guis.editors.providers.WorldListProvider;
import fr.omny.guis.editors.providers.WorldNameListProvider;
import fr.omny.guis.utils.ReflectionUtils;
import fr.omny.guis.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ObjectInListFieldEditor implements OFieldEditor {

	private static Map<Class<? extends ObjectInListProvider<?>>, ObjectInListProvider<?>> PROVIDERS = new HashMap<>();

	/**
	 * @param provider
	 */
	public static void register(Class<? extends ObjectInListProvider<?>> klass, ObjectInListProvider<?> provider) {
		PROVIDERS.put(klass, provider);
	}

	@SuppressWarnings("unchecked")
	public static Optional<ObjectInListProvider<Object>> findProvider(Class<?> type) {
		return PROVIDERS.values().stream().filter(p -> p.type() == type).map(p -> (ObjectInListProvider<Object>) p)
				.findFirst();
	}

	public static ObjectInListProvider<?> instance(Class<? extends ObjectInListProvider<?>> list) {
		if (PROVIDERS.containsKey(list)) {
			return PROVIDERS.get(list);
		}
		try {
			var instance = list.getConstructor().newInstance();
			PROVIDERS.put(list, instance);
			return instance;
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	static {
		// Initializer
		register(EnchantmentListProvider.class, new EnchantmentListProvider());
		register(OnlinePlayerListProvider.class, new OnlinePlayerListProvider());
		register(WorldListProvider.class, new WorldListProvider());
		register(WorldNameListProvider.class, new WorldNameListProvider());
	}

	@Override
	public boolean allowNullValues() {
		return true;
	}

	@Override
	public boolean accept(Field field) {
		Class<?> klass = field.getType();
		return ObjectInListFieldEditor.findProvider(klass).isPresent();
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		edit(0, player, toEdit, field, fieldData, onClose);
	}

	private void edit(int page, Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		Class<?> type = field.getType();
		var optionalProvider = fieldData.provider().provider() == AutomaticProvider.class ? findProvider(type)
				: Optional.of(instance(fieldData.provider().provider()));

		if (optionalProvider.isEmpty()) {
			player.sendMessage("§cNo provider found for type " + type.getSimpleName());
			onClose.run();
			return;
		}
		@SuppressWarnings("unchecked")
		ObjectInListProvider<Object> provider = (ObjectInListProvider<Object>) optionalProvider.get();
		Object selectedValue = ReflectionUtils.get(toEdit, field);
		new GuiListBuilder<>(Component.text(Utils.orString(fieldData.value(), field.getName()), NamedTextColor.GRAY),
				provider.provide()).page(page)
				.itemCreation(obj -> {
					boolean selected = selectedValue == obj;
					return new GuiItemBuilder().name((selected ? "§b" : "§e") + provider.asString(obj))
							.icon(selected ? Material.DIAMOND : fieldData.display())
							.breakLine().descriptionLegacy("§7§oValue: §e" + provider.asString(obj)).click((p, slot, click) -> {
								try {
									ReflectionUtils.set(toEdit, field, obj);
								} catch (Exception e) {
									e.printStackTrace();
								}
								edit(page, player, toEdit, field, fieldData, onClose);
								return true;
							});
				}).pageChange(newPage -> edit(newPage, player, toEdit, field, fieldData, onClose)).close(onClose).open(player);
	}

	@Override
	public int priority() {
		return 1;
	}
}
