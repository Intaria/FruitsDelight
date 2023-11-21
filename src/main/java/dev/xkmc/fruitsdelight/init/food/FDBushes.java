package dev.xkmc.fruitsdelight.init.food;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.fruitsdelight.content.block.BushFruitItem;
import dev.xkmc.fruitsdelight.content.block.FruitBushBlock;
import dev.xkmc.fruitsdelight.init.FruitsDelight;
import dev.xkmc.fruitsdelight.init.data.PlantDataEntry;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.List;
import java.util.Locale;

public enum FDBushes implements PlantDataEntry<FDBushes> {
	BLUEBERRY(2, 0.3f, true, 64),
	LEMON(4, 0.3f, false, 64),
	;

	private final BlockEntry<FruitBushBlock> bush;
	private final ItemEntry<BushFruitItem> fruit;

	private final int rarity;

	public final ResourceKey<ConfiguredFeature<?, ?>> configKey;
	public final ResourceKey<PlacedFeature> placementKey;

	FDBushes(int food, float sat, boolean fast, int rarity) {
		String name = name().toLowerCase(Locale.ROOT);
		this.configKey = ResourceKey.create(Registries.CONFIGURED_FEATURE,
				new ResourceLocation(FruitsDelight.MODID, name + "_bush"));
		this.placementKey = ResourceKey.create(Registries.PLACED_FEATURE,
				new ResourceLocation(FruitsDelight.MODID, name + "_bush"));
		this.rarity = rarity;

		bush = FruitsDelight.REGISTRATE
				.block(name + "_bush", p -> new FruitBushBlock(BlockBehaviour.Properties.copy(Blocks.AZALEA), this::getFruit))
				.blockstate(this::buildBushModel)
				.loot(this::buildLoot)
				.tag(BlockTags.MINEABLE_WITH_AXE, BlockTags.SWORD_EFFICIENT)
				.item().build()
				.register();

		fruit = FruitsDelight.REGISTRATE
				.item(name, p -> new BushFruitItem(getBush(), p.food(food(food, sat, fast))))
				.register();

	}

	public void registerComposter() {
		ComposterBlock.COMPOSTABLES.put(getFruit(), 0.65f);
		ComposterBlock.COMPOSTABLES.put(getBush(), 0.65f);
	}

	public void registerConfigs(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
		BlockState state = getBush().defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3);
		FeatureUtils.register(ctx, configKey, Feature.RANDOM_PATCH,
				FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
						new SimpleBlockConfiguration(BlockStateProvider.simple(state)),
						List.of(Blocks.GRASS_BLOCK)));
	}

	public void registerPlacements(BootstapContext<PlacedFeature> ctx) {
		ctx.register(placementKey, new PlacedFeature(
				ctx.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(configKey),
				List.of(
						RarityFilter.onAverageOnceEvery(rarity),
						InSquarePlacement.spread(),
						PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
						BiomeFilter.biome()
				)));

	}

	@Override
	public String getName() {
		return name().toLowerCase(Locale.ROOT);
	}

	@Override
	public ResourceKey<PlacedFeature> getPlacementKey() {
		return placementKey;
	}

	private void buildLoot(RegistrateBlockLootTables pvd, FruitBushBlock block) {
		pvd.add(block, LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.add(LootItem.lootTableItem(getBush().asItem())
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties()
												.hasProperty(FruitBushBlock.AGE, 0))
										.invert()))
						.when(ExplosionCondition.survivesExplosion()))
				.withPool(LootPool.lootPool()
						.add(LootItem.lootTableItem(getFruit())
								.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
										.setProperties(StatePropertiesPredicate.Builder.properties()
												.hasProperty(FruitBushBlock.AGE, 3)))
								.apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1)))
						.when(ExplosionCondition.survivesExplosion()))
		);
	}

	public FruitBushBlock getBush() {
		return bush.get();
	}

	public BushFruitItem getFruit() {
		return fruit.get();
	}

	private void buildBushModel(DataGenContext<Block, FruitBushBlock> ctx, RegistrateBlockstateProvider pvd) {
		pvd.getVariantBuilder(ctx.get()).forAllStates(state -> {
			int age = state.getValue(FruitBushBlock.AGE);
			String id = ctx.getName();
			String parent = "bush";
			if (age == 0) {
				id += "_small";
				parent += "_small";
			}
			if (age == 2) id += "_flowers";
			if (age == 3) id += "_fruits";
			return ConfiguredModel.builder().modelFile(pvd.models().getBuilder(id)
					.parent(new ModelFile.UncheckedModelFile(pvd.modLoc("block/" + parent)))
					.texture("face", "block/" + id + "_face")
					.texture("cross", "block/" + id + "_cross")).build();
		});
	}

	private static FoodProperties food(int food, float sat, boolean fast) {
		var ans = new FoodProperties.Builder()
				.nutrition(food).saturationMod(sat);
		if (fast) ans.fast();
		return ans.build();
	}

	public static void register() {

	}

}