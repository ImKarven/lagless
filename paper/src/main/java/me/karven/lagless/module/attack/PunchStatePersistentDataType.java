package me.karven.lagless.module.attack;

import me.karven.lagless.module.AttackModule;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PunchStatePersistentDataType implements PersistentDataType<Integer, AttackModule.PunchState> {

    @Override
    public Class<Integer> getPrimitiveType() {
        return Integer.class;
    }

    @Override
    public Class<AttackModule.PunchState> getComplexType() {
        return AttackModule.PunchState.class;
    }

    @Override
    public Integer toPrimitive(final AttackModule.PunchState punchState, final PersistentDataAdapterContext context) {
        return punchState.ordinal();
    }

    @Override
    public AttackModule.PunchState fromPrimitive(final Integer ordinal, final PersistentDataAdapterContext context) {
        return AttackModule.PunchState.values()[ordinal];
    }
}
