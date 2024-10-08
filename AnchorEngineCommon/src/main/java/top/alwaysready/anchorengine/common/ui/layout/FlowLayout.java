package top.alwaysready.anchorengine.common.ui.layout;

import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;

public class FlowLayout extends Layout{
    private final boolean vertical;
    private double max = 0;

    public FlowLayout(boolean vertical) {
        super();
        this.vertical = vertical;
    }

    public boolean isVertical() {
        return vertical;
    }

    @Override
    public void nextOffset(ResolvedBoard region, double childWidth, double childHeight) {
        if(isVertical()){
            max = Math.max(childHeight,max);
            setOffsetX(getOffsetX() + Math.max(childWidth,region.getWidth()));
            if(getOffsetX() > region.getWidth()){
                setOffsetX(0);
                setOffsetY(getOffsetY() + max);
                max = 0;
            }
        } else {
            max = Math.max(childWidth,max);
            setOffsetY(getOffsetY() + Math.max(childHeight,region.getHeight()));
            if(getOffsetY() > region.getHeight()){
                setOffsetY(0);
                setOffsetX(getOffsetX() + max);
                max = 0;
            }
        }
    }
}
