package fr.omny.guis.fields;


import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import fr.omny.guis.editors.ObjectInListFieldEditor;
import fr.omny.guis.editors.stringifiers.JavaStringifier;
import lombok.Getter;
import lombok.Setter;

@OClass
@Getter
@Setter
/**
 * Wrapper enchant
 */
public class EnchantmentField {

	@OField(display = Material.ENCHANTED_BOOK, editor = ObjectInListFieldEditor.class, stringifier = JavaStringifier.class)
	private Enchantment enchantment;

	@OField(display = Material.EXPERIENCE_BOTTLE)
	private int level;

	/**
	 * Create an empty wrapper enchant
	 */
	public EnchantmentField() {}

	/**
	 * Create a wrapper enchant
	 * 
	 * @param enchantment Enchantment
	 * @param level       Level
	 */
	public EnchantmentField(Enchantment enchantment, int level) {
		this.enchantment = enchantment;
		this.level = level;
	}

	/**
	 * Apply the enchantment on the ItemStack
	 * 
	 * @param stack
	 */
	public void applyEnchantment(ItemStack stack) {
		stack.addUnsafeEnchantment(enchantment, level);
	}

}
