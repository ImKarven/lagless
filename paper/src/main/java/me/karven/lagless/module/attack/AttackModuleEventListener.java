package me.karven.lagless.module.attack;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.karven.lagless.LaglessPaperMain;
import me.karven.lagless.config.RootConfiguration;
import me.karven.lagless.module.AttackModule;
import me.karven.lagless.module.PaperModules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AttackModuleEventListener implements Listener {

    @EventHandler
    public void onPunch(final PlayerArmSwingEvent event) {

        final RootConfiguration config = LaglessPaperMain.instance().config();
        if (!PaperModules.ATTACK.isEnabled(config)) return;
        final Player player = event.getPlayer();
        final PaperAttackModule.PunchState punchState = PaperModules.ATTACK.getPunchState(player);

        switch (punchState) {
            case ATTACK, INSTANT_BREAK -> {
                PaperModules.ATTACK.setPunchState(player, AttackModule.PunchState.NONE);
                return;
            }

            // TODO: bug: the client can continue breaking block even after abort
            case DESTROYING_BLOCK -> {
                return;
            }

            case START_DESTROY_BLOCK -> PaperModules.ATTACK.setPunchState(player, AttackModule.PunchState.DESTROYING_BLOCK);
        }
        final AttributeInstance interactionRangeAttribute = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
        if (interactionRangeAttribute == null) return;
        final double range = interactionRangeAttribute.getValue();
        final Location eyeLocation = player.getEyeLocation();
        final RayTraceResult rayTraceResult = player.getWorld().rayTrace(
                eyeLocation,
                eyeLocation.getDirection(),
                range,
                FluidCollisionMode.NEVER,
                false,
                0,
                entity -> {
                    final CraftEntity craftEntity = (CraftEntity) entity;
                    return !entity.getUniqueId().equals(player.getUniqueId()) &&
                            (!(entity instanceof HumanEntity human) || human.getGameMode() != GameMode.SPECTATOR) &&
                            craftEntity.getHandle().isPickable() &&
                            !craftEntity.getHandle().isPassengerOfSameVehicle(((CraftPlayer) player).getHandle()) &&
                            player.canSee(entity);
                }
        );
        if (rayTraceResult == null) return;
        final Entity entity = rayTraceResult.getHitEntity();
        if (entity == null) return;
        final NamespacedKey key = entity.getType().getKey();
        final net.minecraft.world.entity.EntityType<?> nmsEntityType =
                BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath(key.namespace(), key.value()));

        final boolean contain = config.modules.attack.entityTypes.entityTypes.contains(nmsEntityType);
        switch (config.modules.attack.entityTypes.mode) {
            case BLACKLIST -> {
                if (contain) return;
            }

            case WHITELIST -> {
                if (!contain) return;
            }
        }

        PaperModules.ATTACK.setLaglessAttacking(player);
        player.attack(entity);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onStartDestroyingBlock(final PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        final Player player = event.getPlayer();
        switch (player.getGameMode()) {
            case CREATIVE -> PaperModules.ATTACK.setPunchState(player, AttackModule.PunchState.INSTANT_BREAK);

            case SURVIVAL -> PaperModules.ATTACK.setPunchState(player, AttackModule.PunchState.START_DESTROY_BLOCK);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onStartDestroyingBlock(final BlockDamageEvent event) {
        PaperModules.ATTACK.setPunchState(event.getPlayer(), event.getInstaBreak() ? AttackModule.PunchState.INSTANT_BREAK : AttackModule.PunchState.START_DESTROY_BLOCK);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttack(final PrePlayerAttackEntityEvent event) {
        final Player player = event.getPlayer();
        if (PaperModules.ATTACK.isLaglessAttacking(player)) {
            PaperModules.ATTACK.unsetLaglessAttacking(player);
            return;
        }
        PaperModules.ATTACK.setPunchState(player, AttackModule.PunchState.ATTACK);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAbortDestroyingBlock(final BlockDamageAbortEvent event) {
        PaperModules.ATTACK.setPunchState(event.getPlayer(), AttackModule.PunchState.NONE);
    }


    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        PaperModules.ATTACK.resetNonPersistentData(event.getPlayer());
    }
}
