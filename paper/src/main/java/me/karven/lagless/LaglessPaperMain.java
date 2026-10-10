package me.karven.lagless;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LaglessPaperMain extends BaseMain {
    private static @Nullable LaglessPaperMain INSTANCE;

    private final LaglessPaperPlugin plugin;

    public LaglessPaperMain(final LaglessPaperPlugin plugin) {
        super(plugin.getComponentLogger());
        this.plugin = plugin;
        INSTANCE = this;
    }

    public static LaglessPaperMain instance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Lagless is not enabled yet");
        }
        return INSTANCE;
    }

    public LaglessPaperPlugin plugin() {
        return this.plugin;
    }

    @Override
    void registerCommand() {
        plugin.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> event.registrar().register(LaglessPaperCommand.getRootCommand().build())
        );
    }
}
