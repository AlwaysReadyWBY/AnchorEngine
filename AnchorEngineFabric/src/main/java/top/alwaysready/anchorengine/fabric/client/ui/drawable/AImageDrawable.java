package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import top.alwaysready.anchorengine.common.ui.element.AImage;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedPinPoint;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.client.resource.ATexture;
import top.alwaysready.anchorengine.fabric.client.resource.ATextureManager;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AImageDrawable extends AnchorDrawable<AImage> {
    private ATexture texture;
    private ResolvedBoard textureRegion;

    protected AImageDrawable(AImage elem) {
        super(elem);
        AnchorUtils.getService(ATextureManager.class).ifPresent(tm -> setTexture(tm.getLoadingTexture()));
    }

    public void setTexture(ATexture texture) {
        this.texture = texture;
    }

    public Optional<ATexture> getTexture() {
        return Optional.ofNullable(texture);
    }

    public void setTextureRegion(ResolvedBoard textureRegion) {
        this.textureRegion = textureRegion;
    }

    public Optional<ResolvedBoard> getTextureRegion() {
        return Optional.ofNullable(textureRegion);
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
        getElement().getResource(region.getReplacer()).ifPresent(loc -> {
            AnchorUtils.getService(ATextureManager.class).ifPresent(tm ->
                    tm.getOrLoad(loc).whenComplete((texture,thr) -> {
                        if(texture == null || thr!=null) texture = tm.getMissingTexture();
                        new ResolvedBoard(new ResolvedPinPoint(0,0),
                                new ResolvedPinPoint(texture.getWidth(),texture.getHeight()),
                                region.getReplacer())
                                .resolveChild(getElement().getClip())
                                .ifPresent(this::setTextureRegion);
                        setTexture(texture);
                        switch (getElement().getFillMode()){
                            case REPEAT,STRETCH,SCALE_9 -> {
                                setPreferredWidth(region.getWidth());
                                setPreferredHeight(region.getHeight());
                            }
                            case FIXED -> {
                                setPreferredWidth(texture.getWidth());
                                setPreferredHeight(texture.getHeight());
                            }
                            case SCALE -> {
                                double scale = Math.min(region.getWidth()/texture.getWidth(),region.getHeight()/texture.getHeight());
                                setPreferredWidth(texture.getWidth()*scale);
                                setPreferredHeight(texture.getHeight()*scale);
                            }
                        }
                    }));
        });
    }

    @Override
    public void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        getRegion().ifPresent(region -> {
            getTextureRegion().ifPresent(tRegion ->{
                getTexture().ifPresent(texture -> {
                    getBounds().ifPresent(bounds ->{
                        RenderSystem.enableBlend();
                        context.enableScissor((int) bounds.left(), (int) bounds.top(), (int) bounds.right(), (int) bounds.bottom());
                        switch (getElement().getFillMode()){
                            case FIXED -> drawFixed(context,texture,region,tRegion);
                            case SCALE -> drawScaled(context,texture,region,tRegion);
                            case STRETCH -> drawStretched(context,texture,region,tRegion);
                            case SCALE_9 -> draw9Patched(context,texture,region,tRegion);
                            case REPEAT -> drawRepeated(context,texture,region,tRegion);
                        }
                        context.disableScissor();
                        RenderSystem.disableBlend();
                    });
                });
            });
        });
    }

    protected void drawFixed(DrawContext context,ATexture texture,ResolvedBoard region,ResolvedBoard tRegion){
        double width = tRegion.getWidth();
        double height = tRegion.getHeight();
        double left = getAlignOffsetX()+ region.getLeft();
        double top = getAlignOffsetY() + region.getTop();
        context.drawTexture(texture.getId(),
                (int) left,
                (int) top,
                (int) width,
                (int) height,
                (float) tRegion.getLeft(),
                (float) tRegion.getTop(),
                (int) tRegion.getWidth(),
                (int) tRegion.getHeight(),
                texture.getWidth(),
                texture.getHeight());
    }

    protected void drawScaled(DrawContext context,ATexture texture,ResolvedBoard region,ResolvedBoard tRegion){
        double scale = Math.min(region.getWidth()/tRegion.getWidth(),region.getHeight()/tRegion.getHeight());
        double width = scale*tRegion.getWidth();
        double height = scale*tRegion.getHeight();
        double left = getAlignOffsetX() + region.getLeft();
        double top = getAlignOffsetY() + region.getTop();
        context.drawTexture(texture.getId(),
                (int) left,
                (int) top,
                (int) width,
                (int) height,
                (float) tRegion.getLeft(),
                (float) tRegion.getTop(),
                (int) tRegion.getWidth(),
                (int) tRegion.getHeight(),
                texture.getWidth(),
                texture.getHeight());
    }

    protected void draw9Patched(DrawContext context, ATexture texture, ResolvedBoard region, ResolvedBoard tRegion){
        tRegion.getPin("9p_lt").ifPresentOrElse(tPin1 -> {
            ResolvedPinPoint tPin2 = tRegion.getPin("9p_rb")
                    .orElseGet(()->new ResolvedPinPoint(
                            tRegion.getLeft() + tRegion.getRight() - tPin1.x(),
                            tRegion.getTop() + tRegion.getBottom() - tPin1.y()));
            ResolvedPinPoint pin1 = region.getPin("9p_lt")
                    .orElseGet(() -> new ResolvedPinPoint(
                            region.getLeft() + tPin1.x() - tRegion.getLeft(),
                            region.getTop() + tPin1.y() - tRegion.getTop()));
            ResolvedPinPoint pin2 = region.getPin("9p_rb")
                    .orElseGet(()-> new ResolvedPinPoint(
                            region.getRight() + tPin2.x() - tRegion.getRight(),
                            region.getBottom() + tPin2.y() - tRegion.getBottom()));
            int lw = (int) (pin1.x()-region.getLeft());
            int cw = (int) (pin2.x()-pin1.x());
            int rw = (int) (region.getRight() - pin2.x());
            int th = (int) (pin1.y()-region.getTop());
            int ch = (int) (pin2.y()-pin1.y());
            int bh = (int) (region.getBottom() - pin2.y());
            int tlw = (int) (tPin1.x()-tRegion.getLeft());
            int tcw = (int) (tPin2.x()-tPin1.x());
            int trw = (int) (tRegion.getRight() - tPin2.x());
            int tth = (int) (tPin1.y()-tRegion.getTop());
            int tch = (int) (tPin2.y()-tPin1.y());
            int tbh = (int) (tRegion.getBottom() - tPin2.y());
            //LEFT_TOP
            context.drawTexture(texture.getId(),
                    (int) region.getLeft(),
                    (int) region.getTop(),
                    lw,
                    th,
                    (float) tRegion.getLeft(),
                    (float) tRegion.getTop(),
                    tlw,
                    tth,
                    texture.getWidth(),
                    texture.getHeight());
            //LEFT_CENTER
            context.drawTexture(texture.getId(),
                    (int) region.getLeft(),
                    (int)region.getTop() + th,
                    lw,
                    ch,
                    (float) tRegion.getLeft(),
                    (float) tRegion.getTop() + tth,
                    tlw,
                    tch,
                    texture.getWidth(),
                    texture.getHeight());
            //LEFT_BOTTOM
            context.drawTexture(texture.getId(),
                    (int) region.getLeft(),
                    (int) region.getTop() + th + ch,
                    lw,
                    bh,
                    (float) tRegion.getLeft(),
                    (float) tRegion.getBottom() - tbh,
                    tlw,
                    tbh,
                    texture.getWidth(),
                    texture.getHeight());
            //CENTER_TOP
            context.drawTexture(texture.getId(),
                    (int) region.getLeft() + lw,
                    (int) region.getTop(),
                    cw,
                    th,
                    (float) tRegion.getLeft() + tlw,
                    (float) tRegion.getTop(),
                    tcw,
                    tth,
                    texture.getWidth(),
                    texture.getHeight());
            //CENTER
            context.drawTexture(texture.getId(),
                    (int) region.getLeft() + lw,
                    (int) region.getTop() + th,
                    cw,
                    ch,
                    (float) tRegion.getLeft()+tlw,
                    (float) tRegion.getTop()+tth,
                    tcw,
                    tth,
                    texture.getWidth(),
                    texture.getHeight());
            //CENTER_BOTTOM
            context.drawTexture(texture.getId(),
                    (int) region.getLeft() + lw,
                    (int) region.getTop() + th + ch,
                    cw,
                    bh,
                    (float) tRegion.getLeft() + tlw,
                    (float) tRegion.getBottom() - tbh,
                    tcw,
                    tbh,
                    texture.getWidth(),
                    texture.getHeight());
            //RIGHT_TOP
            context.drawTexture(texture.getId(),
                    (int) region.getLeft() + lw + cw,
                    (int) region.getTop(),
                    rw,
                    th,
                    (float) tRegion.getRight() - trw,
                    (float) tRegion.getTop(),
                    trw,
                    tth,
                    texture.getWidth(),
                    texture.getHeight());
            //RIGHT_CENTER
            context.drawTexture(texture.getId(),
                    (int) region.getLeft() + lw + cw,
                    (int) region.getTop()+ th,
                    rw,
                    ch,
                    (float) tRegion.getRight() - trw,
                    (float) tRegion.getTop() + tth,
                    trw,
                    tch,
                    texture.getWidth(),
                    texture.getHeight());
            //RIGHT_BOTTOM
            context.drawTexture(texture.getId(),
                    (int) region.getLeft() + lw + cw,
                    (int) region.getTop() + th + ch,
                    rw,
                    bh,
                    (float) tRegion.getRight() - trw,
                    (float) tRegion.getBottom() - tbh,
                    trw,
                    tbh,
                    texture.getWidth(),
                    texture.getHeight());
        },()-> drawStretched(context,texture,region,tRegion));
    }

    protected void drawStretched(DrawContext context,ATexture texture,ResolvedBoard region,ResolvedBoard tRegion){
        context.drawTexture(texture.getId(),
                (int) region.getLeft(),
                (int) region.getTop(),
                (int) region.getWidth(),
                (int) region.getHeight(),
                (float) tRegion.getLeft(),
                (float) tRegion.getTop(),
                (int) tRegion.getWidth(),
                (int) tRegion.getHeight(),
                texture.getWidth(),
                texture.getHeight());
    }

    protected void drawRepeated(DrawContext context,ATexture texture,ResolvedBoard region,ResolvedBoard tRegion){
        double left = region.getLeft();
        while(left < region.getRight()){
            double right = Math.min(left + tRegion.getWidth(),region.getRight());
            double width = Math.min(right - left,tRegion.getWidth());
            double top = region.getTop();
            while(top < region.getBottom()){
                double bottom = Math.min(top + tRegion.getHeight(),region.getBottom());
                double height = Math.min(bottom - top,tRegion.getHeight());
                context.drawTexture(texture.getId(),
                        (int) left,
                        (int) top,
                        (int) width,
                        (int) height,
                        (float) tRegion.getLeft(),
                        (float) tRegion.getTop(),
                        (int) width,
                        (int) height,
                        texture.getWidth(),
                        texture.getHeight());
            }
            left = right;
        }
    }
}
