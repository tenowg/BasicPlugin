package com.thedemgel.basicplugin.commands;

import com.thedemgel.basicplugin.BasicPlugin;
import com.thedemgel.basicplugin.component.living.passive.Turtle;
import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.CommandDescription;
import org.spout.api.component.Component;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntityPrefab;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.discrete.Point;

/**
 *
 * @author Craig <tenowg at thedemgel.com>
 */
public class PlayerCommands {

	private final BasicPlugin plugin;

	public PlayerCommands(BasicPlugin instance) {
		this.plugin = instance;
	}

	@CommandDescription(aliases = "spawnit", usage = "", desc = "spawn a turtle")
	public void spawnit(CommandSource source, CommandArguments args) throws CommandException {
		Player player;
		
		if (plugin.getEngine().getPlatform() != Platform.CLIENT) {
			player = (Player) source;
		} else {
			player = ((Client) plugin.getEngine()).getPlayer();
		}
		
		final Point pos = player.getPhysics().getPosition();
		
		Class<? extends Component> clazz = Turtle.class;
		
		Entity entity = pos.getWorld().createEntity(pos, clazz);
		entity.setSavable(true);
		
		if (plugin.getEngine().getPlatform() == Platform.CLIENT) {
			EntityPrefab turtlePrefab = (EntityPrefab) plugin.getEngine().getFileSystem().getResource("entity://BasicPlugin/entities/turtle/turtle.sep");
			entity = turtlePrefab.createEntity(pos);		
		}
		
		pos.getWorld().spawnEntity(entity);
	}
}
