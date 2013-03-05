package com.thedemgel.basicplugin.component.living.passive;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.inventory.ItemStack;
import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.component.entity.living.Living;
import org.spout.vanilla.component.entity.living.Passive;
import org.spout.vanilla.component.entity.misc.DeathDrops;
import org.spout.vanilla.component.entity.misc.Health;
import org.spout.vanilla.material.VanillaMaterials;
import org.spout.vanilla.protocol.entity.creature.CreatureProtocol;
import org.spout.vanilla.protocol.entity.creature.CreatureType;

/**
 * A component that identifies the entity as a Chicken.
 */
public class Turtle extends Living implements Passive {

	@Override
	public void onAttached() {
		super.onAttached();
		getOwner().getNetwork().setEntityProtocol(VanillaPlugin.VANILLA_PROTOCOL_ID, new CreatureProtocol(CreatureType.CHICKEN));
		DeathDrops dropComponent = getOwner().add(DeathDrops.class);
		dropComponent.addDrop(new ItemStack(VanillaMaterials.FEATHER, getRandom().nextInt(2)));
		dropComponent.addDrop(new ItemStack(VanillaMaterials.RAW_CHICKEN, 1));
		dropComponent.addXpDrop((short) (getRandom().nextInt(3) + 1));

		if (getAttachedCount() == 1) {
			getOwner().add(Health.class).setSpawnHealth(10);
		}
	}
	
	@Override
	public void onInteract(Action action, Entity source) {
		((Player)source).sendMessage("Hit a Turtle");
	}
}
