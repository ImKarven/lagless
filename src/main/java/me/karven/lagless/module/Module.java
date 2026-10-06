package me.karven.lagless.module;

import org.jspecify.annotations.NullMarked;

import java.util.concurrent.atomic.AtomicBoolean;

@NullMarked
public abstract class Module {
    private final String name;
    private final AtomicBoolean ENABLED;

    protected Module(final boolean enabled, final String name) {
        this.ENABLED = new AtomicBoolean(enabled);
        this.name = name;
    }

    public void enable() {
        this.ENABLED.set(true);
    }

    public void disable() {
        this.ENABLED.set(false);
    }

    public boolean isEnabled() {
        return this.ENABLED.get();
    }

    public String getName() {
        return this.name;
    }
}
