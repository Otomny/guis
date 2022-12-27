package fr.omny.guis.attributes;


/**
 * Represent an Object which can be summerized to a title
 * And then displayed on a GUI
 */
public interface Titeable {


  /**
   * 
   * @return The title of the object, with shouldn't exceed 20 characters
   */
  String title();
}
