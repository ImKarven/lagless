package me.karven.lagless.module;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperModules {
    public static final AttackModule ATTACK = new AttackModule();
    public static final PaperPoseModule POSE = new PaperPoseModule();
    public static final ItemCountModule ITEM_COUNT = new ItemCountModule();
}
