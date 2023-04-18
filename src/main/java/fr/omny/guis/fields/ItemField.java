package fr.omny.guis.fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import fr.omny.guis.attributes.Itemable;
import fr.omny.guis.backend.GuiItemBuilder;
import fr.omny.guis.events.ItemFieldCreateEvent;
import lombok.Getter;
import lombok.Setter;

@OClass
@Getter
@Setter
/**
 * Represent an editable item that can be translated to ItemStack
 */
public class ItemField implements Itemable, Cloneable {

	@OField
	private Material type = Material.STONE;

	@OField
	private int quantity = 1;

	@OField(display = Material.ENCHANTED_BOOK)
	private List<EnchantmentField> enchantments = new ArrayList<>();

	private Map<String, Object> additionalData = new HashMap<>();

	/**
	 * Create an empty item field
	 */
	public ItemField() {
	}

	/**
	 * Create an item field with the given material, quantity and enchantments
	 * 
	 * @param type         The material
	 * @param quantity     The quantity
	 * @param enchantments The enchantments
	 */
	public ItemField(Material type, int quantity, List<EnchantmentField> enchantments) {
		this.type = type;
		this.quantity = quantity;
		this.enchantments = enchantments;
	}

	/**
	 * Create an item field with the given material, quantity, enchantments and
	 * additional data
	 * 
	 * @param type           The material
	 * @param quantity       The quantity
	 * @param enchantments   The enchantments
	 * @param additionalData The additional data
	 */
	public ItemField(Material type, int quantity, List<EnchantmentField> enchantments,
			Map<String, Object> additionalData) {
		this.type = type;
		this.quantity = quantity;
		this.enchantments = enchantments;
		this.additionalData = additionalData;
	}

	@Override
	public GuiItemBuilder item(Player player) {
		return new GuiItemBuilder().icon(new ItemStack(type, quantity));
	}

	/**
	 * Output the itemstack representation if this object
	 * 
	 * @return The itemstack
	 */
	public ItemStack asItem() {
		var item = new ItemStack(type, quantity);
		this.enchantments.forEach(e -> e.applyEnchantment(item));
		new ItemFieldCreateEvent(false, this, item).callEvent();
		return item;
	}

	@Override
	public ItemField clone() {
		return new ItemField(this.type, this.quantity,
				this.enchantments.stream().map(EnchantmentField::clone).collect(Collectors.toList()));
	}

	@Override
	public String toString() {
		return type.toString();
	}

}
