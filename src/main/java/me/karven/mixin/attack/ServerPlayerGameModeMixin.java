package me.karven.mixin.attack;

import me.karven.module.AttackModule;
import me.karven.module.Modules;
import net.minecraft.core.BlockPos;
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

    private final AttackModule ATTACK_MODULE = Modules.ATTACK;

    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "abortDestroyBlock", at = @At(value = "HEAD"))
    private void lagless$attack$abortDestroyBlock(final CallbackInfo ci) {
        ATTACK_MODULE.setPunchState(this.player, AttackModule.PunchState.NONE);
    }

    @Inject(method = "destroyAndAck", at = @At(value = "HEAD"))
    private void lagless$attack$destroyBlock(final BlockPos pos, final int sequence, final String exitId, final CallbackInfo ci) {
        switch (exitId) {
            case "creative destroy", "insta mine" -> ATTACK_MODULE.setPunchState(player, AttackModule.PunchState.INSTANT_BREAK);
            default -> ATTACK_MODULE.setPunchState(player, AttackModule.PunchState.NONE);
        }
    }
}
