package top.alwaysready.anchorengine.common.ui.layout.board;

import java.util.Hashtable;
import java.util.Map;

public class PinBoard {
    private String pin1;
    private String pin2;
    private Map<String, PinPoint> pinMap;
    private String hAlign;
    private String vAlign;
    private String hWrap;
    private String vWrap;

    public void setPin1(String pin1) {
        this.pin1 = pin1;
    }

    public String getPin1() {
        if(pin1 == null) pin1 = "left_top";
        return pin1;
    }

    public void setPin2(String pin2) {
        this.pin2 = pin2;
    }

    public String getPin2() {
        if(pin2 == null) pin2 = "right_bottom";
        return pin2;
    }

    public Map<String, PinPoint> getPinMap() {
        if(pinMap == null) pinMap = new Hashtable<>();
        return pinMap;
    }

    public void setHAlign(String hAlign) {
        this.hAlign = hAlign;
    }

    public String getHAlign() {
        if(hAlign == null) hAlign = "0";
        return hAlign;
    }

    public void setVAlign(String vAlign) {
        this.vAlign = vAlign;
    }

    public String getVAlign() {
        if(vAlign == null) vAlign = "0";
        return vAlign;
    }

    public void setHWrap(String hWrap) {
        this.hWrap = hWrap;
    }

    public String getHWrap() {
        if(hWrap == null) hWrap = "0";
        return hWrap;
    }

    public void setVWrap(String vWrap) {
        this.vWrap = vWrap;
    }

    public String getVWrap() {
        if(vWrap == null) vWrap = "0";
        return vWrap;
    }
}
