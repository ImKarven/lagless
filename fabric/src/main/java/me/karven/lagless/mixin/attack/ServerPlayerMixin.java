package me.karven.lagless.mixin.attack;

import me.karven.lagless.module.attack.FabricAttackModule;
import me.karven.lagless.module.attack.HasPunchState;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements HasPunchState {
    @Unique
    private FabricAttackModule.PunchState punchState$lagless = FabricAttackModule.PunchState.NONE;

    public FabricAttackModule.PunchState getPunchState$lagless() {
        return this.punchState$lagless;
    }

    public void setPunchState$lagless(final FabricAttackModule.PunchState punchState) {
        this.punchState$lagless = punchState;
    }
}
