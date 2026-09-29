package me.karven.mixin.sneak_sync;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.LinkedList;
import java.util.List;

@Mixin(ChunkMap.TrackedEntity.class)
public class ChunkMap$TrackedEntityMixin {

    @Shadow
    @Final
    private Entity entity;

    @ModifyArgs(
            method = "sendToTrackingPlayersAndSelf",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V")
    )
    private void sneak_sync$checkBeforeSendToSelf(final Args arguments) {
        final int POSE_INDEX = 6;

        if (!(arguments.get(0) instanceof ClientboundSetEntityDataPacket(int id, List<SynchedEntityData.DataValue<?>> packedItems))) return;
        if (!(this.entity instanceof ServerPlayer player)) return;
        if (id != player.getId()) return;
        final List<SynchedEntityData.DataValue<?>> copy = new LinkedList<>(packedItems);
        copy.removeIf(dataValue ->
                dataValue.id() == POSE_INDEX &&
                        dataValue.value() instanceof Pose pose &&
                        pose == Pose.CROUCHING
                );
        arguments.set(0, new ClientboundSetEntityDataPacket(id, copy));
    }
}
