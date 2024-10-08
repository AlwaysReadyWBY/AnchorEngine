package top.alwaysready.anchorengine.common.ui.layout.board;

import java.util.Objects;

public final class RenderBounds {
    private final double left;
    private final double right;
    private final double top;
    private final double bottom;
    private Double width;
    private Double height;

    public RenderBounds(double left, double right, double top, double bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public double width() {
        if(width == null) width = right() - left();
        return width;
    }

    public double height() {
        if(height == null) height = bottom() - top();
        return height;
    }

    public RenderBounds intersect(RenderBounds another) {
        if (another == null) return null;
        if (left() > another.right() || right() < another.left()) return null;
        if (top() > another.bottom() || bottom() < another.top()) return null;
        return new RenderBounds(
                Math.max(left(), another.left()),
                Math.min(right(), another.right()),
                Math.max(top(), another.top()),
                Math.min(bottom(), another.bottom()));
    }

    public double left() {
        return left;
    }

    public double right() {
        return right;
    }

    public double top() {
        return top;
    }

    public double bottom() {
        return bottom;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RenderBounds) obj;
        return Double.doubleToLongBits(this.left) == Double.doubleToLongBits(that.left) &&
                Double.doubleToLongBits(this.right) == Double.doubleToLongBits(that.right) &&
                Double.doubleToLongBits(this.top) == Double.doubleToLongBits(that.top) &&
                Double.doubleToLongBits(this.bottom) == Double.doubleToLongBits(that.bottom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, top, bottom);
    }

    @Override
    public String toString() {
        return "RenderBounds[" +
                "left=" + left + ", " +
                "right=" + right + ", " +
                "top=" + top + ", " +
                "bottom=" + bottom + ']';
    }

}
