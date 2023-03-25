package fr.omny.guis.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicates;

import fr.omny.guis.utils.Utils.Tuple2;
import net.kyori.adventure.text.Component;

public class GuiListBuilder<T> {

	public static boolean isLastPage(GuiListState state) {
		return state.page() == state.maxPage();
	}

	private int page = 0;
	private int rowsPerPage = 3;
	private Optional<Consumer<Integer>> pageChange = Optional.empty();
	private Optional<BiFunction<T, Integer, GuiItemBuilder>> itemCreator = Optional.empty();
	private Optional<Runnable> onClose = Optional.empty();
	private SortedMap<Integer, Tuple2<GuiItem, Predicate<GuiListState>>> items = new TreeMap<>();
	private Collection<T> list;
	private Component name;

	public GuiListBuilder(String name, Collection<T> list) {
		this(Component.text(name), list);
	}

	public GuiListBuilder(Component name, Collection<T> list) {
		this.name = name;
		this.list = list;
	}

	public GuiListBuilder<T> rows(int rows) {
		if (rows + 2 > GuiBuilder.MAX_ROWS)
			throw new IllegalArgumentException("Row count cannot exceed " + (GuiBuilder.MAX_ROWS - 2) + "");
		this.rowsPerPage = rows;
		return this;
	}

	public GuiListBuilder<T> page(int page) {
		this.page = page;
		return this;
	}

	public GuiListBuilder<T> pageChange(Consumer<Integer> pageChange) {
		this.pageChange = Optional.of(pageChange);
		return this;
	}

	public GuiListBuilder<T> itemCreation(Function<T, GuiItemBuilder> itemCreator) {
		this.itemCreator = Optional.of((data, index) -> itemCreator.apply(data));
		return this;
	}

	public GuiListBuilder<T> itemCreation(BiFunction<T, Integer, GuiItemBuilder> itemCreator) {
		this.itemCreator = Optional.of(itemCreator);
		return this;
	}

	public GuiListBuilder<T> close(Runnable onClose) {
		this.onClose = Optional.of(onClose);
		return this;
	}

	public GuiListBuilder<T> items(GuiItem item) {
		return items(Predicates.alwaysTrue(), item);
	}

	public GuiListBuilder<T> items(int index, GuiItem item) {
		return items(index, Predicates.alwaysTrue(), item);
	}

	public GuiListBuilder<T> items(Predicate<GuiListState> condition, GuiItem item) {
		return items(-1, condition, item);
	}

	public GuiListBuilder<T> items(int index, Predicate<GuiListState> condition, GuiItem item) {
		this.items.put(index, new Tuple2<>(item, condition));
		return this;
	}

	public record GuiListState(int page, int maxPage) {
	}

	public Gui build() {
		int itemPageCount = this.rowsPerPage * 7;
		List<T> wrapped = new ArrayList<>(this.list);
		BiFunction<T, Integer, GuiItemBuilder> generator = this.itemCreator.orElse((obj, index) -> {

			String toString = obj == null ? "null" : obj.toString();
			return new GuiItemBuilder().icon(new ItemStack(Material.PAPER)).breakLine()
					.descriptionLegacy(toString);
		});

		int maxPageCount = (int) Math.floor(Double.valueOf(list.size()) / Double.valueOf(itemPageCount));
		if (page < 0) {
			this.pageChange.ifPresent(c -> c.accept(0));
		} else if (page > maxPageCount) {
			this.pageChange.ifPresent(c -> c.accept(maxPageCount));
		}
		int startPage = page * itemPageCount;
		int endPage = Math.min(list.size(), (page + 1) * itemPageCount);

		GuiBuilder guiBuilder = new GuiBuilder(this.name).rows(this.rowsPerPage + 2)
				.fillSide(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE));

		for (int i = startPage; i < endPage; i++) {
			final int index = i;
			var objectAt = wrapped.get(index);

			GuiItemBuilder guiItemBuilder = generator.apply(objectAt, i);

			guiBuilder.item(guiItemBuilder.build());
		}

		var guiState = new GuiListState(page, maxPageCount);

		for (var entry : this.items.entrySet()) {
			int index = entry.getKey();
			var value = entry.getValue();
			if (value.value().test(guiState)) {
				if (index >= 0) {
					guiBuilder.item(index, value.key());
				} else {
					guiBuilder.item(value.key());
				}
			}
		}

		if (page > 0) {
			guiBuilder.item(39, new GuiItemBuilder().name("§7Previous page").icon(Material.ARROW)
					.click(() -> this.pageChange.ifPresent(c -> c.accept(page - 1))).build());
		}
		if (page < maxPageCount) {
			guiBuilder.item(41, new GuiItemBuilder().icon(Material.ARROW).name("§7Next page")
					.click(() -> this.pageChange.ifPresent(c -> c.accept(page + 1))).build());
		}

		guiBuilder.item(9, GuiItem.back(onClose));
		return guiBuilder.build();
	}

	public void open(Player player) {
		build().open(player);
	}

}
