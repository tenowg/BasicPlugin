package com.thedemgel.basicplugin.commands;

import com.thedemgel.basicplugin.BasicPlugin;
import com.thedemgel.basicplugin.component.living.passive.Turtle;
import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.component.Component;
import org.spout.api.entity.Entity;
import org.spout.api.entity.EntityPrefab;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.discrete.Point;
import org.spout.api.plugin.Platform;

/**
 *
 * @author Craig <tenowg at thedemgel.com>
 */
public class PlayerCommands {

	private final BasicPlugin plugin;

	public PlayerCommands(BasicPlugin instance) {
		this.plugin = instance;
	}

	@Command(aliases = "spawnit", usage = "", desc = "spawn a turtle", min = 0, max = 0)
	public void spawnit(CommandContext args, CommandSource source) throws CommandException {
		Player player;
		
		if (Spout.getPlatform() != Platform.CLIENT) {
			player = (Player) source;
		} else {
			player = ((Client) Spout.getEngine()).getActivePlayer();
		}
		
		final Point pos = player.getScene().getPosition();
		
		Class<? extends Component> clazz = Turtle.class;
		
		Entity entity = pos.getWorld().createEntity(pos, clazz);
		entity.setSavable(true);
		
		if (Spout.getPlatform() == Platform.CLIENT) {
			EntityPrefab turtlePrefab = (EntityPrefab) Spout.getFilesystem().getResource("entity://BasicPlugin/entities/turtle/turtle.sep");
			entity = turtlePrefab.createEntity(pos);		
		}
		
		pos.getWorld().spawnEntity(entity);
	}
}
