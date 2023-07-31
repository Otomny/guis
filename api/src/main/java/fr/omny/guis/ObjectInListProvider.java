package fr.omny.guis;

import java.util.List;

import fr.omny.guis.utils.ReflectionUtils;

/**
 * 
 */
public interface ObjectInListProvider<T> {
	
	/**
	 * Provide the list of available object
	 * @return
	 */
	List<T> provide();

	/**
	 * The class of the object
	 * @return
	 */
	Class<T> type();

	/**
	 * Provide string representation of object
	 * @param object
	 * @return
	 */
	default String asString(T object){
		return ReflectionUtils.string(object);
	}

}
