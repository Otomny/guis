package fr.omny.guis.fields;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import fr.omny.guis.attributes.Itemable;
import fr.omny.guis.backend.GuiItemBuilder;
import lombok.Getter;
import lombok.Setter;

@OClass
@Getter
@Setter
public class ItemField implements Itemable {

	@OField
	private Material type = Material.STONE;

	@OField
	private int quantity = 1;

	@Override
	public GuiItemBuilder item() {
		return new GuiItemBuilder().icon(new ItemStack(this.type, this.quantity));
	}

	@Override
	public String toString() {
		return type.toString();
	}

}
