package top.alwaysready.anchorengine.common.ui.element;

import top.alwaysready.anchorengine.common.ui.layout.board.PinBoard;

import java.util.Optional;

public class UIElement {
    private PinBoard layout;
    private String count;
    private String id;

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
}
