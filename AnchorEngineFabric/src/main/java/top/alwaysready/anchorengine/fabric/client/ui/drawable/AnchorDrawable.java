package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import top.alwaysready.anchorengine.common.ui.element.UIElement;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class AnchorDrawable<T extends UIElement> implements Drawable, Element, Selectable {

    private final T elem;
    private ResolvedBoard region;
    private RenderBounds bounds;
    private double preferredWidth = 0;
    private double preferredHeight = 0;
    private double alignOffsetX = 0;
    private double alignOffsetY = 0;
    private double hAlign = 0;
    private double vAlign = 0;
    private boolean hWrap = false;
    private boolean vWrap = true;
    private boolean focused = false;
    private int index = 0;
    private float totalDelta = 0;

    private final Object renderLock = new Object();
    private boolean updating;

    protected AnchorDrawable(T elem) {
        this.elem = elem;
    }

    public T getElement() {
        return elem;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void update(ResolvedBoard parent) {
        if(isUpdating()) return;
        updating = true;
        synchronized (renderLock) {
            updateRegion(parent);
            getRegion().ifPresent(region -> {
                updateForRegion(region);
                setRegion(applyAlignAndWrap(region));
            });
        }
        updating = false;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setHAlign(double hAlign) {
        this.hAlign = hAlign;
    }

    public double getHAlign() {
        return hAlign;
    }

    public void setVAlign(double vAlign) {
        this.vAlign = vAlign;
    }

    public double getVAlign() {
        return vAlign;
    }

    public void setHWrap(boolean hWrap) {
        this.hWrap = hWrap;
    }

    public boolean isHWrap() {
        return hWrap;
    }

    public void setVWrap(boolean vWrap) {
        this.vWrap = vWrap;
    }

    public boolean isVWrap() {
        return vWrap;
    }

    public void setAlignOffsetX(double alignOffsetX) {
        this.alignOffsetX = alignOffsetX;
    }

    public double getAlignOffsetX() {
        return alignOffsetX;
    }

    public void setAlignOffsetY(double alignOffsetY) {
        this.alignOffsetY = alignOffsetY;
    }

    public double getAlignOffsetY() {
        return alignOffsetY;
    }

    public synchronized void setRegion(ResolvedBoard region) {
        this.region = region;
    }

    public synchronized Optional<ResolvedBoard> getRegion() {
        return Optional.ofNullable(region);
    }

    protected void updateRegion(ResolvedBoard parent){
        if(parent == null) {
            setRegion(null);
            return;
        }
        setRegion(parent.resolveChild(getElement().getLayout()).orElse(null));
        setHWrap(region.getReplacer().getAsInt(getElement().getLayout().getHWrap())
                .map(i -> i>0)
                .orElse(false));
        setVWrap(region.getReplacer().getAsInt(getElement().getLayout().getVWrap())
                .map(i -> i>0)
                .orElse(false));
        setHAlign(region.getReplacer().getAsDouble(getElement().getLayout().getHAlign()).orElse(0d));
        setVAlign(region.getReplacer().getAsDouble(getElement().getLayout().getVAlign()).orElse(0d));
    }

    protected abstract void updateForRegion(ResolvedBoard region);

    protected ResolvedBoard applyAlignAndWrap(ResolvedBoard region){
        double width = isHWrap()? Math.max(region.getWidth(),getPreferredWidth()): region.getWidth();
        double height = isVWrap()? Math.max(region.getHeight(),getPreferredHeight()): region.getHeight();
        double left = region.getLeft() + getHAlign()*(region.getWidth() - width);
        double top = region.getTop() + getVAlign()*(region.getHeight() - height);
        setAlignOffsetX(isHWrap()? 0: getHAlign() * (width - getPreferredWidth()));
        setAlignOffsetY(isVWrap()? 0: getVAlign() * (height - getPreferredHeight()));
        return new ResolvedBoard(left, left + width, top, top + height, region.getReplacer())
                .resolvePins(getElement().getLayout().getPinMap());
    }

    public void shift(double offsetX, double offsetY) {
        getRegion().ifPresent(region -> setRegion(region.shift(offsetX,offsetY)));
    }

    public Optional<RenderBounds> getBounds() {
        return Optional.ofNullable(bounds);
    }

    public void setPreferredHeight(double preferredHeight) {
        this.preferredHeight = preferredHeight;
    }

    public double getPreferredHeight() {
        return preferredHeight;
    }

    public void setPreferredWidth(double preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    public double getPreferredWidth() {
        return preferredWidth;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getBounds().map(bounds -> mouseY >= bounds.top()
                        && mouseY <= bounds.bottom()
                        && mouseX >= bounds.left()
                        && mouseX <= bounds.right())
                .orElse(false);
    }
    protected abstract void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta);

    public void render(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        totalDelta += delta;
        synchronized (renderLock) {
            bounds = getRegion().map(ResolvedBoard::getBounds)
                    .map(parentBounds::intersect)
                    .orElse(null);
            renderImpl(context, parentBounds, mouseX, mouseY, delta);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        getRegion().map(ResolvedBoard::getBounds).ifPresent(bounds ->
                render(context,bounds,mouseX,mouseY,delta));
    }

    @Override
    public SelectionType getType() {
        return isFocused()? SelectionType.FOCUSED:SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return Element.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return Element.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public float getTotalDelta() {
        return totalDelta;
    }
}
