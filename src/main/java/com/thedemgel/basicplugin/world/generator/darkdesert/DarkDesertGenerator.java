package com.thedemgel.basicplugin.world.generator.darkdesert;

import java.util.Random;

import net.royawesome.jlibnoise.NoiseQuality;
import net.royawesome.jlibnoise.module.modifier.ScalePoint;
import net.royawesome.jlibnoise.module.source.Perlin;

import org.spout.api.generator.WorldGeneratorUtils;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.generator.biome.BiomePopulator;
import org.spout.api.generator.biome.BiomeSelector;
import org.spout.api.generator.biome.selector.BiomeSelectorLayer;
import org.spout.api.generator.biome.selector.LayeredBiomeSelector;
import org.spout.api.generator.biome.selector.PerlinRangeLayer;
import org.spout.api.generator.biome.selector.RidgedMultiRangeLayer;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.GenericMath;
import org.spout.api.math.Vector3;
import org.spout.api.util.LogicUtil;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.api.util.map.TIntPairObjectHashMap;
import org.spout.vanilla.data.Climate;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.material.block.Liquid;
import org.spout.vanilla.util.MathHelper;
import org.spout.vanilla.world.generator.biome.VanillaBiomeGenerator;
import org.spout.vanilla.world.generator.biome.VanillaBiomes;
import org.spout.vanilla.world.generator.normal.biome.NormalBiome;
import org.spout.vanilla.world.generator.normal.biome.selector.WhittakerLayer;
import org.spout.vanilla.world.generator.normal.populator.CavePopulator;
import org.spout.vanilla.world.generator.normal.populator.DungeonPopulator;
import org.spout.vanilla.world.generator.normal.populator.FallingLiquidPopulator;
import org.spout.vanilla.world.generator.normal.populator.GroundCoverPopulator;
import org.spout.vanilla.world.generator.normal.populator.MineshaftPopulator;
import org.spout.vanilla.world.generator.normal.populator.OrePopulator;
import org.spout.vanilla.world.generator.normal.populator.PondPopulator;
import org.spout.vanilla.world.generator.normal.populator.RavinePopulator;
import org.spout.vanilla.world.generator.normal.populator.RockyShieldPopulator;
import org.spout.vanilla.world.generator.normal.populator.SnowPopulator;
import org.spout.vanilla.world.generator.normal.populator.StrongholdPopulator;
import org.spout.vanilla.world.generator.normal.populator.TemplePopulator;

public class DarkDesertGenerator extends VanillaBiomeGenerator {
	// world constants

	public static final int HEIGHT = 256;
	public static final int SEA_LEVEL = 62;
	private static final byte BEDROCK_DEPTH = 5;
	// noise for generation
	private static final Perlin PERLIN = new Perlin();
	private static final ScalePoint NOISE = new ScalePoint();
	// smoothing stuff
	private static final int SMOOTH_SIZE = 4;

	static {
		PERLIN.setFrequency(0.01);
		PERLIN.setLacunarity(2);
		PERLIN.setNoiseQuality(NoiseQuality.BEST);
		PERLIN.setPersistence(0.5);
		PERLIN.setOctaveCount(16);

		NOISE.SetSourceModule(0, PERLIN);
		NOISE.setxScale(1);
		NOISE.setyScale(1);
		NOISE.setzScale(1);
	}

