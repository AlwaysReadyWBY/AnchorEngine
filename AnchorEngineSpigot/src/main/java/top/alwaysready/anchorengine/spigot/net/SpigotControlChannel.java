package top.alwaysready.anchorengine.spigot.net;

import com.google.gson.JsonObject;
import me.clip.placeholderapi.PlaceholderAPI;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacket;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketTypes;
import top.alwaysready.anchorengine.common.net.packet.json.PacketQuery;
import top.alwaysready.anchorengine.common.net.packet.json.Push;
import top.alwaysready.anchorengine.common.service.FileService;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.config.AnchorEngineConfig;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public class SpigotControlChannel extends AControlChannel {
    private final SpigotChannelHandler handler;
    private boolean defaultPushSent = false;

    public SpigotControlChannel(SpigotChannelHandler handler) {
        super(true);
        setPlayerId(handler.getPlayerId());
        this.handler = handler;
        registerListener(JsonPacketTypes.C2S.DEBUG,this::handleDebug);
        registerListener(JsonPacketTypes.C2S.QUERY,this::handleQuery);
        newContext();
    }

    public SpigotChannelHandler getHandler() {
        return handler;
    }

    public boolean isDefaultPushSent() {
        return defaultPushSent;
    }

    private boolean handleQuery(JsonPacket jsonPacket) {
        getHandler().getPlayer().ifPresent(player -> {
            jsonPacket.read(PacketQuery.class).ifPresent(query -> {
                if(!isDefaultPushSent()){
                    AnchorUtils.getService(FileService.class).ifPresent(fsv->{
                        AnchorUtils.getService(AnchorEngineConfig.class)
                                .map(AnchorEngineConfig::getAutoPush)
                                .map(Collection::stream)
                                .ifPresent(stream -> stream
                                        .map(str -> "push/"+str)
                                        .map(fsv::getFile)
                                        .filter(File::exists)
                                        .forEach(this::sendPush));
                    });
                    AnchorUtils.getService(AnchorEngineConfig.class).ifPresent(cfg-> cfg.getLoginConfig().getMenu()
                            .flatMap(cfg::getMenu)
                            .ifPresent(menu -> AnchorUtils.getService(ScheduleService.class)
                                    .ifPresent(sch -> sch.schedule(()->menu.open(player.getUniqueId()),
                                            cfg.getLoginConfig().getDelay() * 50L))));
                    defaultPushSent = true;
                }
                Push push = new Push();
                Map<String, String> map = push.getVarMap();
                query.getVarList().forEach(var -> map.put(var,PlaceholderAPI.setPlaceholders(player,"%"+var+"%")));
                send(new JsonPacket(JsonPacketTypes.S2C.PUSH,push));
            });
        });
        return true;
    }

    private boolean handleDebug(JsonPacket jsonPacket) {
        AnchorUtils.info("Received debug packet!");
        send(new JsonPacket(JsonPacketTypes.S2C.DEBUG,new JsonObject()));
        return true;
    }
}
