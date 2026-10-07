package me.karven.lagless.config.module;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@NullMarked
@ConfigSerializable
public class AttackModuleConfiguration {

    public boolean enabled = false;
    public EntityTypesConfiguration entityTypes = new EntityTypesConfiguration();


    @NullMarked
    @ConfigSerializable
    public static class EntityTypesConfiguration {

        public Mode mode = Mode.BLACKLIST;
        public List<EntityType<?>> entityTypes = List.of(EntityTypes.PLAYER);

        public enum Mode {
            WHITELIST,
            BLACKLIST
        }
    }
}
