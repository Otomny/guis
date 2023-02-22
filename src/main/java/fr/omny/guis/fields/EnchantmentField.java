package fr.omny.guis.fields;


import java.lang.reflect.Field;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.omny.guis.OClass;
import fr.omny.guis.OField;
import fr.omny.guis.attributes.Itemable;
import fr.omny.guis.attributes.Updateable;
import fr.omny.guis.backend.GuiItemBuilder;
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
public class EnchantmentField implements Cloneable, Itemable, Updateable{

	@OField(display = Material.ENCHANTED_BOOK, editor = ObjectInListFieldEditor.class, stringifier = JavaStringifier.class)
	private Enchantment enchantment;

	/**
	 * Initialized with level 1, level 0 doesn't make any sense
	 */
	@OField(display = Material.EXPERIENCE_BOTTLE)
	private int level = 1;

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

	@Override
	public EnchantmentField clone() {
		return new EnchantmentField(this.enchantment, this.level);
	}

	@Override
	public GuiItemBuilder item(Player player) {
		return new GuiItemBuilder()
			.icon(Material.ENCHANTED_BOOK)
			.name(this.enchantment == null ? "§cUndefined" : this.enchantment.getKey().getKey())
			.description("§7§oLevel "+this.level);
	}

	@Override
	public void fieldUpdate(Field field) {
		if(field.getName().equalsIgnoreCase("level")){
			this.level = Math.min(level, 1);
		}
	}

	@Override
	public void update() {}

}
