package me.karven.lagless.mixin.attack;

import me.karven.lagless.LaglessFabricMod;
import me.karven.lagless.config.RootConfiguration;
import me.karven.lagless.module.FabricModules;
import me.karven.lagless.module.attack.FabricAttackModule;
import me.karven.lagless.module.attack.HasPunchState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
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
    public abstract boolean hasClientLoaded();

    // Inject at the start (after ensuring running on same thread) to keep vanilla behavior: attack packet is sent first, punch packet after.
    @Inject(method = "handlePunch", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V"))
    private void punchPacket(final CallbackInfo ci) {
        final RootConfiguration config = LaglessFabricMod.instance().config();
        if (!FabricModules.ATTACK.isEnabled(config)) return;
        final FabricAttackModule.PunchState punchState = ((HasPunchState) this.player).getPunchState$lagless();

        switch (punchState) {
            case ATTACK, INSTANT_BREAK -> {
                // We shouldn't attack if the player broke a block with one punch
                ((HasPunchState) this.player).setPunchState$lagless(FabricAttackModule.PunchState.NONE);
                return;
            }

            // TODO: bug: the client can continue breaking block even after abort
            case DESTROYING_BLOCK -> {
                return;
            }

            case START_DESTROY_BLOCK -> ((HasPunchState) this.player).setPunchState$lagless(FabricAttackModule.PunchState.DESTROYING_BLOCK);
        }

        if (this.player.isSpectator() || !this.hasClientLoaded()) {
            return;
        }

        final ItemStack mainHandItem = this.player.getMainHandItem();
        if (this.player.cannotAttackWithItem(mainHandItem, 5)) {
            return;
        }

        if (mainHandItem.has(DataComponents.PIERCING_WEAPON)) {
            return;
        }

        final ServerLevel level = this.player.level();
        if (!mainHandItem.isItemEnabled(level.enabledFeatures())) {
            return;
        }

        final Entity entity = FabricModules.ATTACK.rayTraceEntity(this.player);
        if (entity == null) return;

        if (!level.getWorldBorder().isWithinBounds(entity.blockPosition())) {
            return;
        }

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
        this.player.attack(entity);
    }

    @Inject(method = "handleAttack", at = @At(value = "HEAD"))
    private void attackPacket(final ServerboundAttackPacket packet, final CallbackInfo ci) {
        ((HasPunchState) this.player).setPunchState$lagless(FabricAttackModule.PunchState.ATTACK);
    }
}
