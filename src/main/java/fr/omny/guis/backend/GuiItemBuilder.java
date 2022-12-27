package fr.omny.guis.backend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

@Getter
public class GuiItemBuilder implements Cloneable {

  private ItemStack display = new ItemStack(Material.STONE);
  private OnClickHandler handler = (player, slot, click) -> true;
  private List<String> description = new ArrayList<>();
  private String displayName;
  private boolean hideEnchant = false;
  private boolean glow = false;

  public GuiItemBuilder() {}

  private GuiItemBuilder(GuiItemBuilder guiItemBuilder) {
    this.display = guiItemBuilder.display.clone();
    this.handler = guiItemBuilder.handler;
  }

  public GuiItemBuilder icon(Material type) {
    this.display = new ItemStack(type);
    return this;
  }

  public GuiItemBuilder glow(boolean glow){
    this.glow = glow;
    return this;
  }

  public GuiItemBuilder hideEnchantments(boolean hideEnchant){
    this.hideEnchant = hideEnchant;
    return this;
  }

  public GuiItemBuilder icon(ItemStack itemStack) {
    this.display = new ItemStack(itemStack);
    return this;
  }

  public GuiItemBuilder name(String name) {
    this.displayName = name;
    return this;
  }

  public GuiItemBuilder breakLine() { return description(""); }

  public GuiItemBuilder description(String... description) {
    return description(List.of(description));
  }

  public GuiItemBuilder description(List<String> description) {
    this.description.addAll(description);
    return this;
  }

  public GuiItemBuilder click(OnClickHandler handler) {
    this.handler = handler;
    return this;
  }

  public static interface OnClickHandler {

    boolean onClick(Player player, int slot, ClickType click);
  }

  public GuiItem build() { return new GuiItem(this); }

  @Override
  public GuiItemBuilder clone() {
    return new GuiItemBuilder(this);
  }
}
