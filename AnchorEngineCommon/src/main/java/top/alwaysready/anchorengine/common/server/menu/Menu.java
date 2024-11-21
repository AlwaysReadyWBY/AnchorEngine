package top.alwaysready.anchorengine.common.server.menu;

import com.google.gson.annotations.SerializedName;
import top.alwaysready.anchorengine.common.action.Action;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Menu {
    @SerializedName("requiredPermissions")
    private List<String> perms;
    private String id;
    private String ui;
    @SerializedName("actions")
    private Map<String, Action> actionMap;

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

    public List<String> getPerms() {
        if(perms==null) perms = new ArrayList<>();
        return perms;
    }

    public void open(UUID playerId){
        if(playerId == null){
            AnchorUtils.info("Got null uuid, something is wrong!");
            return;
        }
        AnchorUtils.getService(ScheduleService.class).ifPresent(sch->sch.scheduleAsync(()->{
            JsonPacketUtils.S2C.setScreen(playerId, getUI(),ch-> getActionMap().forEach(ch::registerAction));
        }));
    }
}
