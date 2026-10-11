package me.karven.lagless.injector;

import io.netty.channel.Channel;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InjectorEventListener implements Listener {
    private static final String LAGLESS_HANDLER_NAME = "lagless_packet_handler";
    private static final String PACKET_HANDLER_NAME = "packet_handler";

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        eject(event.getPlayer());
    }

    private void inject(final Player player) {
        getChannel(player).pipeline().addBefore(
                PACKET_HANDLER_NAME,
                LAGLESS_HANDLER_NAME,
                new OutboundPacketHandler(player)
        );
    }

    private void eject(final Player player) {
        final Channel channel = getChannel(player);
        channel.eventLoop().execute(() -> {
            if (channel.pipeline().get(LAGLESS_HANDLER_NAME) == null) return;
            channel.pipeline().remove(LAGLESS_HANDLER_NAME);
        });
    }

    private Channel getChannel(final Player player) {
        return ((CraftPlayer) player).getHandle().connection.connection.channel;
    }
}
