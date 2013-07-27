package com.thedemgel.basicplugin.configuration;

import com.thedemgel.basicplugin.BasicPlugin;
import java.io.File;
import java.util.logging.Level;
import org.spout.cereal.config.ConfigurationException;
import org.spout.cereal.config.ConfigurationHolder;
import org.spout.cereal.config.ConfigurationHolderConfiguration;
import org.spout.cereal.config.yaml.YamlConfiguration;

public class BasicConfiguration extends ConfigurationHolderConfiguration {

	public static final ConfigurationHolder DEFAULT_LANG = new ConfigurationHolder("en", "lang", "default");
	
	public static final WorldConfiguration WORLDS = new WorldConfiguration(BasicPlugin.getInstance().getDataFolder());
	
	public BasicConfiguration(File dataFolder) {
		super(new YamlConfiguration(new File(dataFolder, "config.yml")));
	}

	@Override
	public void load() {
		try {
			WORLDS.load();
			WORLDS.save();
			super.load();
			super.save();
		} catch (ConfigurationException e) {
			BasicPlugin.getInstance().getLogger().log(Level.WARNING, "Error loading Vanilla configuration: ", e);
		}
	}

	@Override
	public void save() {
		try {
			WORLDS.save();
			super.save();
		} catch (ConfigurationException e) {
			BasicPlugin.getInstance().getLogger().log(Level.WARNING, "Error saving Vanilla configuration: ", e);
		}
	}
}
