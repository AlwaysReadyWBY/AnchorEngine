package top.alwaysready.anchorengine.common.server.menu;

import com.google.gson.annotations.SerializedName;
import top.alwaysready.anchorengine.common.action.Action;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Menu {
    @SerializedName("requiredPermissions")
    private List<String> perms;
    private String id;
    private String ui;
    @SerializedName("actions")
    private Map<String, Action> actionMap;

    @SerializedName("var")
    private Map<String,String> varMap;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUI(String ui) {
        this.ui = ui;
    }

    public String getUI() {
        return ui;
    }

    protected Map<String, Action> getActionMap() {
        if(actionMap == null) actionMap = new ConcurrentHashMap<>();
        return actionMap;
    }

    protected Map<String, String> getVarMap(){
        if(varMap == null) varMap = new ConcurrentHashMap<>();
        return varMap;
    }

    public List<String> getPerms() {
        if(perms==null) perms = new ArrayList<>();
        return perms;
    }

    public void open(UUID playerId, Consumer<AControlChannel> channelOp){
        if(playerId == null){
            AnchorUtils.info("Got null uuid, something is wrong!");
            return;
        }
        AnchorUtils.getService(ScheduleService.class).ifPresent(sch->sch.scheduleAsync(()->{
            JsonPacketUtils.S2C.setScreen(playerId, getUI(),ch-> {
                getActionMap().forEach(ch::registerAction);
                getVarMap().forEach(ch::registerVar);
                channelOp.accept(ch);
            });
        }));
    }
    public void open(UUID playerId){
        open(playerId,ch->{});
    }
}
