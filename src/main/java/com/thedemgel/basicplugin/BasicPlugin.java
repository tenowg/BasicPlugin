package com.thedemgel.basicplugin;

import com.thedemgel.basicplugin.commands.PlayerCommands;
import java.util.logging.Level;
import org.spout.api.Engine;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.RootCommand;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleAnnotatedCommandExecutorFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.event.Order;
import org.spout.api.plugin.CommonPlugin;
import org.spout.api.plugin.PluginLogger;

/**
 *
 * @author Craig <tenowg at thedemgel.com>
 */
public class BasicPlugin extends CommonPlugin {
    private Engine engine;
    private static BasicPlugin instance;
    
    @Override
    public void onLoad() {
        this.instance = this;
        ((PluginLogger) getLogger()).setTag(new ChatArguments(ChatStyle.RESET, "[", ChatStyle.GOLD, "BasicPlugin", ChatStyle.RESET, "] "));
        engine = getEngine();
        getLogger().info("loaded");
    }
    
    @Override
    public void onEnable() {
        //Commands
        final CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this), new SimpleAnnotatedCommandExecutorFactory());
        final RootCommand root = engine.getRootCommand();
        root.addSubCommands(this, PlayerCommands.class, commandRegFactory);
        //root.addSubCommands(this, AdminCommands.class, commandRegFactory);

        engine.getEventManager().registerEvents(new PlayerListener(this), this);

        getLogger().log(Level.INFO, "v{0} enabled.", getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "v{0} disabled.", getDescription().getVersion());
    }
    
}
