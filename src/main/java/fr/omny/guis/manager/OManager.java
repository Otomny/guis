package fr.omny.guis.manager;


import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import fr.omny.guis.attributes.Identifiable;

/**
 * Represent a manager that work with Identifiable object (Object that can be found by UUIDs)
 */
public interface OManager<T extends Identifiable> {

	/**
	 * @return The class of the object
	 */
	Class<T> getRepresentingClass();

	/**
	 * The namespace used for underlying implementation (SQL, Config files, etc...) By default, return the class name in
	 * lowercase
	 * 
	 * @return The namespace name
	 */
	default String getNamespace() {
		return getRepresentingClass().getSimpleName().toLowerCase();
	}

	/**
	 * Find an Object by it ID
	 * @param id The object's ID
	 * @return Optional of T
	 */
	Optional<T> getFromId(UUID id);

	/**
	 * 
	 * @return a view of all objects stored
	 */
	Collection<T> iterate();

}
