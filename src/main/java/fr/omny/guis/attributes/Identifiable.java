package fr.omny.guis.attributes;

import java.util.UUID;

/**
 * Something that can be found by an UUID
 */
public interface Identifiable {

	/**
	 * @return The UUID of the objet
	 */
	UUID getId();
}
