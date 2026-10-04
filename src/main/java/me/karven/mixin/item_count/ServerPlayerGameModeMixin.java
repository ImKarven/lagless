package me.karven.mixin.item_count;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.karven.mixin.accessor.AbstractContainerMenuAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void lagless$item_count$setSnapshot(
            final ServerPlayer player,
            final Level level,
            final ItemStack itemStack,
            final InteractionHand hand,
            final BlockHitResult hitResult,
            final CallbackInfoReturnable<InteractionResult> cir,
            final @Share("snapshot") LocalRef<ItemStack> snapshot // Get a snapshot of the ItemStack to compare
    ) {
        snapshot.set(itemStack.copy());
    }

    @Inject(method = "useItemOn", at = @At("RETURN"))
    private void lagless$item_count$syncRemote(
            final ServerPlayer player,
            final Level level,
            final ItemStack itemStack,
            final InteractionHand hand,
            final BlockHitResult hitResult,
            final CallbackInfoReturnable<InteractionResult> cir,
            final @Share("snapshot") LocalRef<ItemStack> snapshot
    ) {
        final ItemStack untouchedItemStack = snapshot.get();
        if (untouchedItemStack == null || !(untouchedItemStack.getItem() instanceof BlockItem)) return;
        if (!cir.getReturnValue().consumesAction()) return;
        if (player.hasInfiniteMaterials()) return;

        final ItemStack processedItemStack = player.getItemInHand(hand);
        // We expect the process ItemStack to have an off-by-one count to the original ItemStack
        // If this is not the case, the client and server are not in sync, so the server should sync the item normally
        final boolean exactlyOneConsumed =
                processedItemStack.getCount() == untouchedItemStack.getCount() - 1
                && (processedItemStack.isEmpty() || ItemStack.isSameItemSameComponents(processedItemStack, untouchedItemStack));
        if (!exactlyOneConsumed) return;

        final Inventory inventory = player.getInventory();
        final int handSlot = hand == InteractionHand.MAIN_HAND ? inventory.getSelectedSlot() : Inventory.SLOT_OFFHAND;
        final AbstractContainerMenu currentMenu = player.containerMenu;

        for (final Slot slot : currentMenu.slots) {
            if (slot.container != inventory || slot.getContainerSlot() != handSlot) continue;
            final RemoteSlot remoteSlot = ((AbstractContainerMenuAccessor) currentMenu).lagless$getRemoteSlots().get(slot.index);
            if (!(remoteSlot.matches(untouchedItemStack))) continue;
            currentMenu.setRemoteSlot(slot.index, processedItemStack); // Sync remote so that packet won't be sent on container update
        }
    }
}