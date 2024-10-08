package top.alwaysready.anchorengine.common.ui.layout;

import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

public abstract class Layout {
    public static final String BOARD = AnchorUtils.toKey("board");
    public static final String LINEAR_HORIZONTAL = AnchorUtils.toKey("linear_horizontal");
    public static final String LINEAR_VERTICAL = AnchorUtils.toKey("linear_vertical");
    public static final String FLOW_HORIZONTAL = AnchorUtils.toKey("flow_horizontal");
    public static final String FLOW_VERTICAL = AnchorUtils.toKey("flow_vertical");

    private double offsetX = 0;
    private double offsetY = 0;

    public abstract void nextOffset(ResolvedBoard region, double childWidth, double childHeight);

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }
}
