package me.karven.lagless;

import me.karven.lagless.config.LoadConfigResult;
import me.karven.lagless.config.RootConfiguration;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;

import java.util.concurrent.CompletableFuture;

@NullMarked
public abstract class BaseMain {
    private static @Nullable BaseMain INSTANCE;
    private static final Object CONFIG_IO_LOCK = new Object();
    private static volatile @Nullable RootConfiguration CONFIG;
    private final Logger logger;

    protected BaseMain(final Logger logger) {
        this.logger = logger;
        INSTANCE = this;
    }

    public void enable() {
        loadConfig();
        registerCommand();
    }

    public static BaseMain instance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Lagless instance is used before enable");
        }
        return INSTANCE;
    }

    public RootConfiguration config() {
        final RootConfiguration currentConfig = CONFIG;
        if (currentConfig == null) {
            throw new IllegalStateException("Cannot access config before enable");
        }
        return currentConfig;
    }

    public CompletableFuture<LoadConfigResult> reloadConfigAsync() {
        return CompletableFuture.supplyAsync(this::loadConfig);
    }

    private LoadConfigResult loadConfig() {
        synchronized (CONFIG_IO_LOCK) {
            try {
                CONFIG = RootConfiguration.load();
                logger.info("Config loaded successfully");
                return LoadConfigResult.SUCCESS;
            } catch (ConfigurateException e) {
                logger.error("Failed to load config file", e);
                if (CONFIG == null) CONFIG = RootConfiguration.DEFAULT;
                return LoadConfigResult.FAILED;
            }
        }
    }

    abstract void registerCommand();
}
