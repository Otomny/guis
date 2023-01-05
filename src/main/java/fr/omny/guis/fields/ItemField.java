package fr.omny.guis.fields;


import java.util.ArrayList;
import java.util.List;

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
/**
 * Represent an editable item that can be translated to ItemStack
 */
public class ItemField implements Itemable {

	@OField
	private Material type = Material.STONE;

	@OField
	private int quantity = 1;

	@OField(display = Material.ENCHANTED_BOOK)
	private List<EnchantmentField> enchantments = new ArrayList<>();

	public ItemField() {}

	public ItemField(Material type, int quantity, List<EnchantmentField> enchantments) {
		this.type = type;
		this.quantity = quantity;
		this.enchantments = enchantments;
	}

	@Override
	public GuiItemBuilder item() {
		return new GuiItemBuilder().icon(new ItemStack(type, quantity));
	}

	/**
	 * Output the itemstack representation if this object
	 * 
	 * @return The itemstack
	 */
	public ItemStack asItem() {
		return new ItemStack(type, quantity);
	}

	@Override
	public String toString() {
		return type.toString();
	}

}