	@Override
	public void registerBiomes() {
		// if you want to check out a particular biome, use this!
		//setSelector(new PerBlockBiomeSelector(VanillaBiomes.TUNDRA));
		setSelector(new LayeredBiomeSelector(buildSelectorStack(1), VanillaBiomes.OCEAN));
		addGeneratorPopulators(
			new GroundCoverPopulator(), new RockyShieldPopulator(),
			new CavePopulator(), new RavinePopulator());
		addPopulators(
			new MineshaftPopulator(), new StrongholdPopulator(), new TemplePopulator(),
			new PondPopulator(), new DungeonPopulator(), new OrePopulator(),
			new BiomePopulator(),
			new FallingLiquidPopulator(), new SnowPopulator());
		register(VanillaBiomes.OCEAN);
		register(VanillaBiomes.FROZEN_OCEAN);
		register(VanillaBiomes.PLAINS);
		register(VanillaBiomes.DESERT);
		register(VanillaBiomes.DESERT_HILLS);
		register(VanillaBiomes.SMALL_MOUNTAINS);
		register(VanillaBiomes.MOUNTAINS);
		register(VanillaBiomes.BEACH);
		register(VanillaBiomes.SWAMP);
		//register(VanillaBiomes.FOREST);
		register(VanillaBiomes.FOREST_HILLS);
		register(VanillaBiomes.FROZEN_RIVER);
		register(VanillaBiomes.RIVER);
		//register(VanillaBiomes.JUNGLE);
		//register(VanillaBiomes.JUNGLE_HILLS);
		//register(VanillaBiomes.MUSHROOM);
		//register(VanillaBiomes.MUSHROOM_SHORE);
		register(VanillaBiomes.TUNDRA);
		register(VanillaBiomes.TUNDRA_HILLS);
		register(VanillaBiomes.TAIGA);
		register(VanillaBiomes.TAIGA_HILLS);
	}

	@Override
	public String getName() {
		return "DarkDesert";
	}

	@Override
	protected void generateTerrain(CuboidBlockMaterialBuffer blockData, int x, int y, int z, BiomeManager biomes, long seed) {
		if (y >= HEIGHT) {
			return;
		}
		final Vector3 size = blockData.getSize();
		final int sizeX = size.getFloorX();
		final int sizeY = Math.min(size.getFloorY(), HEIGHT);
		final int sizeZ = size.getFloorZ();
		PERLIN.setSeed((int) seed);
		final double[][][] noise = WorldGeneratorUtils.fastNoise(NOISE, sizeX, sizeY, sizeZ, 4, x, y, z);
		final BiomeSelector selector = getSelector();
		final TIntPairObjectHashMap<NormalBiome> biomeCache = new TIntPairObjectHashMap<NormalBiome>();
		for (int xx = 0; xx < sizeX; xx++) {
			for (int zz = 0; zz < sizeZ; zz++) {
				double maxSum = 0;
				double minSum = 0;
				int count = 0;
				for (int sx = -SMOOTH_SIZE; sx <= SMOOTH_SIZE; sx++) {
					for (int sz = -SMOOTH_SIZE; sz <= SMOOTH_SIZE; sz++) {
						final NormalBiome adjacent;
						if (xx + sx < 0 || zz + sz < 0
							|| xx + sx >= sizeX || zz + sz >= sizeZ) {
							if (biomeCache.containsKey(x + xx + sx, z + zz + sz)) {
								adjacent = biomeCache.get(x + xx + sx, z + zz + sz);
							} else {
								adjacent = (NormalBiome) selector.pickBiome(x + xx + sx, y, z + zz + sz, seed);
								biomeCache.put(x + xx + sx, z + zz + sz, adjacent);
							}
						} else {
							adjacent = (NormalBiome) biomes.getBiome(xx + sx, y, zz + sz);
						}
						minSum += adjacent.getMinElevation();
						maxSum += adjacent.getMaxElevation();
						count++;
					}
				}
				final double minElevation = minSum / count;
				final double smoothHeight = (maxSum / count - minElevation) / 2;
				for (int yy = 0; yy < sizeY; yy++) {
					final double noiseValue = pow(noise[xx][yy][zz], 2) - 1 / smoothHeight * (y + yy - smoothHeight - minElevation);
					if (noiseValue >= 0) {
						blockData.set(x + xx, y + yy, z + zz, VanillaMaterials.STONE);
					} else {
						if (y + yy <= SEA_LEVEL) {
							if (y + yy == SEA_LEVEL && ((NormalBiome) biomes.getBiome(xx, 0, zz)).getClimate() == Climate.COLD) {
								blockData.set(x + xx, y + yy, z + zz, VanillaMaterials.ICE);
							} else {
								blockData.set(x + xx, y + yy, z + zz, VanillaMaterials.STATIONARY_WATER);
							}
						} else {
							blockData.set(x + xx, y + yy, z + zz, VanillaMaterials.AIR);
						}
					}
				}
				if (y == 0) {
					final byte bedrockDepth =
						(byte) (MathHelper.hashToFloat(x + xx, z + zz, (int) seed) * BEDROCK_DEPTH + 1);
					for (byte yy = 0; yy < bedrockDepth; yy++) {
						blockData.set(x + xx, yy, z + zz, VanillaMaterials.BEDROCK);
					}
				}
			}
		}
	}

