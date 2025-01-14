package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.minecraft.client.gui.DrawContext;
import top.alwaysready.anchorengine.common.ui.element.ARect;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Optional;

public class ARectDrawable extends AnchorDrawable<ARect>{
    private Integer fill;
    private Integer border;

    public ARectDrawable(ARect elem) {
        super(elem);
    }

    public void setFill(Integer fill) {
        this.fill = fill;
    }

    public Optional<Integer> getFill() {
        return Optional.ofNullable(fill);
    }

    public void setBorder(Integer border) {
        this.border = border;
    }

    public Optional<Integer> getBorder() {
        return Optional.ofNullable(border);
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
        getElement().getFill().flatMap(region.getReplacer()::getAsHexInt).ifPresent(this::setFill);
        getElement().getBorder().flatMap(region.getReplacer()::getAsHexInt).ifPresent(this::setBorder);
        setPreferredWidth(region.getWidth());
        setPreferredHeight(region.getHeight());
    }

    @Override
    protected void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        getBounds().ifPresent(bounds -> {
            context.enableScissor((int) bounds.left(), (int) bounds.top(), (int) bounds.right(), (int) bounds.bottom());
            getBorder().ifPresent(color->drawBorder(context,
                    (int) bounds.left(),
                    (int) bounds.top(),
                    (int) bounds.width(),
                    (int) bounds.height(),
                    getZ(),
                    color));
            getFill().ifPresent(color ->context.fill((int) bounds.left(),
                    (int) bounds.top(),
                    (int) bounds.right(),
                    (int) bounds.bottom(),
                    getZ(),
                    color));
            context.disableScissor();
        });
    }

    public void drawBorder(DrawContext context,int x, int y, int width, int height, int z,int color) {
        context.fill(x, y, x + width, y + 1,z, color);
        context.fill(x, y + height - 1, x + width, y + height,z, color);
        context.fill(x, y + 1, x + 1, y + height - 1, z,color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1,z, color);
    }
}
