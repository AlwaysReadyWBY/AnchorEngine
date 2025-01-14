package top.alwaysready.anchorengine.spigot.api;

import org.bukkit.entity.Player;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.server.menu.Menu;
import top.alwaysready.anchorengine.common.server.overlay.Overlay;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.config.AnchorEngineConfig;
import top.alwaysready.anchorengine.spigot.util.SpigotPlayerReplacer;

import java.util.*;
import java.util.function.Consumer;

public interface AnchorAPI {

    default Optional<AnchorEngineConfig> getConfig(){
        return AnchorUtils.getService(AnchorEngineConfig.class);
    }

    default void sendMenu(Player player, Menu menu,Consumer<AControlChannel> channelOp){
        menu.open(player.getUniqueId(),channelOp);
    }

    default void sendMenu(Player player,String key,Consumer<AControlChannel> channelOp){
        this.getConfig().ifPresent(cfg -> cfg.getMenu(key).ifPresentOrElse(
                    menu -> sendMenu(player,menu,channelOp),
                    ()->cfg.info("%info.menu-not-found%",key))
        );
    }

    default void sendOverlay(Collection<? extends Player> players, String id, Overlay overlay,Consumer<Map<String,String>> setVars){
        Map<String,String> varMap = new Hashtable<>();
        setVars.accept(varMap);
        players.forEach(player -> overlay.show(player.getUniqueId(),new SpigotPlayerReplacer(player), id, varMap));
    }

    default void sendOverlay(Collection<? extends Player> players,String id, String key, Consumer<Map<String,String>> setVars){
        getConfig().ifPresent(cfg -> cfg.getOverlay(key).ifPresentOrElse(
                overlay -> sendOverlay(players, id, overlay, setVars),
                () -> cfg.info("%info.overlay-not-found%",key)
        ));
    }

    default void sendDanmaku(Collection<? extends Player> players,String msg,double y,long millis){
        sendOverlay(players,"danmaku-"+UUID.randomUUID(),"danmaku",varMap->{
            varMap.put("danmaku_text",msg);
            varMap.put("danmaku_y",String.valueOf(y));
            varMap.put("danmaku_time",String.valueOf(millis));
        });
    }
}
