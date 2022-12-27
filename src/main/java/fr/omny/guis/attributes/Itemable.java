package fr.omny.guis.attributes;

import fr.omny.guis.backend.GuiItemBuilder;

/**
 * Provide a custom displayed item for GUI builder
 */
public interface Itemable {

  /**
   *
   * Click handler will be rewrited, so don't take this in count
   *
   * @return The item to be displayed
   */
  GuiItemBuilder item();
}
