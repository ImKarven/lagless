package me.karven.lagless.module;

import me.karven.lagless.config.RootConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ItemCountModule extends Module {

    public ItemCountModule() {
        super("item_count");
    }

    @Override
    public boolean isEnabled(final RootConfiguration config) {
        return config.modules.itemCount;
    }
}
