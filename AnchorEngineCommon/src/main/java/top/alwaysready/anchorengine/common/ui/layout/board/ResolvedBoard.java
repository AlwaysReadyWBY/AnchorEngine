package top.alwaysready.anchorengine.common.ui.layout.board;

import top.alwaysready.anchorengine.common.string.StringReplacer;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class ResolvedBoard {
    private StringReplacer replacer;
    private final double left;
    private final double right;
    private final double top;
    private final double bottom;
    private final double width;
    private final double height;
    private final Map<String,ResolvedPinPoint> pinMap = new Hashtable<>();

    public ResolvedBoard(double width,double height,StringReplacer replacer){
        this(0,width,0,height,replacer);
    }

    public ResolvedBoard(ResolvedPinPoint p1,ResolvedPinPoint p2,StringReplacer replacer) {
        this(Math.min(p1.x(),p2.x()),
                Math.max(p1.x(),p2.x()),
                Math.min(p1.y(),p2.y()),
                Math.max(p1.y(),p2.y()),
                replacer);
    }

    public ResolvedBoard(double left,double right,double top,double bottom,StringReplacer replacer){
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.width = right - left;
        this.height = bottom - top;
        this.replacer = replacer;
        addDefaultPins();
    }

    protected void addDefaultPins(){
        double cx = getLeft() + getWidth()*0.5;
        double cy = getTop() + getHeight()*0.5;
        pinMap.put("left_top",new ResolvedPinPoint(getLeft(),getTop()));
        pinMap.put("left_center",new ResolvedPinPoint(getLeft(),cy));
        pinMap.put("left_bottom",new ResolvedPinPoint(getLeft(),getBottom()));
        pinMap.put("center_top",new ResolvedPinPoint(cx,getTop()));
        pinMap.put("center",new ResolvedPinPoint(cx,cy));
        pinMap.put("center_bottom",new ResolvedPinPoint(cx,getBottom()));
        pinMap.put("right_top",new ResolvedPinPoint(getRight(),getTop()));
        pinMap.put("right_center",new ResolvedPinPoint(getRight(),cy));
        pinMap.put("right_bottom",new ResolvedPinPoint(getRight(),getBottom()));
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }

    public StringReplacer getReplacer() {
        return replacer;
    }

    public Map<String, ResolvedPinPoint> getPinMap() {
        return pinMap;
    }

    public ResolvedBoard resolvePins(Map<String,PinPoint> pinMap){
        pinMap.forEach((key,point)-> {
            double x = getLeft() + getWidth() * point.getXGrow(getReplacer()) + point.getXOffset(getReplacer());
            double y = getTop() + getHeight() * point.getYGrow(getReplacer()) + point.getYOffset(getReplacer());
            getPinMap().put(key, new ResolvedPinPoint(x,y));
        });
        return this;
    }

    public Optional<ResolvedPinPoint> getPin(String reference){
        return Optional.ofNullable(getPinMap().get(getReplacer().apply(reference)));
    }

    public Optional<ResolvedBoard> resolveChild(PinBoard def){
        ResolvedPinPoint pin1 = getPin(def.getPin1()).orElse(null);
        if(pin1 == null) return Optional.empty();
        ResolvedPinPoint pin2 = getPin(def.getPin2()).orElse(null);
        if(pin2 == null) return Optional.empty();
        return Optional.ofNullable(new ResolvedBoard(pin1, pin2, getReplacer().createChild()).resolvePins(def.getPinMap()));
    }

    public ResolvedBoard shift(double x, double y) {
        ResolvedBoard shifted = new ResolvedBoard(getLeft()+x,getRight()+x,getTop()+y,getBottom()+y,getReplacer());
        Map<String, ResolvedPinPoint> shiftedPinMap = shifted.getPinMap();
        getPinMap().forEach((key,point)-> shiftedPinMap.put(key,point.shift(x,y)));
        return shifted;
    }

    public ResolvedBoard newReplacer(){
        replacer = replacer.createChild();
        return this;
    }

    public RenderBounds getBounds(){
        return new RenderBounds(getLeft(),getRight(),getTop(),getBottom());
    }
}
