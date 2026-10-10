package me.karven.lagless;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.Identifier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NullMarked
public class LaglessFabricMod extends BaseMain implements ModInitializer {
	public static @Nullable LaglessFabricMod INSTANCE;
	public static final String MOD_ID = "lagless";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public LaglessFabricMod() {
		super(LOGGER);
		INSTANCE = this;
	}

	public static LaglessFabricMod instance() {
		if (INSTANCE == null) {
			throw new IllegalStateException("Lagless is not initialized yet");
		}
		return INSTANCE;
	}

	@Override
	public void onInitialize() {
		this.enable();
    }

	@Override
	void registerCommand() {
		CommandRegistrationCallback.EVENT.register((
				(dispatcher, _, _) ->
						dispatcher.register(LaglessFabricCommand.getRootCommand())
		));
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
