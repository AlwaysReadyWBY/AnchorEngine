package top.alwaysready.anchorengine.common.ui.element;

import com.google.gson.annotations.SerializedName;
import top.alwaysready.anchorengine.common.ui.layout.board.PinBoard;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class UIElement {
    private PinBoard layout;
    private String count;
    private String id;

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
}
