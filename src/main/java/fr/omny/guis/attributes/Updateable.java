package fr.omny.guis.attributes;

import java.lang.reflect.Field;

/**
 * Behaviour of something that need to listen to field and global updates
 *
 * Implement this class when you need to get events when a field was edited, or
 * when the inventory is closed.
 */
public interface Updateable {

  /**
   * Method called when a single field was edited by the player
   */
  default void fieldUpdate(Field field) {}

  /**
   * Method called when the inventory has been closed
   */
  void update();
}
