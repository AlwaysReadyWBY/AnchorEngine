package top.alwaysready.anchorengine.common.server.overlay;

import com.google.gson.annotations.SerializedName;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.net.packet.json.OverlayInfo;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.string.StringReplacer;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Overlay {
    private String id;
    private String ui;
    private String lifeMillis;

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

    public String getLifeMillis() {
        if(lifeMillis == null) lifeMillis = "10000";
        return lifeMillis;
    }

    public long getLifeMillis(StringReplacer replacer){
        return replacer.getAsDouble(getLifeMillis()).map(Double::longValue).orElse(10000L);
    }

    public void setLifeMillis(String lifeMillis) {
        this.lifeMillis = lifeMillis;
    }

    protected Map<String, String> getVarMap(){
        if(varMap == null) varMap = new ConcurrentHashMap<>();
        return varMap;
    }

    public void show(UUID playerId,StringReplacer replacer) {
        if(playerId == null){
            AnchorUtils.info("Got null uuid, something is wrong!");
            return;
        }
        StringReplacer childReplacer = replacer.createChild();
        getVarMap().forEach(childReplacer::map);
        OverlayInfo info = new OverlayInfo(getId(), getUI(), getLifeMillis(childReplacer));
        info.getVarMap().putAll(getVarMap());

        AnchorUtils.getService(ScheduleService.class).ifPresent(sch->sch.scheduleAsync(()->{
            JsonPacketUtils.S2C.addOverlay(playerId,info,ch->{});
        }));
    }

    public void show(UUID playerId,StringReplacer replacer,String id,Map<String,String> varMap) {
        if(playerId == null){
            AnchorUtils.info("Got null uuid, something is wrong!");
            return;
        }
        StringReplacer childReplacer = replacer.createChild();
        getVarMap().forEach(childReplacer::map);
        varMap.forEach(childReplacer::map);
        OverlayInfo info = new OverlayInfo(id, getUI(), getLifeMillis(childReplacer));
        info.getVarMap().putAll(getVarMap());
        info.getVarMap().putAll(varMap);

        AnchorUtils.getService(ScheduleService.class).ifPresent(sch->sch.scheduleAsync(()->{
            JsonPacketUtils.S2C.addOverlay(playerId,info,ch->{});
        }));
    }
}
