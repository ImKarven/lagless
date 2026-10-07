package me.karven.lagless;

import me.karven.lagless.config.RootConfiguration;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;

import java.util.concurrent.CompletableFuture;

public class Lagless implements ModInitializer {
	public static final String MOD_ID = "lagless";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static volatile RootConfiguration CONFIG;
	@Override
	public void onInitialize() {
		reloadConfigAsync();

		CommandRegistrationCallback.EVENT.register((
				(dispatcher, _, _) ->
						dispatcher.register(LaglessCommand.getRootCommand())
		));
    }

	public static CompletableFuture<LoadConfigResult> reloadConfigAsync() {
		return CompletableFuture.supplyAsync(Lagless::loadConfig);
	}

	private static LoadConfigResult loadConfig() {
		try {
			CONFIG = RootConfiguration.load();
			LOGGER.info("Config loaded successfully");
			return LoadConfigResult.SUCCESS;
		} catch (ConfigurateException e) {
			LOGGER.error("Failed to load config file", e);
			if (CONFIG == null) CONFIG = RootConfiguration.DEFAULT;
			return LoadConfigResult.FAILED;
		}
	}

	public static RootConfiguration config() {
		return CONFIG;
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public enum LoadConfigResult {
		SUCCESS, FAILED
	}
}
