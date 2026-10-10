package me.karven.lagless.mixin.attack;

import me.karven.lagless.module.attack.AttackModule;
import me.karven.lagless.module.attack.HasPunchState;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements HasPunchState {
    @Unique
    private AttackModule.PunchState punchState$lagless = AttackModule.PunchState.NONE;

    public AttackModule.PunchState getPunchState$lagless() {
        return this.punchState$lagless;
    }

    public void setPunchState$lagless(final AttackModule.PunchState punchState) {
        this.punchState$lagless = punchState;
    }
}
