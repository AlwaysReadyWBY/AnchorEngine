package top.alwaysready.anchorengine.common.ui.layout.board;

import top.alwaysready.anchorengine.common.serialization.StringReplacer;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

public class ResolvedBoard {
    private StringReplacer parentReplacer;
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
        pinMap.forEach((key,point)-> getPinMap().put(key, getPin(point)));
        return this;
    }

    public Optional<ResolvedPinPoint> getPin(String key){
        return Optional.ofNullable(getPinMap().get(key));
    }

    public ResolvedPinPoint getPin(PinPoint pin){
        ResolvedPinPoint ref = pin.getRef().map(getReplacer()::apply).map(getPinMap()::get).orElse(null);
        double x = getWidth() * pin.getXGrow(getReplacer()) + pin.getXOffset(getReplacer())
                + (ref == null? getLeft():ref.x());
        double y = getHeight() * pin.getYGrow(getReplacer()) + pin.getYOffset(getReplacer())
                + (ref == null? getTop():ref.y());
        return new ResolvedPinPoint(x,y);
    }

    public Optional<ResolvedBoard> resolveChild(PinBoard def){
        ResolvedPinPoint pin1 = getPin(def.getPin1());
        if(pin1 == null) return Optional.empty();
        ResolvedPinPoint pin2 = getPin(def.getPin2());
        if(pin2 == null) return Optional.empty();
        return Optional.ofNullable(new ResolvedBoard(pin1, pin2, getReplacer().createChild()).resolvePins(def.getPinMap()));
    }

    public ResolvedBoard shift(double x, double y) {
        ResolvedBoard shifted = new ResolvedBoard(getLeft()+x,getRight()+x,getTop()+y,getBottom()+y,getReplacer());
        Map<String, ResolvedPinPoint> shiftedPinMap = shifted.getPinMap();
        getPinMap().forEach((key,point)-> shiftedPinMap.put(key,point.shift(x,y)));
        return shifted;
    }

    private StringReplacer getParentReplacer() {
        if(parentReplacer == null) parentReplacer = getReplacer();
        return parentReplacer;
    }

    public ResolvedBoard newReplacer(){
        replacer = getParentReplacer().createChild();
        return this;
    }

    public RenderBounds getBounds(){
        return new RenderBounds(getLeft(),getRight(),getTop(),getBottom());
    }
}
