package me.karven.lagless.config.serializer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class EntityTypeSerializer implements TypeSerializer<EntityType<?>> {
    public static final EntityTypeSerializer INSTANCE = new EntityTypeSerializer();

    private EntityTypeSerializer() {}

    @Override
    public EntityType<?> deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final Identifier identifier = node.get(Identifier.class);
        if (identifier == null) {
            throw new SerializationException("Entity type identifier must not be null");
        }
        return BuiltInRegistries.ENTITY_TYPE.getValue(identifier);
    }

    @Override
    public void serialize(final Type type, final @Nullable EntityType<?> object, final ConfigurationNode node) throws SerializationException {
        if (object == null) {
            node.raw(null);
            return;
        }
        final Identifier identifier = BuiltInRegistries.ENTITY_TYPE.getKey(object);
        node.set(identifier);
    }
}
