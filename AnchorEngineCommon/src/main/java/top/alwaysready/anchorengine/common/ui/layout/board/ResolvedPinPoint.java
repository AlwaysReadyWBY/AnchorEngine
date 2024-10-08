package top.alwaysready.anchorengine.common.ui.layout.board;

public record ResolvedPinPoint(double x,double y) {
    public ResolvedPinPoint shift(double x, double y) {
        return new ResolvedPinPoint(x()+x,y()+y);
    }
}
