package top.alwaysready.anchorengine.spigot.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import top.alwaysready.anchorengine.common.server.ServerChannelManager;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent ev){
        AnchorUtils.getService(ServerChannelManager.class).ifPresent(
                chMan -> chMan.unloadHandler(ev.getPlayer().getUniqueId()));
    }
}
