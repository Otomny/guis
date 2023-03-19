package fr.omny;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fr.omny.guis.backend.GuiItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class GuiItemBuilderTest {

	@Test
	public void testItemBuilder() {
		GuiItemBuilder builder = new GuiItemBuilder()
				.name(Component.text("Test", NamedTextColor.GRAY))
				.description(Component.text("A"), Component.text("B"))
				.description(Component.text("C"), Component.text("D"));

		PlainTextComponentSerializer pSerializer = PlainTextComponentSerializer.plainText();

		assertEquals("Test", pSerializer.serialize(builder.getDisplayName()));
		assertEquals(4, builder.getDescription().size());
		assertEquals("A", pSerializer.serialize(builder.getDescription().get(0)));
		assertEquals("B", pSerializer.serialize(builder.getDescription().get(1)));
		assertEquals("C", pSerializer.serialize(builder.getDescription().get(2)));
		assertEquals("D", pSerializer.serialize(builder.getDescription().get(3)));
	}

}
