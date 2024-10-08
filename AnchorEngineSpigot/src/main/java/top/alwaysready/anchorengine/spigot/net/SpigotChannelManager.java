package top.alwaysready.anchorengine.spigot.net;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import top.alwaysready.anchorengine.common.net.channel.AChannel;
import top.alwaysready.anchorengine.common.server.ServerChannelHandler;
import top.alwaysready.anchorengine.common.server.ServerChannelManager;

import java.util.UUID;

public class SpigotChannelManager extends ServerChannelManager {

    public SpigotChannelManager(JavaPlugin plugin){
        Messenger messenger = plugin.getServer().getMessenger();
        messenger.registerIncomingPluginChannel(plugin, AChannel.CONTROL, (channel, player, message) ->
                getHandler(player.getUniqueId()).getControlChannel().accept(message));
        messenger.registerOutgoingPluginChannel(plugin, AChannel.CONTROL);
    }

    @Override
    public ServerChannelHandler newHandler(UUID id) {
        return new SpigotChannelHandler(id);
    }
}
