package com.thedemgel.basicplugin.configuration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.spout.api.geo.World;
import org.spout.cereal.config.ConfigurationException;
import org.spout.cereal.config.yaml.YamlConfiguration;

public class WorldConfiguration extends YamlConfiguration {

	private final Map<String, WorldConfigurationNode> worldNodes = new HashMap<String, WorldConfigurationNode>();
	public static WorldConfigurationNode DARKDESERT;
	public static WorldConfigurationNode LIGHTDESERT;

	public WorldConfiguration(File dataFolder) {
		super(new File(dataFolder, "worlds.yml"));
//TODO: Allow the creation of sub-sections for configuration holders
		DARKDESERT = get("DarkDesert").setDefaults("nether", "darkdesert");
		LIGHTDESERT = get("lightdesert").setDefaults("normal", "darkdesert");
	}

	public Collection<WorldConfigurationNode> getAll() {
		return worldNodes.values();
	}

	/**
	 * Gets the world configuration of a certain world<br> Creates a new one
	 * if it doesn't exist
	 *
	 * @param world of the configuration
	 * @return the World configuration node
	 */
	public WorldConfigurationNode get(World world) {
		return get(world.getName());
	}

	/**
	 * Gets the world configuration of a certain world<br> Creates a new one
	 * if it doesn't exist
	 *
	 * @param worldname of the configuration
	 * @return the World configuration node
	 */
	public final WorldConfigurationNode get(String worldname) {
		synchronized (worldNodes) {
			WorldConfigurationNode node = worldNodes.get(worldname);
			if (node == null) {
				node = new WorldConfigurationNode(this, worldname);
				worldNodes.put(worldname, node);
			}
			return node;
		}
	}

	@Override
	public void load() throws ConfigurationException {
		super.load();
		for (WorldConfigurationNode node : getAll()) {
			node.load();
		}
	}

	@Override
	public void save() throws ConfigurationException {
		for (WorldConfigurationNode node : getAll()) {
			node.save();
		}
		super.save();
	}
}