package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import top.alwaysready.anchorengine.common.client.ClientChannelHandler;
import top.alwaysready.anchorengine.common.net.channel.AbstractAChannel;
import top.alwaysready.anchorengine.common.ui.element.AScroll;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class AScrollDrawable extends AnchorDrawable<AScroll> {
    private AnchorDrawable<?> child;
    private double hBarHeight = 0;
    private double vBarWidth = 0;
    private double hScrollSpeed = 4;
    private double vScrollSpeed = 4;
    private double offsetX = 0;
    private double offsetY = 0;
    private double maxScrollX = -1;
    private double maxScrollY = -1;
    private boolean scrollingX = false;
    private boolean scrollingY = false;
    private ResolvedBoard contentRegion;
    private RenderBounds contentBounds;
    private UUID context;

    public AScrollDrawable(AScroll elem) {
        super(elem);
    }

    public void setChild(AnchorDrawable<?> child) {
        this.child = child;
    }

    public Optional<AnchorDrawable<?>> getChild() {
        return Optional.ofNullable(child);
    }

    public void setHBarHeight(double hBarHeight) {
        this.hBarHeight = Math.max(0,hBarHeight);
    }

    public double getHBarHeight() {
        return hBarHeight;
    }

    public void setVBarWidth(double vBarWidth) {
        this.vBarWidth = Math.max(0,vBarWidth);
    }

    public double getVBarWidth() {
        return vBarWidth;
    }

    public void setVScrollSpeed(double vScrollSpeed) {
        this.vScrollSpeed = vScrollSpeed;
    }

    public double getVScrollSpeed() {
        return vScrollSpeed;
    }

    public void setHScrollSpeed(double hScrollSpeed) {
        this.hScrollSpeed = hScrollSpeed;
    }

    public double getHScrollSpeed() {
        return hScrollSpeed;
    }

    public void setContentRegion(ResolvedBoard contentRegion) {
        this.contentRegion = contentRegion;
    }

    public Optional<ResolvedBoard> getContentRegion() {
        return Optional.ofNullable(contentRegion);
    }

    public void setMaxScrollX(double maxScrollX) {
        this.maxScrollX = maxScrollX;
        if(offsetX>maxScrollX) offsetX = maxScrollX;
    }

    public double getMaxScrollX() {
        return maxScrollX;
    }

    public void setMaxScrollY(double maxScrollY) {
        this.maxScrollY = maxScrollY;
        if(offsetY>maxScrollY) offsetY = maxScrollY;
    }

    public double getMaxScrollY() {
        return maxScrollY;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = MathHelper.clamp(offsetX,0,getMaxScrollX());
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = MathHelper.clamp(offsetY,0,getMaxScrollY());
    }

    public void setContentBounds(RenderBounds contentBounds) {
        this.contentBounds = contentBounds;
    }

    public Optional<RenderBounds> getContentBounds() {
        return Optional.ofNullable(contentBounds);
    }

    public double getOffsetY() {
        return offsetY;
    }

    public UUID getContext() {
        return context;
    }

    public void setContext(UUID context) {
        this.context = context;
        setOffsetX(0);
        setOffsetY(0);
    }

    @Override
    public void update(ResolvedBoard parent) {
        super.update(parent);
        getRegion().ifPresent(region -> {
            setContentRegion(new ResolvedBoard(
                    region.getPin("left_top").get(),
                    region.getPin("right_bottom").get().shift(-getVBarWidth(),-getHBarHeight()),
                    region.getReplacer()));
        });
        getContentRegion().ifPresent(contentRegion -> {
            child = getChild().orElseGet(()->AnchorUtils.getService(ADrawableManager.class)
                            .flatMap(sv -> getElement().getChild()
                                    .map(sv::newRenderer)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get))
                            .orElse(null));
            if(child == null) return;
            ResolvedBoard childRegion = new ResolvedBoard(
                    contentRegion.getPin("left_top").get().shift(-offsetX,-offsetY),
                    contentRegion.getPin("right_bottom").get().shift(getMaxScrollX()-offsetX,getMaxScrollY()-offsetY),
                    contentRegion.getReplacer())
                    .resolvePins(getElement().getLayout().getPinMap());
            child.update(childRegion);
            setChild(child);
            setMaxScrollX(getHBarHeight()<=0?
                    0:
                    Math.max(0,child.getPreferredWidth() - contentRegion.getWidth()));
            setMaxScrollY(getVBarWidth()<=0?
                    0:
                    Math.max(0,child.getPreferredHeight() - contentRegion.getHeight()));
            AnchorUtils.getService(ClientChannelHandler.class)
                    .map(ClientChannelHandler::getControlChannel)
                    .map(AbstractAChannel::getContext)
                    .filter(context -> context.equals(getContext()))
                    .ifPresent(this::setContext);
        });
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
        region.getReplacer().getAsDouble(getElement().getHBarHeight()).ifPresent(this::setHBarHeight);
        region.getReplacer().getAsDouble(getElement().getVBarWidth()).ifPresent(this::setVBarWidth);
        region.getReplacer().getAsDouble(getElement().getHScrollSpeed()).ifPresent(this::setHScrollSpeed);
        region.getReplacer().getAsDouble(getElement().getVScrollSpeed()).ifPresent(this::setVScrollSpeed);
        setPreferredWidth(region.getWidth());
        setPreferredHeight(region.getHeight());
    }

    @Override
    public void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        getContentRegion().map(ResolvedBoard::getBounds)
                .map(parentBounds::intersect)
                .ifPresent(contentBounds -> {
                    getChild().ifPresent(child -> {
                        child.render(context,contentBounds,mouseX,mouseY,delta);
                        getRegion().map(ResolvedBoard::getBounds)
                                .map(parentBounds::intersect)
                                .ifPresent(bounds -> renderBar(context,bounds,contentBounds));
                    });
                });
    }

    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        getBounds().ifPresent(bounds -> {
            this.scrollingX = button == 0 && mouseY >= bounds.bottom() - getHBarHeight() && mouseY < bounds.bottom();
            this.scrollingY = button == 0 && mouseX >= bounds.right() - getVBarWidth() && mouseX < bounds.right();
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (this.isMouseOver(mouseX, mouseY)) {
            return this.scrollingX || this.scrollingY || getChild()
                    .filter(child -> child.isMouseOver(mouseX,mouseY))
                    .stream().anyMatch(child -> child.mouseClicked(mouseX, mouseY, button));
        } else {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(button == 0) {
            scrollingX = scrollingY = false;
        }
        return getChild()
                .filter(AnchorDrawable::isFocused)
                .map(child -> child.mouseReleased(mouseX,mouseY,button))
                .orElse(false);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && (this.scrollingX)){
            if(getBounds().flatMap(bounds -> getContentBounds().map(contentBounds -> {
                if (mouseX < contentBounds.left()){
                    setOffsetX(0);
                } else if(mouseX > contentBounds.right()){
                    setOffsetX(getMaxScrollX());
                } else {
                    double max = Math.max(1,getMaxScrollX());
                    double barLength = MathHelper.clamp(contentBounds.width()*contentBounds.width()/(getMaxScrollX()+contentBounds.width()),
                            getHBarHeight(),contentBounds.width());
                    double speed = Math.max(1, max/(contentBounds.width() - barLength));
                    setOffsetX(getOffsetX() + deltaX * speed);
                }
                return true;
            })).orElse(false)) return true;
        } else if (button == 0 && (this.scrollingY)) {
            if(getBounds().flatMap(bounds -> getContentBounds().map(contentBounds -> {
                if (mouseY < contentBounds.top()){
                    setOffsetY(0);
                } else if(mouseY > contentBounds.bottom()){
                    setOffsetY(getMaxScrollY());
                } else {
                    double max = Math.max(1,getMaxScrollY());
                    double barLength = MathHelper.clamp(contentBounds.height()*contentBounds.height()/(getMaxScrollY()+contentBounds.height()),
                            getVBarWidth(),contentBounds.height());
                    double speed = Math.max(1, max/(contentBounds.height() - barLength));
                    setOffsetY(getOffsetY() + deltaY * speed);
                }
                return true;
            })).orElse(false)) return true;
        }
        return getChild().filter(child -> child.isMouseOver(mouseX,mouseY))
                .map(child -> child.mouseDragged(mouseX,mouseY,button,deltaX,deltaY))
                .orElse(false);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (getChild().filter(child -> child.isMouseOver(mouseX, mouseY))
                .map(child -> child.mouseScrolled(mouseX,mouseY,amount))
                .orElse(false)) return true;
        if(InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(),InputUtil.GLFW_KEY_LEFT_CONTROL)) {
            if (getMaxScrollX() > 0) {
                this.setOffsetX(getOffsetX() - amount * getHScrollSpeed());
            }
        } else {
            if (getMaxScrollY() > 0) {
                this.setOffsetY(getOffsetY() - amount * getVScrollSpeed());
            }
        }
        return true;
    }

    public void renderBar(DrawContext context, RenderBounds bounds, RenderBounds contentBounds){
        setContentBounds(contentBounds);
        int maxScrX = (int)getMaxScrollX();
        int maxScrY = (int)getMaxScrollY();

        RenderSystem.enableBlend();
        //Horizontal bar
        if (maxScrX > 0) {
            int barLength = (int)(contentBounds.width()*contentBounds.width()/ (maxScrX+contentBounds.width()));
            barLength = (int) MathHelper.clamp(barLength, getHBarHeight(), contentBounds.width());
            int barStart = (int)(getOffsetX() * (contentBounds.width() - barLength) / maxScrX + contentBounds.left());
            if (barStart < contentBounds.left()) {
                barStart = (int)contentBounds.left();
            }
            context.fill((int)contentBounds.left(),(int)contentBounds.bottom(),(int)contentBounds.right(),(int)bounds.bottom(),0x77000000);
            context.fill(barStart, (int)contentBounds.bottom(), barStart + barLength, (int)bounds.bottom(), 0xFF808080);
            context.fill(barStart, (int)contentBounds.bottom(), barStart + barLength - 1, (int)bounds.bottom(), 0xFFC0C0C0);
        }
        //Vertical bar
        if (maxScrY > 0) {
            int barLength = (int)(contentBounds.height()*contentBounds.height()/ (maxScrY+contentBounds.height()));
            barLength = (int) MathHelper.clamp(barLength, getVBarWidth(), contentBounds.height());
            int barStart = (int)(getOffsetY() * (contentBounds.height() - barLength) / maxScrY + contentBounds.top());
            if (barStart < contentBounds.top()) {
                barStart = (int)contentBounds.top();
            }
            context.fill((int)contentBounds.right(),(int)contentBounds.top(),(int)bounds.right(),(int)contentBounds.bottom(),0x77000000);
            context.fill((int)contentBounds.right(), barStart, (int)bounds.right(), barStart + barLength, 0xFF808080);
            context.fill((int)contentBounds.right(), barStart, (int)bounds.right() - 1, barStart + barLength - 1, 0xFFC0C0C0);
        }
        RenderSystem.disableBlend();
    }
}
