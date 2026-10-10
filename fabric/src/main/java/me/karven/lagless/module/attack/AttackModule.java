package me.karven.lagless.module.attack;

import me.karven.lagless.config.RootConfiguration;
import me.karven.lagless.module.Module;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class AttackModule extends Module {
    public AttackModule() {
        super("attack");
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

        return entityHitResult == null ? null : entityHitResult.getEntity();
    }

    @Override
    public boolean isEnabled(final RootConfiguration config) {
        return config.modules.attack.enabled;
    }

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
        START_DESTROY_BLOCK,
        DESTROYING_BLOCK
    }
}
