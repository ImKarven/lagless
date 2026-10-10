package me.karven.lagless.config.module;

import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@NullMarked
@ConfigSerializable
public class ModulesConfiguration {

    public boolean itemCount = true;
    public boolean pose = true;
    public AttackModuleConfiguration attack = new AttackModuleConfiguration();
}
