package me.karven.lagless.mixin.attack;

import me.karven.lagless.module.attack.AttackModule;
import me.karven.lagless.module.attack.HasPunchState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "handleBlockBreakAction", at = @At(value = "HEAD"))
    private void lagless$attack$startDestroyBlock(
            final BlockPos pos,
            final ServerboundPlayerActionPacket.Action action,
            final Direction direction,
            final int maxY,
            final int sequence,
            final CallbackInfo ci
    ) {
        if (action != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) return;
        ((HasPunchState) this.player).lagless$attack$setPunchState(AttackModule.PunchState.START_DESTROY_BLOCK);
    }

    @Inject(method = "abortDestroyBlock", at = @At(value = "HEAD"))
    private void lagless$attack$abortDestroyBlock(final CallbackInfo ci) {
        ((HasPunchState) this.player).lagless$attack$setPunchState(AttackModule.PunchState.NONE);
    }

    @Inject(method = "destroyAndAck", at = @At(value = "HEAD"))
    private void lagless$attack$destroyBlock(final BlockPos pos, final int sequence, final String exitId, final CallbackInfo ci) {
        switch (exitId) {
            case "creative destroy", "insta mine" -> ((HasPunchState) this.player).lagless$attack$setPunchState(AttackModule.PunchState.INSTANT_BREAK);
            default -> ((HasPunchState) this.player).lagless$attack$setPunchState(AttackModule.PunchState.NONE);
        }
    }
}