	private static double pow(double val, int pow) {
		val = val * 0.5 + 0.5;
		for (int i = 1; i < pow; i++) {
			val *= val;
		}
		return (val - 0.5) / 0.5;
	}

	@Override
	public Point getSafeSpawn(World world) {
		short shift = 0;
		final BiomeSelector selector = getSelector();
		final long seed = world.getSeed();
		while (LogicUtil.equalsAny(selector.pickBiome(shift, 0, seed),
			VanillaBiomes.OCEAN, VanillaBiomes.BEACH, VanillaBiomes.RIVER,
			VanillaBiomes.SWAMP, VanillaBiomes.MUSHROOM)
			&& shift < 1600) {
			shift += 16;
		}
		final Random random = GenericMath.getRandom();
		for (byte attempts = 0; attempts < 32; attempts++) {
			final int x = random.nextInt(256) - 127 + shift;
			final int z = random.nextInt(256) - 127;
			final int y = getHighestSolidBlock(world, x, z);
			if (y != -1) {
				return new Point(world, x, y + 0.5f, z);
			}
		}
		return new Point(world, shift, 80, 0);
	}

	private int getHighestSolidBlock(World world, int x, int z) {
		int y = HEIGHT - 1;
		while (world.getBlockMaterial(x, y, z).isInvisible()) {
			if (--y == 0 || world.getBlockMaterial(x, y, z) instanceof Liquid) {
				return -1;
			}
		}
		return ++y;
	}

	@Override
	public int[][] getSurfaceHeight(World world, int chunkX, int chunkY) {
		int[][] heights = new int[Chunk.BLOCKS.SIZE][Chunk.BLOCKS.SIZE];
		for (int x = 0; x < Chunk.BLOCKS.SIZE; x++) {
			for (int z = 0; z < Chunk.BLOCKS.SIZE; z++) {
				heights[x][z] = SEA_LEVEL;
			}
		}
		return heights;
	}

