package com.thedemgel.basicplugin;

import com.thedemgel.basicplugin.commands.PlayerCommands;
import com.thedemgel.basicplugin.configuration.BasicConfiguration;
import com.thedemgel.basicplugin.configuration.WorldConfigurationNode;
import com.thedemgel.basicplugin.world.generator.darkdesert.DarkDesertGenerator;
import com.thedemgel.yamlresourcebundle.YamlResourceBundle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.spout.api.Engine;
import org.spout.api.command.annotated.AnnotatedCommandExecutorFactory;
import org.spout.api.component.DatatableComponent;
import org.spout.api.component.entity.ObserverComponent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.Plugin;
import org.spout.api.plugin.PluginLogger;
import org.spout.api.plugin.services.ProtectionService;
import org.spout.api.plugin.services.ServiceManager;
import org.spout.api.util.FlatIterator;
import org.spout.vanilla.ChatStyle;
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
public class BasicPlugin extends Plugin {

	private Engine engine;
	private static BasicPlugin instance;
	private BasicConfiguration config;
	private ResourceBundle rb;

	@Override
	public void onLoad() {
		instance = this;
		((PluginLogger) getLogger()).setTag(ChatStyle.RESET + "[" + ChatStyle.GOLD + "BasicPlugin" + ChatStyle.RESET + "] ");
		engine = getEngine();
		config = new BasicConfiguration(getDataFolder());
		config.load();
		getLogger().info("loaded");

		rb = YamlResourceBundle.getBundle("lang.test", Locale.forLanguageTag(BasicConfiguration.DEFAULT_LANG.getString()), getDataFolder());
	}

	@Override
	public void onEnable() {
		//Commands
		//final CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(getEngine(), new SimpleAnnotatedCommandExecutorFactory());
		//final RootCommand root = engine.getRootCommand();
		AnnotatedCommandExecutorFactory.create(new PlayerCommands(this), engine.getCommandManager().getCommand("bp"));
		//root.addSubCommands(this, PlayerCommands.class, commandRegFactory);
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
		ArrayList<World> worlds = new ArrayList<World>();

		for (WorldConfigurationNode worldNode : BasicConfiguration.WORLDS.getAll()) {

			VanillaGenerator generator = (VanillaGenerator) new DarkDesertGenerator();
			World world = engine.loadWorld(worldNode.getWorldName(), generator);

			// Apply general settings
			final DatatableComponent data = world.getDatatable();
			data.put(VanillaData.GAMEMODE, GameMode.get("creative"));
			data.put(VanillaData.DIFFICULTY, Difficulty.get("normal"));
			data.put(VanillaData.DIMENSION, Dimension.get("nether"));

			world.addLightingManager(VanillaLighting.BLOCK_LIGHT);
			world.addLightingManager(VanillaLighting.SKY_LIGHT);

			worlds.add(world);
		}

		final int radius = VanillaConfiguration.SPAWN_RADIUS.getInt();
		final int protectionRadius = VanillaConfiguration.SPAWN_PROTECTION_RADIUS.getInt();
		SpawnLoader loader = new SpawnLoader(1);

		if (worlds.isEmpty()) {
			return;
		}

		engine.getServiceManager().register(ProtectionService.class, new VanillaProtectionService(), this, ServiceManager.ServicePriority.Highest);

		for (World world : worlds) {
			WorldConfigurationNode worldConfig = BasicConfiguration.WORLDS.get(world);
			boolean newWorld = world.getAge() <= 0;
			if (worldConfig.LOADED_SPAWN.getBoolean() || newWorld) {
				Point point = world.getSpawnPoint().getPosition();
				int cx = point.getBlockX() >> Chunk.BLOCKS.BITS;
				int cz = point.getBlockZ() >> Chunk.BLOCKS.BITS;

				((VanillaProtectionService) engine.getServiceManager().getRegistration(ProtectionService.class).getProvider()).addProtection(new SpawnProtection(world.getName() + " Spawn Protection", world, point, protectionRadius));

// Load or generate spawn area
				int effectiveRadius = newWorld ? (2 * radius) : radius;
				loader.load(world, cx, cz, effectiveRadius, newWorld);

				if (worldConfig.LOADED_SPAWN.getBoolean()) {
					Entity e = world.createAndSpawnEntity(point, LoadOption.LOAD_GEN, ObserverComponent.class);
					e.setObserver(new FlatIterator(cx, 0, cz, 16, effectiveRadius));
				}

// Grab safe spawn if newly created world.
				if (newWorld && world.getGenerator() instanceof VanillaGenerator) {
					Point spawn = ((VanillaGenerator) world.getGenerator()).getSafeSpawn(world);
					world.setSpawnPoint(new Transform(spawn, Quaternion.IDENTITY, Vector3.ONE));
				}
			}

			world.add(NetherSky.class).setHasWeather(false);
		}
	}

	public ResourceBundle getLang() {
		return rb;
	}
}
