package dev.xkmc.fruitsdelight.init.food;

import dev.xkmc.fruitsdelight.content.item.FDFoodItem;
import dev.xkmc.fruitsdelight.init.FruitsDelight;
import dev.xkmc.fruitsdelight.init.data.TagGen;
import dev.xkmc.fruitsdelight.init.registrate.FDItems;
import dev.xkmc.l2library.repack.registrate.providers.ProviderType;
import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.repack.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.registry.ModEffects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public enum FDFood implements IFDFood {

	BLUEBERRY_CUSTARD(FruitType.BLUEBERRY, FoodType.CREAM),
	BLUEBERRY_MUFFIN(FruitType.BLUEBERRY, FoodType.DESSERT),
	CRANBERRY_MUFFIN(FruitType.CRANBERRY, FoodType.DESSERT),
	FIG_PUDDING_SLICE(FruitType.FIG, FoodType.ROLL),
	HAMIMELON_POPSICLE(FruitType.HAMIMELON, FoodType.STICK),
	HAMIMELON_SHAVED_ICE(FruitType.HAMIMELON, FoodType.JUICE),
	HAWBERRY_ROLL(FruitType.HAWBERRY, FoodType.ROLL, 0),
	HAWBERRY_SHEET(FruitType.HAWBERRY, FoodType.SHEET),
	HAWBERRY_STICK(FruitType.HAWBERRY, FoodType.STICK),
	ORANGE_SLICE(FruitType.ORANGE, FoodType.SLICE),
	LEMON_SLICE(FruitType.LEMON, FoodType.SLICE),
	BAKED_PEAR(FruitType.PEAR, FoodType.FRUIT),
	PINEAPPLE_PIE(FruitType.PINEAPPLE, FoodType.DESSERT),
	LEMON_TART(FruitType.LEMON, FoodType.DESSERT),
	FIG_TART(FruitType.FIG, FoodType.DESSERT),
	MANGO_MILKSHAKE(FruitType.MANGO, FoodType.JUICE),
	MANGO_SALAD(FruitType.MANGO, FoodType.BOWL, 0),
	DRIED_PERSIMMON(FruitType.PERSIMMON, FoodType.FRUIT),
	PERSIMMON_COOKIE(FruitType.PERSIMMON, FoodType.COOKIE),
	LEMON_COOKIE(FruitType.LEMON, FoodType.COOKIE),
	CRANBERRY_COOKIE(FruitType.CRANBERRY, FoodType.COOKIE),
	BAYBERRY_COOKIE(FruitType.BAYBERRY, FoodType.COOKIE),
	MANGOSTEEN_CAKE(FoodType.MANGOSTEEN_CAKE, FruitType.MANGOSTEEN, new EffectEntry(ModEffects.COMFORT, 1200)),
	PEAR_WITH_ROCK_SUGAR(FruitType.PEAR, FoodType.BOWL, new EffectEntry(ModEffects.COMFORT, 1200)),
	ORANGE_CHICKEN(FruitType.ORANGE, FoodType.MEAL, new EffectEntry(ModEffects.NOURISHMENT, 3600)),
	FIG_CHICKEN_STEW(FruitType.FIG, FoodType.MEAL, new EffectEntry(ModEffects.NOURISHMENT, 3600)),
	ORANGE_MARINATED_PORK(FruitType.ORANGE, FoodType.MEAL, new EffectEntry(ModEffects.NOURISHMENT, 3600)),
	BOWL_OF_PINEAPPLE_FRIED_RICE(FruitType.PINEAPPLE, FoodType.STAPLE, new EffectEntry(ModEffects.COMFORT, 6000)),
	PINEAPPLE_MARINATED_PORK(FruitType.PINEAPPLE, FoodType.MEAL, new EffectEntry(ModEffects.NOURISHMENT, 3600)),
	LYCHEE_CHICKEN(FruitType.LYCHEE, FoodType.MEAL, new EffectEntry(ModEffects.NOURISHMENT, 3600)),
	JELLY_BREAD(FruitType.SWEETBERRY, FoodType.SWEET, 1),
	;

	private final String name;

	public final FruitType fruit;
	public final FoodType type;
	public final ItemEntry<Item> item;
	public final EffectEntry[] effs;
	public final int overlay;

	FDFood(boolean allowJelly, int overlay, FruitType fruit, FoodType food, @Nullable String str, EffectEntry... effs) {
		this.fruit = fruit;
		this.type = food;
		this.overlay = overlay;
		this.name = name().toLowerCase(Locale.ROOT);
		this.item = FruitsDelight.REGISTRATE.item(name, p -> food.build(p, this))
				.transform(b -> food.model(b, overlay, fruit)).lang(str != null ? str : FDItems.toEnglishName(name))
				.tag(getTags(allowJelly, food.tags))
				.register();
		this.effs = effs;
	}

	FDFood(FruitType fruit, FoodType food, EffectEntry... effs) {
		this(false, 0, fruit, food, null, effs);
	}

	FDFood(FoodType food, FruitType fruit, EffectEntry... effs) {
		this.fruit = fruit;
		this.type = food;
		this.effs = effs;
		this.overlay = 0;
		this.name = name().toLowerCase(Locale.ROOT);
		this.item = FruitsDelight.REGISTRATE.item(name, p -> food.build(p, this))
				.model((ctx, pvd) -> pvd.generated(ctx))
				.setData(ProviderType.LANG, NonNullBiConsumer.noop())
				.tag(getTags(false, food.tags))
				.register();
	}

	FDFood(FruitType fruit, FoodType food, String str) {
		this(false, 0, fruit, food, str);
	}

	FDFood(FruitType fruit, FoodType food, int overlay) {
		this(true, overlay, fruit, food, null);
	}

	@SuppressWarnings({"unsafe", "unchecked"})
	private TagKey<Item>[] getTags(boolean allowJelly, TagKey<Item>[] tags) {
		var ans = new ArrayList<>(Arrays.stream(tags).toList());
		if (allowJelly) ans.add(TagGen.ALLOW_JELLY);
		return ans.toArray(TagKey[]::new);
	}

	public static void register() {

	}

	public Item getFruit() {
		return fruit.fruit.get();
	}

	public FDFoodItem get() {
		return (FDFoodItem) item.get();
	}

	@Override
	public FruitType fruit() {
		return fruit;
	}

	@Override
	public FoodType getType() {
		return type;
	}

	@Override
	public EffectEntry[] getEffects() {
		return effs;
	}
}