	private static BiomeSelectorLayer buildSelectorStack(double scale) {
//
// STANDARD FEATURES
//
// rivers
		final RidgedMultiRangeLayer rivers =
			new RidgedMultiRangeLayer(2).
			setOctaveCount(1).
			setFrequency(0.005 / scale);
// hills
		final PerlinRangeLayer hills =
			new PerlinRangeLayer(1).
			setOctaveCount(2).
			setFrequency(0.004 / scale);
// frozen oceans
		final PerlinRangeLayer frozenOceans =
			new PerlinRangeLayer(3).
			setOctaveCount(2).
			setFrequency(0.004 / scale);
//
// LAND LAYERS
//
// desert
		final BiomeSelectorLayer basicDesert =
			hills.clone().
			addElement(VanillaBiomes.DESERT, -1, 0.5f).
			addElement(VanillaBiomes.DESERT_HILLS, 0.5f, 1);
// desert land
		final BiomeSelectorLayer desert =
			rivers.clone().
			addElement(basicDesert, -1, 0.16f).
			addElement(VanillaBiomes.RIVER, 0.16f, 1);
// forest
		final BiomeSelectorLayer basicForest =
			hills.clone().
			//addElement(VanillaBiomes.FOREST, -1, 0.5f).
			addElement(VanillaBiomes.FOREST_HILLS, -1, 1);
// forest land
		final BiomeSelectorLayer forest =
			rivers.clone().
			addElement(basicForest, -1, 0.16f).
			addElement(VanillaBiomes.RIVER, 0.16f, 1);
// jungle
		/*final BiomeSelectorLayer basicJungle =
			hills.clone().
			addElement(VanillaBiomes.JUNGLE, -1, 0.5f).
			addElement(VanillaBiomes.JUNGLE_HILLS, 0.5f, 1);
// jungle land
		final BiomeSelectorLayer jungle =
			rivers.clone().
			addElement(basicJungle, -1, 0.16f).
			addElement(VanillaBiomes.RIVER, 0.16f, 1);*/
// plains
		final BiomeSelectorLayer plains =
			rivers.clone().
			addElement(VanillaBiomes.PLAINS, -1, 0.16f).
			addElement(VanillaBiomes.RIVER, 0.16f, 1);
// swamp
		final BiomeSelectorLayer swamp =
			rivers.clone().
			addElement(VanillaBiomes.SWAMP, -1, 0.16f).
			addElement(VanillaBiomes.RIVER, 0.16f, 1);
// taiga
		final BiomeSelectorLayer basicTaiga =
			hills.clone().
			addElement(VanillaBiomes.TAIGA, -1, 0.5f).
			addElement(VanillaBiomes.TAIGA_HILLS, 0.5f, 1);
// taiga sub-land
		final BiomeSelectorLayer subTaiga =
			rivers.clone().
			addElement(basicTaiga, -1, 0.16f).
			addElement(VanillaBiomes.FROZEN_RIVER, 0.16f, 1);
// taiga land
		final BiomeSelectorLayer taiga =
			frozenOceans.clone().
			addElement(subTaiga, -1, 0.4f).
			addElement(VanillaBiomes.FROZEN_OCEAN, 0.4f, 1);
// tundra
		final BiomeSelectorLayer basicTundra =
			hills.clone().
			addElement(VanillaBiomes.TUNDRA, -1, 0.5f).
			addElement(VanillaBiomes.TUNDRA_HILLS, 0.5f, 1);
// tundra sub-land
		final BiomeSelectorLayer subTundra =
			rivers.clone().
			addElement(basicTundra, -1, 0.16f).
			addElement(VanillaBiomes.FROZEN_RIVER, 0.16f, 1);
// tundra land
		final BiomeSelectorLayer tundra =
			frozenOceans.clone().
			addElement(subTundra, -1, 0.4f).
			addElement(VanillaBiomes.FROZEN_OCEAN, 0.4f, 1);
//
// PRIMARY LAYERS
//
// mushroom
		/*final BiomeSelectorLayer mushroom =
			new PerlinRangeLayer(11).
			setOctaveCount(2).
			setFrequency(0.004 / scale).
			addElement(VanillaBiomes.OCEAN, -1, 0.75f).
			addElement(VanillaBiomes.MUSHROOM_SHORE, 0.75f, 0.85f).
			addElement(VanillaBiomes.MUSHROOM, 0.85f, 1);*/
// shore
		final BiomeSelectorLayer shore =
			rivers.clone().
			addElement(VanillaBiomes.BEACH, -1, 0.16f).
			addElement(VanillaBiomes.RIVER, 0.16f, 1);
// land
		final BiomeSelectorLayer land =
			new WhittakerLayer(7).
			setHumidityOctaveCount(2).
			setHumidityFrequency(0.002 / scale).
			setTemperatureOctaveCount(2).
			setTemperatureFrequency(0.001 / scale).
			addElement(desert, 20, 300).
			addElement(forest, 15, 100).
			//addElement(jungle, 20, 300).
			addElement(plains, 10, 50).
			addElement(swamp, 10, 300).
			addElement(taiga, 0, 50).
			addElement(tundra, -5, 50);
// small mountains
		final BiomeSelectorLayer smallMountains =
			rivers.clone().
			addElement(VanillaBiomes.SMALL_MOUNTAINS, -1, 0.16f).
			addElement(VanillaBiomes.RIVER, 0.16f, 1);
// mountains
		final BiomeSelectorLayer mountains =
			rivers.clone().
			addElement(VanillaBiomes.MOUNTAINS, -1, 0.16f).
			addElement(VanillaBiomes.RIVER, 0.16f, 1);
//
// STARTING LAYER
//
// start
		final BiomeSelectorLayer start =
			new PerlinRangeLayer(5).
			setOctaveCount(2).
			setFrequency(0.0028 / scale).
			//addElement(mushroom, -1, -0.5f).
			addElement(VanillaBiomes.OCEAN, -1, -0.75f).
			addElement(shore, -0.75f, -.30f).
			addElement(land, -0.30f, 0.675f).
			addElement(smallMountains, 0.675f, 0.71f).
			addElement(mountains, 0.71f, 1);
		return start;
	}
}