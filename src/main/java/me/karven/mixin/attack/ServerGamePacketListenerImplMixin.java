package me.karven.mixin.attack;

import me.karven.Lagless;
import me.karven.module.AttackModule;
import me.karven.module.Modules;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
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

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    private final AttackModule ATTACK_MODULE = Modules.ATTACK;
    private final EnumSet<AttackModule.PunchState> ONE_PUNCH_PACKET_STATES = EnumSet.of(
            AttackModule.PunchState.ATTACK,
            AttackModule.PunchState.INSTANT_BREAK
    );

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
        final AttackModule.PunchState punchState = ATTACK_MODULE.getPunchState(this.player);
        if (ONE_PUNCH_PACKET_STATES.contains(punchState)) {
            ATTACK_MODULE.setPunchState(this.player, AttackModule.PunchState.NONE);
            return;
        }

        // TODO: proper breaking block check
        if (punchState == AttackModule.PunchState.DESTROYING_BLOCK) {
            return;
        }

        final Entity entity = ATTACK_MODULE.rayTraceEntity(this.player);
        if (entity == null) return;
        this.handleAttack(new ServerboundAttackPacket(entity.getId()));
    }

    @Inject(method = "handleAttack", at = @At(value = "HEAD"))
    private void lagless$attack$attackPacket(final ServerboundAttackPacket packet, final CallbackInfo ci) {
        ATTACK_MODULE.setPunchState(this.player, AttackModule.PunchState.ATTACK);
    }

    @Inject(method = "handlePlayerAction", at = @At(value = "RETURN"))
    private void lagless$attack$playerActionPacket(final ServerboundPlayerActionPacket packet, final CallbackInfo ci) {
        this.player.sendSystemMessage(Component.literal(packet.getAction().name()));
    }
}
