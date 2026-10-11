package me.karven.lagless.module;

import me.karven.lagless.module.attack.FabricAttackModule;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FabricModules {
    public static final FabricAttackModule ATTACK = new FabricAttackModule();
    public static final PoseModule POSE = new PoseModule();
    public static final ItemCountModule ITEM_COUNT = new ItemCountModule();
}
