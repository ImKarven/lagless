package me.karven.mixin.attack;

import me.karven.module.Modules;
import me.karven.module.attack.AttackModule;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    public abstract void handleAttack(ServerboundAttackPacket packet);

    // Inject at the start (after ensuring running on same thread) to keep vanilla behavior: attack packet is sent first, punch packet after.
    @Inject(method = "handlePunch", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V"))
    private void lagless$attack$punchPacket(final CallbackInfo ci) {
        if (!Modules.ATTACK.isEnabled()) return;
        final AttackModule.PunchState punchState = this.player.lagless$attack$getPunchState();

        // We shouldn't attack if the player broke a block with one punch
        if (punchState == AttackModule.PunchState.ATTACK || punchState == AttackModule.PunchState.INSTANT_BREAK) {
            this.player.lagless$attack$setPunchState(AttackModule.PunchState.NONE);
            return;
        }

        // TODO: bug: the client can continue breaking block even after abort
        if (punchState == AttackModule.PunchState.DESTROYING_BLOCK) {
            return;
        }

        final Entity entity = Modules.ATTACK.rayTraceEntity(this.player);
        if (entity == null) return;

        // Attack the entity
        this.handleAttack(new ServerboundAttackPacket(entity.getId()));
    }

    @Inject(method = "handleAttack", at = @At(value = "HEAD"))
    private void lagless$attack$attackPacket(final ServerboundAttackPacket packet, final CallbackInfo ci) {
        this.player.lagless$attack$setPunchState(AttackModule.PunchState.ATTACK);
    }
}
