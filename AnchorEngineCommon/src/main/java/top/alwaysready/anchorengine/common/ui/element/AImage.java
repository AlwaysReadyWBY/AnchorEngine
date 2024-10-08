package top.alwaysready.anchorengine.common.ui.element;

import top.alwaysready.anchorengine.common.net.AResourceLocation;
import top.alwaysready.anchorengine.common.net.AResourceManager;
import top.alwaysready.anchorengine.common.string.StringReplacer;
import top.alwaysready.anchorengine.common.ui.layout.board.PinBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Optional;

public class AImage extends UIElement{
    private String url;
    private PinBoard clip;
    private Fill fillMode;

    public void setUrlFromResource(String url) {
        setUrl("res:"+AnchorUtils.toKey(url));
    }

    public void setUrlFromLocal(String localPath){
        setUrl("local:"+localPath);
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Optional<AResourceLocation> getResource(StringReplacer replacer){
        if(getUrl() == null) return Optional.empty();
        String replaced = replacer.apply(getUrl());
        if(replaced.startsWith("%") || replaced.equals("...")) return Optional.empty();
        return AnchorUtils.getService(AResourceManager.class)
                .map(rm -> rm.getOrCreate(replaced));
    }

    public void setClip(PinBoard clip) {
        this.clip = clip;
    }

    public PinBoard getClip() {
        if(clip == null) clip = new PinBoard();
        return clip;
    }

    public void setFillMode(Fill fillMode) {
        this.fillMode = fillMode;
    }

    public Fill getFillMode() {
        if(fillMode == null) fillMode = Fill.SCALE;
        return fillMode;
    }

    public enum Fill {
        STRETCH,
        SCALE,
        SCALE_9,
        FIXED,
        REPEAT
    }
}
