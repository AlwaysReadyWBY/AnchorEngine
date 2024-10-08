package top.alwaysready.anchorengine.common.ui.layout;

import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;

public class LinearLayout extends Layout{
    private final boolean vertical;

    public LinearLayout(boolean vertical) {
        super();
        this.vertical = vertical;
    }

    public boolean isVertical() {
        return vertical;
    }

    @Override
    public void nextOffset(ResolvedBoard region, double childWidth, double childHeight) {
        if(isVertical()){
            setOffsetY(getOffsetY() + childHeight);
        } else {
            setOffsetX(getOffsetX() + childWidth);
        }
    }
}
