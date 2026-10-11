package me.karven.lagless.injector;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import me.karven.lagless.module.PaperModules;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class OutboundPacketHandler extends ChannelOutboundHandlerAdapter {
    private final Player player;

    public OutboundPacketHandler(final Player player) {
        this.player = player;
    }

    @Override
    public void write(
            final ChannelHandlerContext context,
            final Object packet,
            final ChannelPromise promise) {
        Object modified = packet;
        modified = PaperModules.POSE.handleAndModifyPacket(player, modified);
        context.write(modified, promise);
    }

}
