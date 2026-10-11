package me.karven.lagless.module;

import me.karven.lagless.config.RootConfiguration;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AttackModule extends Module {
    public AttackModule() {
        super("attack");
    }

    @Override
    public boolean isEnabled(final RootConfiguration config) {
        return config.modules.attack.enabled;
    }

    public enum PunchState {
        NONE,
        /**
         * Player sends a `ServerboundAttackPacket` then a `ServerboundPunchPacket`
         */
        ATTACK,
        /**
         * Player sends a {@code ServerboundPlayerActionPacket} to start breaking a block, then a {@code ServerboundPunchPacket} once. The player is in creative mode or the block is insta-breakable for them.
         */
        INSTANT_BREAK,
        /**
         * Player sends a {@code ServerboundPlayerActionPacket} to start breaking a block and sends {@code ServerboundPunchPacket} every tick until the block is broken or the player aborts destroying the block.
         */
        START_DESTROY_BLOCK,
        DESTROYING_BLOCK
    }
}
