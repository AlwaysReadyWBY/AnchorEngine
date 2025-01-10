package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.minecraft.client.gui.DrawContext;
import top.alwaysready.anchorengine.common.ui.element.AReference;
import top.alwaysready.anchorengine.common.ui.element.UIElement;
import top.alwaysready.anchorengine.common.ui.element.UIElementManager;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Optional;

public class ARefDrawable extends AnchorDrawable<AReference> {
    private AnchorDrawable<?> drawable;
    private UIElement lastElem;

    protected ARefDrawable(AReference elem) {
        super(elem);
    }

    public Optional<AnchorDrawable<?>> getDrawable() {
        return Optional.ofNullable(drawable);
    }

    @Override
    protected void updateForRegion(ResolvedBoard parentRegion) {
        UIElement elem = getElement().getKey()
                .map(parentRegion.getReplacer()::apply)
                .flatMap(key -> AnchorUtils.getService(UIElementManager.class).flatMap(uiMan -> uiMan.getElement(key)))
                .orElse(null);
        if(elem == null) return;
        if(elem != lastElem) {
            lastElem = elem;
            drawable = AnchorUtils.getService(ADrawableManager.class)
                    .flatMap(dMan -> dMan.newRenderer(elem))
                    .orElse(null);
        }
        getRegion().ifPresent(region -> {
            getDrawable().ifPresent(drawable->{
                drawable.update(region);
                setPreferredWidth(drawable.getPreferredWidth());
                setPreferredHeight(drawable.getPreferredHeight());
            });
        });
    }

    @Override
    protected void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        getBounds().ifPresent(bounds -> {
            getDrawable().ifPresent(drawable -> drawable.render(context,bounds,mouseX,mouseY,delta));
        });
    }
}
