package fr.omny.guis.editors.providers;


import java.util.List;

import org.bukkit.enchantments.Enchantment;

import fr.omny.guis.ObjectInListProvider;

public class EnchantmentListProvider implements ObjectInListProvider<Enchantment> {

	@Override
	public List<Enchantment> provide() {
		return List.of(Enchantment.values());
	}

	@Override
	public Class<Enchantment> type() {
		return Enchantment.class;
	}

	@Override
	public String asString(Enchantment object) {
		return object.getKey().getKey();
	}
}
