package me.karven.lagless;

import org.bukkit.plugin.java.JavaPlugin;

public final class LaglessPaperPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new LaglessPaperMain(this).enable();
    }
}
