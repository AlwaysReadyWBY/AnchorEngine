package top.alwaysready.anchorengine.spigot.net;

import org.bukkit.entity.Player;
import top.alwaysready.anchorengine.common.net.channel.AChannel;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.server.ServerChannelHandler;
import top.alwaysready.readycore.ReadyCore;

import java.util.Optional;
import java.util.UUID;

public class SpigotChannelHandler implements ServerChannelHandler {
    private Player player;
    private final UUID id;
    private final SpigotControlChannel chCtrl = new SpigotControlChannel(this);

    public SpigotChannelHandler(UUID id) {
        this.id = id;
        chCtrl.setOutput(bytes -> getPlayer().ifPresent(player ->
                player.sendPluginMessage(ReadyCore.getInstance().getPlugin(), AChannel.CONTROL, bytes)));
    }

    @Override
    public UUID getPlayerId() {
        return id;
    }

    public Optional<Player> getPlayer() {
        if(player == null) player = ReadyCore.getInstance().getPlugin().getServer().getPlayer(getPlayerId());
        return Optional.ofNullable(player);
    }

    @Override
    public AControlChannel getControlChannel() {
        return chCtrl;
    }
}
