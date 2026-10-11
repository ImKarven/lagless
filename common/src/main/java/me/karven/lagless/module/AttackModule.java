package me.karven.lagless.module;

import me.karven.lagless.config.RootConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AttackModule extends Module {
    public AttackModule() {
        super("attack");
    }

    @Override
    public boolean isEnabled(final RootConfiguration config) {
        return config.modules.attack.enabled;
    }
}
