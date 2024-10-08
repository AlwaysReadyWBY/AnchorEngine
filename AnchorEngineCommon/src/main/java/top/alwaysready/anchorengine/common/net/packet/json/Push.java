package top.alwaysready.anchorengine.common.net.packet.json;

import com.google.gson.annotations.SerializedName;
import top.alwaysready.anchorengine.common.ui.element.UIElement;

import java.util.Hashtable;
import java.util.Map;

public class Push {
    @SerializedName("ui")
    private Map<String,UIElement> uiMap;
    @SerializedName("var")
    private Map<String,String> varMap;

    public Map<String, UIElement> getUIMap() {
        if(uiMap == null) uiMap = new Hashtable<>();
        return uiMap;
    }

    public Map<String, String> getVarMap() {
        if(varMap == null) varMap = new Hashtable<>();
        return varMap;
    }
}
