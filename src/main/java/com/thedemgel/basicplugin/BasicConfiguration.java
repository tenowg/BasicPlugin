package com.thedemgel.basicplugin;

import java.io.File;
import java.util.logging.Level;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

public class BasicConfiguration extends ConfigurationHolderConfiguration {

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
