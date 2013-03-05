package com.thedemgel.basicplugin;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.entity.Player;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.engine.EngineStartEvent;
import org.spout.api.event.player.PlayerJoinEvent;

/**
 *
 * @author Craig <tenowg at thedemgel.com>
 */
public class PlayerListener implements Listener {

    private final BasicPlugin plugin;
    
    public PlayerListener(BasicPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(order = Order.EARLY)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
	//HUDComponent HUD = player.add(HUDComponent.class);
	//HUD.setDefault(BasicArmorWidget.class);
	//HUD.setDefault(BasicExpWidget.class);
	
	player.sendMessage(plugin.getLang().getString("name"));
    }

    @EventHandler(order = Order.EARLY)
    public void onGameStart(EngineStartEvent event) {
        if (plugin.getEngine().getPlatform() != Platform.CLIENT) {
            return;
        }
        Player player = ((Client) plugin.getEngine()).getActivePlayer();
        //HUDComponent HUD = player.add(HUDComponent.class);
	//HUD.setDefault(BasicArmorWidget.class);
	//HUD.setDefault(BasicExpWidget.class);
    }
}
