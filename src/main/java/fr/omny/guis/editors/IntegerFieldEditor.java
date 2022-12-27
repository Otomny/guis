package fr.omny.guis.editors;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import fr.omny.guis.OField;
import fr.omny.guis.OFieldEditor;

public class IntegerFieldEditor implements OFieldEditor<Integer>{

  @Override
  public Class<? super Integer> type() {
    return Integer.class;
  }

  @Override
  public void edit(Player player, Integer edit, Field field, OField fieldData) {
    
    
  }
  
}
