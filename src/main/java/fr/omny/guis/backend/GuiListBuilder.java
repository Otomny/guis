package fr.omny.guis.backend;

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.entity.Player;

public class GuiListBuilder<T> {

	private int startPage = 0;
	private int rowsPerPage = 0;
	private SortedMap<Integer, GuiItem> items = new TreeMap<>();
	private Optional<Consumer<Integer>> pageChange = Optional.empty();
	private Optional<Function<T, GuiItemBuilder>> itemCreator = Optional.empty();
	private Optional<Runnable> onClose = Optional.empty();
	private List<T> list;
	private String name;

	public GuiListBuilder(String name, List<T> list){
		this.name = name;
		this.list = list;
	}
	
	public GuiListBuilder<T> page(int page){
		this.startPage = page;
		return this;
	}

	public GuiListBuilder<T> pageChange(Consumer<Integer> pageChange){
		this.pageChange = Optional.of(pageChange);
		return this;
	}

	public GuiListBuilder<T> itemCreation(Function<T, GuiItemBuilder> itemCreator){
		this.itemCreator = Optional.of(itemCreator);
		return this;
	}

	public GuiListBuilder<T> close(Runnable onClose){
		this.onClose = Optional.of(onClose);
		return this;
	}

	public void open(Player player) {}

}
