package me.karven.lagless.module.attack;

import me.karven.lagless.module.AttackModule;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PaperAttackModule extends AttackModule {
    private static final PunchStatePersistentDataType PUNCH_STATE_TYPE = new PunchStatePersistentDataType();
    private static final NamespacedKey PUNCH_STATE_KEY = new NamespacedKey("lagless", "attack_punch_state");
    private static final NamespacedKey LAGLESS_ATTACK_KEY = new NamespacedKey("lagless", "attack_actually_attacking");

    public PunchState getPunchState(final Player player) {
        return player.getPersistentDataContainer().getOrDefault(PUNCH_STATE_KEY, PUNCH_STATE_TYPE, PunchState.NONE);
    }

    public void setPunchState(final Player player, final PunchState punchState) {
        player.getPersistentDataContainer().set(PUNCH_STATE_KEY, PUNCH_STATE_TYPE, punchState);
    }

    public void resetNonPersistentData(final Player player) {
        player.getPersistentDataContainer().remove(PUNCH_STATE_KEY);
        player.getPersistentDataContainer().remove(LAGLESS_ATTACK_KEY);
    }
    
    public void setLaglessAttacking(final Player player) {
        player.getPersistentDataContainer().set(LAGLESS_ATTACK_KEY, PersistentDataType.BYTE, (byte) 0);
    }
    
    public void unsetLaglessAttacking(final Player player) {
        player.getPersistentDataContainer().remove(LAGLESS_ATTACK_KEY);
    }
    
    public boolean isLaglessAttacking(final Player player) {
        return player.getPersistentDataContainer().has(LAGLESS_ATTACK_KEY);
    }
}
