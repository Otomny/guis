package fr.omny.guis;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

public interface OFieldEditor<T> {
  
  Class<? super T> type();

  /**
   * 
   * @param player The player to show inventory to
   * @param edit The 
   * @param field
   * @param fieldData
   */
  void edit(Player player, T edit, Field field, OField fieldData);

}
