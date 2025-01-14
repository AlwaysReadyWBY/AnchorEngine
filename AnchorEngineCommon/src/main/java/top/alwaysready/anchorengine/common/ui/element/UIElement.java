package top.alwaysready.anchorengine.common.ui.element;

import com.google.gson.annotations.SerializedName;
import top.alwaysready.anchorengine.common.serialization.SerializableText;
import top.alwaysready.anchorengine.common.ui.layout.board.PinBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class UIElement {
    private PinBoard layout;
    private String count;
    private String id;
    private SerializableText tooltip;
    private String z;

    @SerializedName("var")
    private Map<String,String> varMap;

    public PinBoard getLayout() {
        if(layout == null) layout = new PinBoard();
        return layout;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCount() {
        if(count == null) count = "1";
        if(!count.equals("1")) AnchorUtils.info(count);
        return count;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    public Map<String, String> getVarMap() {
        if(varMap == null) varMap = new Hashtable<>();
        return varMap;
    }

    public void setTooltip(SerializableText tooltip) {
        this.tooltip = tooltip;
    }

    public Optional<SerializableText> getTooltip() {
        return Optional.ofNullable(tooltip);
    }

    public String getZ() {
        if(z==null) z = "0";
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }
}
