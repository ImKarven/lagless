package me.karven.lagless.module.attack;

import me.karven.lagless.module.AttackModule;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperAttackModule extends AttackModule {
    private static final PunchStatePersistentDataType PUNCH_STATE_TYPE = new PunchStatePersistentDataType();
    private static final NamespacedKey PUNCH_STATE_KEY = new NamespacedKey("lagless", "attack_punch_state");

    public PunchState getPunchState(final Player player) {
        return player.getPersistentDataContainer().getOrDefault(PUNCH_STATE_KEY, PUNCH_STATE_TYPE, PunchState.NONE);
    }

    public void setPunchState(final Player player, final PunchState punchState) {
        player.getPersistentDataContainer().set(PUNCH_STATE_KEY, PUNCH_STATE_TYPE, punchState);
    }

    public void resetPunchState(final Player player) {
        player.getPersistentDataContainer().remove(PUNCH_STATE_KEY);
    }
}
