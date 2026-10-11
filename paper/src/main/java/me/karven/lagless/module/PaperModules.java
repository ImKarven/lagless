package me.karven.lagless.module;

import me.karven.lagless.module.attack.PaperAttackModule;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperModules {
    public static final PaperAttackModule ATTACK = new PaperAttackModule();
    public static final PaperPoseModule POSE = new PaperPoseModule();
    public static final ItemCountModule ITEM_COUNT = new ItemCountModule();
}
