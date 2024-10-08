package top.alwaysready.anchorengine.common.net.packet.json;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PacketQuery {
    @SerializedName("var")
    private List<String> varList;

    public void setVarList(List<String> varList) {
        this.varList = varList;
    }

    public List<String> getVarList() {
        if(varList.isEmpty()) varList = new ArrayList<>();
        return varList;
    }
}
