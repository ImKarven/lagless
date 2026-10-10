package me.karven.lagless.mixin.attack;

import me.karven.lagless.LaglessFabricMod;
import me.karven.lagless.config.RootConfiguration;
import me.karven.lagless.module.Modules;
import me.karven.lagless.module.attack.AttackModule;
import me.karven.lagless.module.attack.HasPunchState;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
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
    public abstract void handleAttack(ServerboundAttackPacket packet);

    // Inject at the start (after ensuring running on same thread) to keep vanilla behavior: attack packet is sent first, punch packet after.
    @Inject(method = "handlePunch", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V"))
    private void punchPacket(final CallbackInfo ci) {
        final RootConfiguration config = LaglessFabricMod.config();
        if (!Modules.ATTACK.isEnabled(config)) return;
        final AttackModule.PunchState punchState = ((HasPunchState) this.player).getPunchState$lagless();

        switch (punchState) {
            case ATTACK, INSTANT_BREAK -> {
                // We shouldn't attack if the player broke a block with one punch
                ((HasPunchState) this.player).setPunchState$lagless(AttackModule.PunchState.NONE);
                return;
            }

            // TODO: bug: the client can continue breaking block even after abort
            case DESTROYING_BLOCK -> {
                return;
            }

            case START_DESTROY_BLOCK -> ((HasPunchState) this.player).setPunchState$lagless(AttackModule.PunchState.DESTROYING_BLOCK);
        }

        final Entity entity = Modules.ATTACK.rayTraceEntity(this.player);
        if (entity == null) return;

        final boolean contain = config.modules.attack.entityTypes.entityTypes.contains(entity.getType());
        switch (config.modules.attack.entityTypes.mode) {
            case BLACKLIST -> {
                if (contain) return;
            }

            case WHITELIST -> {
                if (!contain) return;
            }
        }

        // Attack the entity
        this.handleAttack(new ServerboundAttackPacket(entity.getId()));
    }

    @Inject(method = "handleAttack", at = @At(value = "HEAD"))
    private void attackPacket(final ServerboundAttackPacket packet, final CallbackInfo ci) {
        ((HasPunchState) this.player).setPunchState$lagless(AttackModule.PunchState.ATTACK);
    }
}
