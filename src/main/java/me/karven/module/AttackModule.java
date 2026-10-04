package me.karven.module;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class AttackModule extends Module {
    private final ConcurrentHashMap<UUID, PunchState> playersPunchState = new ConcurrentHashMap<>();

    protected AttackModule() {
        super(false);
    }

    public void setPunchState(final Player player, final PunchState state) {
        playersPunchState.put(player.getUUID(), state);
    }

    public PunchState getPunchState(final Player player) {
        return playersPunchState.get(player.getUUID());
    }

    public @Nullable Entity rayTraceEntity(final ServerPlayer player) {
        if (player.gameMode() == GameType.SPECTATOR) return null;
        final Vec3 startPosition = player.getEyePosition();
        final Vec3 direction = player.getLookAngle();
        final double rayLength = player.entityInteractionRange();
        final Vec3 delta = direction.scale(rayLength);
        final Vec3 endPosition = startPosition.add(delta);


        // Ray trace to find a matching block
        final BlockHitResult blockHitResult = player.level().clip(
                new ClipContext(
                        startPosition,
                        endPosition,
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        player
                )
        );

        // When ray tracing entities, the distance must be less than the block's distance because blocks obstruct entities
        final double maxDistance = blockHitResult.getType() == HitResult.Type.MISS ? rayLength : blockHitResult.getLocation().distanceTo(startPosition);
        final Vec3 entityRayTraceDelta = direction.scale(maxDistance);
        final Vec3 entityRayTraceEndPosition = startPosition.add(entityRayTraceDelta);
        final AABB searchBox = player.getBoundingBox().expandTowards(entityRayTraceDelta).inflate(1.0D);

        final EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                player,
                startPosition,
                entityRayTraceEndPosition,
                searchBox,
                entity -> entity.isPickable() && !entity.isSpectator(),
                maxDistance * maxDistance
        );
        player.sendOverlayMessage(Component.literal(
                "Block Distance: " + maxDistance +
                        " Entity Distance: " + (entityHitResult == null ? "None" : entityHitResult.getLocation().distanceTo(startPosition))
        ));

        return entityHitResult == null ? null : entityHitResult.getEntity();
    }

    @NullMarked
    public enum PunchState {
        NONE,
        /**
         * Player sends a `ServerboundAttackPacket` then a `ServerboundPunchPacket`
         */
        ATTACK,
        /**
         * Player sends a {@code ServerboundPlayerActionPacket} to start breaking a block, then a {@code ServerboundPunchPacket} once. The player is in creative mode or the block is insta-breakable for them.
         */
        INSTANT_BREAK,
        /**
         * Player sends a {@code ServerboundPlayerActionPacket} to start breaking a block and sends {@code ServerboundPunchPacket} every tick until the block is broken or the player aborts destroying the block.
         */
        DESTROYING_BLOCK
    }
}
