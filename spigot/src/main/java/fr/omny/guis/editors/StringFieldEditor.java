package fr.omny.guis.editors;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.OGui;
import fr.omny.guis.attributes.Updateable;
import fr.omny.guis.utils.ReflectionUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.wesjd.anvilgui.AnvilGUI;

public class StringFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		return field.getType() == String.class;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		try {
			field.setAccessible(true);
			String currentValue = (String) field.get(toEdit);
			new AnvilGUI.Builder().onClose(p -> {
				Bukkit.getScheduler().runTask(OGui.getPlugin(), onClose);
			}).onComplete((completion) -> {
				try {
					ReflectionUtils.access(field, () -> {
						String text = MiniMessage.builder().build()
								.serialize(completion.getOutputItem().getItemMeta().displayName());
						field.set(toEdit, text);
						if (toEdit instanceof Updateable updateable) {
							updateable.fieldUpdate(field);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				return Arrays.asList(AnvilGUI.ResponseAction.close());
			}).text(Optional.ofNullable(currentValue).orElse("Editing " + field.getName()))
					.itemLeft(new ItemStack(Material.PAPER))
					.plugin(OGui.getPlugin()).open(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
