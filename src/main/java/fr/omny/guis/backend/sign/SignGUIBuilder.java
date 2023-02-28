package fr.omny.guis.backend.sign;


import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class SignGUIBuilder {

	public static Function<SignGUIBuilder, ISignGui> SIGN_GUI_CREATOR = SignGUI::new;

	private Optional<Consumer<String>> onClose = Optional.empty();
	private String title = "Sign Edit";
	private Material signType = Material.BIRCH_WALL_SIGN;

	private Predicate<String> validate;

	public SignGUIBuilder(Predicate<String> validate) {
		this.validate = validate;
	}

	public SignGUIBuilder signType(Material material) {
		if (!material.toString().contains("SIGN")) {
			throw new IllegalArgumentException(
					"The type given is not a sign (given " + material.toString() + ")");
		}
		this.signType = material;
		return this;
	}

	public SignGUIBuilder title(String title) {
		this.title = title;
		return this;
	}

	public SignGUIBuilder onClose(Consumer<String> onClose) {
		this.onClose = Optional.of(onClose);
		return this;
	}

	public void open(Player player) {
		build().open(player);
	}

	public SignGUI build() {
		return new SignGUI(this);
	}

}
