package top.alwaysready.anchorengine.common.net.packet.json;

import com.google.gson.annotations.SerializedName;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OverlayInfo {
    private String id;
    private String ui;
    private long lifeMillis;

    @SerializedName("var")
    private Map<String,String> varMap;

    public OverlayInfo(String id,String ui,long lifeMillis){
        this.id = id;
        this.ui = ui;
        this.lifeMillis = lifeMillis;
    }

    public String getUI() {
        return ui;
    }

    public long getLifeMillis() {
        return lifeMillis;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getVarMap(){
        if(varMap == null) varMap = new Hashtable<>();
        return varMap;
    }
}
