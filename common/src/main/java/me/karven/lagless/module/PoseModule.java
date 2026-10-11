package me.karven.lagless.module;

import me.karven.lagless.config.RootConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PoseModule extends Module {
    public PoseModule() {
        super("pose");
    }

    @Override
    public boolean isEnabled(final RootConfiguration config) {
        return config.modules.pose;
    }
}
