package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.minecraft.client.gui.DrawContext;
import top.alwaysready.anchorengine.common.net.packet.json.OverlayInfo;
import top.alwaysready.anchorengine.common.serialization.StringReplacer;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;

public final class OverlayDrawable {
    private final AnchorDrawable<?> drawable;
    private final OverlayInfo info;
    private final long endMillis;

    public OverlayDrawable(AnchorDrawable<?> drawable, OverlayInfo info) {
        this.drawable = drawable;
        this.info = info;
        endMillis = System.currentTimeMillis() + info.getLifeMillis();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float tickDelta) {
        getDrawable().render(context, mouseX, mouseY, tickDelta);
    }

    public void update(ResolvedBoard hudRegion) {
        ResolvedBoard region = hudRegion.newReplacer();
        StringReplacer replacer = region.getReplacer();
        info.getVarMap().forEach(replacer::map);
        replacer.map("overlay_life", String.valueOf(getEndMillis()-System.currentTimeMillis()));
        getDrawable().update(region);
    }

    public OverlayInfo getInfo() {
        return info;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public AnchorDrawable<?> getDrawable() {
        return drawable;
    }

}
