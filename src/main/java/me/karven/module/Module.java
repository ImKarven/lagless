package me.karven.module;

import org.jspecify.annotations.NullMarked;

import java.util.concurrent.atomic.AtomicBoolean;

@NullMarked
public abstract class Module {
    private final AtomicBoolean ENABLED;

    protected Module(final boolean enabled) {
        this.ENABLED = new AtomicBoolean(enabled);
    }

    public void enable() {
        this.ENABLED.set(true);
    };
    public void disable() {
        this.ENABLED.set(false);
    }

    public boolean isEnabled() {
        return this.ENABLED.get();
    }
}
