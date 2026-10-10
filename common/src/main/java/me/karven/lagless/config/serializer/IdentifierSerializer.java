package me.karven.lagless.config.serializer;

import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

@NullMarked
public class IdentifierSerializer implements TypeSerializer<Identifier> {
    public static final IdentifierSerializer INSTANCE = new IdentifierSerializer();

    private IdentifierSerializer() {}

    @Override
    public Identifier deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        final String stringIdentifier = node.getString();
        if (stringIdentifier == null) {
            throw new SerializationException(node, String.class, "Identifier must not be null");
        }

        try {
            return Identifier.parse(stringIdentifier);
        } catch (final IdentifierException exception) {
            throw new SerializationException(exception);
        }
    }

    @Override
    public void serialize(final Type type, final @Nullable Identifier object, final ConfigurationNode node) throws SerializationException {
        if (object == null) {
            node.raw(null);
            return;
        }

        node.set(object.toString());
    }
}
