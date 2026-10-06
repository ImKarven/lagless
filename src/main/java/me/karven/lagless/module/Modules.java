package me.karven.lagless.module;

import me.karven.lagless.module.attack.AttackModule;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class Modules {
    public static final AttackModule ATTACK = new AttackModule();
    public static final PoseModule POSE = new PoseModule();
    public static final ItemCountModule ITEM_COUNT = new ItemCountModule();

    private static final List<Module> MODULES = List.of(ATTACK, POSE, ITEM_COUNT);

    public static @Nullable Module fromName(final String name) {
        for (final Module module : MODULES) {
            if (module.getName().equals(name)) return module;
        }
        return null;
    }
}
