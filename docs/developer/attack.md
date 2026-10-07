> [!NOTE]
> The documentation is written for 26.3. Older or newer versions may behave differently, especially for the introduction of `ServerboundPunchPacket` in 26.3 with many changes to how hand swings work.

# Attack

This file explains the logic around the "Attack" module of Lagless.

## Problem

Take Crystal PvP gamemode for example. In this gamemode, players constantly place and destroy end crystals.

Assume the latency of the player is `x` ms, we have the following sequence:

- Player holds end crystals in their main hand and sends `ServerboundUseItemOnPacket` to attempt placing an end crystal
- The packet travels in `x` ms
- The server receives the packet then spawns an end crystal on its end, and send `ClientboundAddEntityPacket` to let the client know the existence of the end crystal
- The packet travels in `x` ms
- The client receives the packet then destroy the end crystal, sending a `ServerboundAttackPacket`
- The packet travels in `x` ms
- The server receives the packet then destroy the end crystal on its end, and send `ClientboundRemoveEntitiesPacket`
- The packet travels in `x` ms
- The sequence repeats, player can place another end crystal

Looking at this sequence, we can see that in Singleplayer environment, where x = 0, this process generally takes 0 ms to complete (ignoring ticks), and in Multiplayer environment, assuming x = 100, this sequence takes **400 ms** to complete.

In such a competitive gamemode like Crystal PvP, this is a delay that can affect the gameplay significantly for players with high latency.

## High-level explanation

We can see that the player can only send the attack packet after the server sends the add entity packet because that's when the client actually knows the existence of the end crystal.

Moreover, we can detect the player's left-clicks, every time the player presses the attack key, they send a `ServerboundPunchPacket`.

From those information, we can **manually attack the crystal** using the player's left-clicks instead of attack packet.

When the player sends a punch packet, we ray trace in the direction the player is looking to find a matching entity. If we found an end crystal, that means the player would have attacked that crystal, and we do that for them.

This basically means that before the player even knows the existence of the end crystal, they can hit it, right after placing the crystal down.

This turns the sequence that previously took 400 ms to complete to only 200 ms, this 200 ms delay also only appears at the start of the sequence. If the player continues place-then-hit'ing end crystals, they will essentially end up doing it as fast as x = 0.

However, it is important to note that this is not entirely true because the desync of the end crystals spawn and removal can prevent the client from placing end crystal on the same block. This also can be improved by checking for right-clicks and ray trace similarly, but it is not the scope of this module.

## Packet order

When punching at air, the player sends a `ServerboundPunchPacket`. However, they also send this packet in some other cases.

Start breaking block sends a `ServerboundPlayerActionPacket` followed by a `ServerboundPunchPacket`. `ServerboundPunchPacket`s are sent every tick until the player aborts breaking the block, or the block is broken.
<br>
If the block is destroyed in one tick, a `ServerboundPlayerActionPacket` is sent then one `ServerboundPunchPacket` is sent.

Attacking an entity sends a `ServerboundAttackPacket` followed by a `ServerboundPunchPacket`

From these information, we should account for the module to not process the punch packet if the player previously sent an attack packet. This is because the player's left click attacked an entity already, meaning we should not attempt to attack the entity again. If the player destroys a block in one tick, we also ignore the punch packet. If the player does not destroy the block in one tick, we should ignore punch packets until they abort destroying the block or the block is broken.

