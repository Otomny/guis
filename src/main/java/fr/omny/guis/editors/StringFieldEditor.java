package fr.omny.guis.editors;


import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;
import fr.omny.guis.OGui;
import fr.omny.guis.attributes.Updateable;
import fr.omny.guis.utils.ReflectionUtils;
import net.wesjd.anvilgui.AnvilGUI;

@OMainEditor
public class StringFieldEditor implements OFieldEditor {

	@Override
	public boolean accept(Field field) {
		return field.getType() == String.class;
	}

	@Override
	public void edit(Player player, Object toEdit, Field field, OField fieldData, Runnable onClose) {
		new AnvilGUI.Builder().onClose(p -> {
			Bukkit.getScheduler().runTask(OGui.getPlugin(), onClose);
		}).onComplete((completion) -> {
			try {
				ReflectionUtils.access(field, () -> {
					field.set(toEdit, completion.getText());
					if (toEdit instanceof Updateable updateable) {
						updateable.fieldUpdate(field);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return Arrays.asList(AnvilGUI.ResponseAction.close());
		}).text("Editing " + field.getName()).itemLeft(new ItemStack(Material.PAPER))
				.plugin(OGui.getPlugin()).open(player);
	}

}
