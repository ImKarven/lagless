package me.karven.mixin.attack;

import me.karven.module.attack.AttackModule;
import me.karven.module.attack.HasPunchState;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements HasPunchState {
    @Unique
    private AttackModule.PunchState lagless$attack$punchState = AttackModule.PunchState.NONE;

    public AttackModule.PunchState lagless$attack$getPunchState() {
        return this.lagless$attack$punchState;
    }

    public void lagless$attack$setPunchState(final AttackModule.PunchState punchState) {
        this.lagless$attack$punchState = punchState;
    }
}
