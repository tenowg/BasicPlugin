package com.thedemgel.basicplugin;

import com.thedemgel.basicplugin.commands.PlayerCommands;
import com.thedemgel.basicplugin.resourcebundle.BasicBundles;
import com.thedemgel.basicplugin.world.generator.darkdesert.DarkDesertGenerator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.spout.api.Engine;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.RootCommand;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.component.impl.DatatableComponent;
import org.spout.api.component.impl.ObserverComponent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.PluginLogger;
import org.spout.api.plugin.ServiceManager;
import org.spout.api.plugin.services.ProtectionService;
import org.spout.api.util.FlatIterator;
import org.spout.vanilla.component.world.sky.NetherSky;
import org.spout.vanilla.data.Difficulty;
import org.spout.vanilla.data.Dimension;
import org.spout.vanilla.data.GameMode;
import org.spout.vanilla.data.VanillaData;
import org.spout.vanilla.data.configuration.VanillaConfiguration;
import org.spout.vanilla.service.VanillaProtectionService;
import org.spout.vanilla.service.protection.SpawnProtection;
import org.spout.vanilla.util.thread.SpawnLoader;
import org.spout.vanilla.world.generator.VanillaGenerator;
import org.spout.vanilla.world.lighting.VanillaLighting;

/**
 *
 * @author Craig <tenowg at thedemgel.com>
 */
public class BasicPlugin extends CommonPlugin {

	private Engine engine;
	private static BasicPlugin instance;
	private BasicConfiguration config;

	@Override
	public void onLoad() {
		instance = this;
		((PluginLogger) getLogger()).setTag(new ChatArguments(ChatStyle.RESET, "[", ChatStyle.GOLD, "BasicPlugin", ChatStyle.RESET, "] "));
		engine = getEngine();
		config = new BasicConfiguration(getDataFolder());
		config.load();
		getLogger().info("loaded");
		
		BasicBundles bb = new BasicBundles();
		ResourceBundle rb = bb.getBundle("ptest", Locale.ENGLISH);
		getLogger().info(rb.getString("name"));
		
	}

	@Override
	public void onEnable() {
		//Commands
		final CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());
		final RootCommand root = engine.getRootCommand();
		root.addSubCommands(this, PlayerCommands.class, commandRegFactory);
		//root.addSubCommands(this, AdminCommands.class, commandRegFactory);

		engine.getEventManager().registerEvents(new PlayerListener(this), this);

		setupWorlds();

		getLogger().log(Level.INFO, "v{0} enabled.", getDescription().getVersion());
	}

	@Override
	public void onDisable() {
		getLogger().log(Level.INFO, "v{0} disabled.", getDescription().getVersion());
	}

	public static BasicPlugin getInstance() {
		return instance;
	}

	public void setupWorlds() {
		VanillaGenerator generator = (VanillaGenerator) new DarkDesertGenerator();
		World world = engine.loadWorld(generator.getName(), generator);

		// Apply general settings
		final DatatableComponent data = world.getComponentHolder().getData();
		data.put(VanillaData.GAMEMODE, GameMode.get("creative"));
		data.put(VanillaData.DIFFICULTY, Difficulty.get("normal"));
		data.put(VanillaData.DIMENSION, Dimension.get("nether"));

		world.addLightingManager(VanillaLighting.BLOCK_LIGHT);
		world.addLightingManager(VanillaLighting.SKY_LIGHT);

		final int radius = VanillaConfiguration.SPAWN_RADIUS.getInt();
		final int protectionRadius = VanillaConfiguration.SPAWN_PROTECTION_RADIUS.getInt();
		SpawnLoader loader = new SpawnLoader(1);

		engine.getServiceManager().register(ProtectionService.class, new VanillaProtectionService(), this, ServiceManager.ServicePriority.Highest);

		WorldConfigurationNode worldConfig = BasicConfiguration.WORLDS.get(world);
		boolean newWorld = world.getAge() <= 0;
		Point point = world.getSpawnPoint().getPosition();
		int cx = point.getBlockX() >> Chunk.BLOCKS.BITS;
		int cz = point.getBlockZ() >> Chunk.BLOCKS.BITS;

		((VanillaProtectionService) engine.getServiceManager().getRegistration(ProtectionService.class).getProvider()).addProtection(new SpawnProtection(world.getName() + " Spawn Protection", world, point, protectionRadius));

// Load or generate spawn area
		int effectiveRadius = newWorld ? (2 * radius) : radius;
		loader.load(world, cx, cz, effectiveRadius, newWorld);

		if (worldConfig.LOADED_SPAWN.getBoolean()) {
			Entity e = world.createAndSpawnEntity(point, ObserverComponent.class, LoadOption.LOAD_GEN);
			e.setObserver(new FlatIterator(cx, 0, cz, 16, effectiveRadius));
		}

// Grab safe spawn if newly created world.
		if (newWorld && world.getGenerator() instanceof VanillaGenerator) {
			Point spawn = ((VanillaGenerator) world.getGenerator()).getSafeSpawn(world);
			world.setSpawnPoint(new Transform(spawn, Quaternion.IDENTITY, Vector3.ONE));
		}

// Grab safe spawn if newly created world.
		if (newWorld && world.getGenerator() instanceof VanillaGenerator) {
			Point spawn = ((VanillaGenerator) world.getGenerator()).getSafeSpawn(world);
			world.setSpawnPoint(new Transform(spawn, Quaternion.IDENTITY, Vector3.ONE));
		}

		world.getComponentHolder().add(NetherSky.class).setHasWeather(false);
	}
}
