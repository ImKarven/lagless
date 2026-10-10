package me.karven.lagless.module;

import me.karven.lagless.LaglessFabricMod;
import me.karven.lagless.config.RootConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class Module {
    private final String name;

    protected Module(final String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return isEnabled(LaglessFabricMod.config());
    }

    abstract public boolean isEnabled(final RootConfiguration config);

    public String getName() {
        return this.name;
    }
}
