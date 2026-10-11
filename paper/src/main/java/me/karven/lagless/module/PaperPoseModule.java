package me.karven.lagless.module;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Pose;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class PaperPoseModule extends PoseModule {
    private static final int POSE_ID = 6;

    public Object handleAndModifyPacket(final Player player, final Object packet) {
        if (!isEnabled()) return packet;
        if (
                !(packet instanceof ClientboundSetEntityDataPacket(
                        int id, List<SynchedEntityData.DataValue<?>> packedItems
                )) || id != player.getEntityId()
        ) return packet;

        final List<SynchedEntityData.DataValue<?>> data = new ArrayList<>(packedItems);

        data.removeIf(dataValue -> {
            if (dataValue.id() != POSE_ID) return false;
            if (!(dataValue.value() instanceof Pose pose)) return false;
            return pose == Pose.CROUCHING || pose == Pose.STANDING;
        });

        return new ClientboundSetEntityDataPacket(id, data);
    }
}
