package me.karven.lagless;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.karven.lagless.injector.InjectorEventListener;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LaglessPaperMain extends BaseMain {

    private final LaglessPaperPlugin plugin;

    public LaglessPaperMain(final LaglessPaperPlugin plugin) {
        super(plugin.getComponentLogger());
        this.plugin = plugin;

        registerListeners();
    }

    public LaglessPaperPlugin plugin() {
        return this.plugin;
    }

    @Override
    void registerCommand() {
        this.plugin.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> event.registrar().register(LaglessPaperCommand.getRootCommand().build())
        );
    }

    void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new InjectorEventListener(), this.plugin);
    }
}
