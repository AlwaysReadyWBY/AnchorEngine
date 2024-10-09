package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import top.alwaysready.anchorengine.common.string.StringParser;
import top.alwaysready.anchorengine.common.ui.element.AGroup;
import top.alwaysready.anchorengine.common.ui.element.UIElement;
import top.alwaysready.anchorengine.common.ui.layout.BoardLayout;
import top.alwaysready.anchorengine.common.ui.layout.Layout;
import top.alwaysready.anchorengine.common.ui.layout.LayoutFactory;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AGroupDrawable extends AnchorDrawable<AGroup> {
    private List<AnchorDrawable<?>> children = new ArrayList<>();

    public AGroupDrawable(AGroup elem) {
        super(elem);
    }

    public List<AnchorDrawable<?>> getChildren() {
        return children;
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
        AnchorUtils.getService(ADrawableManager.class).ifPresent(sv -> {
            Layout layout = AnchorUtils.getRegistry(LayoutFactory.class)
                    .flatMap(registry -> registry.get(region.getReplacer().apply(getElement().getLayoutMode())))
                    .orElse(BoardLayout::new)
                    .newLayout();
            double right = region.getLeft();
            double bottom = region.getTop();
            int totalCount = getElement().getChildren().stream()
                    .map(UIElement::getCount)
                    .map(region.getReplacer()::getAsInt)
                    .flatMap(Optional::stream)
                    .mapToInt(i->i)
                    .sum();
            if(totalCount == getChildren().size()){
                for (AnchorDrawable<?> child : getChildren()) {
                    UIElement childElem = child.getElement();
                    if(childElem == null) continue;
                    String id = childElem.getId().map(region.getReplacer()::apply).orElse("");
                    String indexStr = id.isEmpty() ? "index" : "index_" + id;
                    int index = child.getIndex();
                    ResolvedBoard contentRegion = region.shift(layout.getOffsetX(), layout.getOffsetY()).newReplacer();
                    contentRegion.getReplacer().map(indexStr, String.valueOf(index));
                    child.update(contentRegion);
                    Optional<ResolvedBoard> opt = child.getRegion();
                    if (opt.isEmpty()) continue;
                    ResolvedBoard childRegion = opt.get();
                    double childWidth = Math.max(childRegion.getRight(), contentRegion.getRight()) - Math.min(childRegion.getLeft(), contentRegion.getLeft());
                    double childHeight = Math.max(childRegion.getBottom(), contentRegion.getBottom()) - Math.min(childRegion.getTop(), contentRegion.getTop());
                    right = Math.max(right, contentRegion.getLeft() + childWidth);
                    bottom = Math.max(bottom, contentRegion.getTop() + childHeight);
                    layout.nextOffset(region, childWidth, childHeight);
                }
            } else {
                List<AnchorDrawable<?>> children = new ArrayList<>();
                for (UIElement childElem : getElement().getChildren()) {
                    String id = childElem.getId().map(region.getReplacer()::apply).orElse("");
                    int count = region.getReplacer().getAsInt(childElem.getCount()).orElse(1);
                    String indexStr = id.isEmpty() ? "index" : "index_" + id;
                    for (int i = 0; i < count; i++) {
                        Optional<AnchorDrawable<?>> childOpt = sv.newRenderer(childElem);
                        if (childOpt.isEmpty()) continue;
                        AnchorDrawable<?> child = childOpt.get();
                        children.add(child);
                        ResolvedBoard contentRegion = region.shift(layout.getOffsetX(), layout.getOffsetY()).newReplacer();
                        child.setIndex(i);
                        contentRegion.getReplacer().map(indexStr, String.valueOf(i));
                        child.update(contentRegion);
                        Optional<ResolvedBoard> opt = child.getRegion();
                        if (opt.isEmpty()) continue;
                        ResolvedBoard childRegion = opt.get();
                        double childWidth = Math.max(childRegion.getRight(), contentRegion.getRight()) - Math.min(childRegion.getLeft(), contentRegion.getLeft());
                        double childHeight = Math.max(childRegion.getBottom(), contentRegion.getBottom()) - Math.min(childRegion.getTop(), contentRegion.getTop());
                        right = Math.max(right, contentRegion.getLeft() + childWidth);
                        bottom = Math.max(bottom, contentRegion.getTop() + childHeight);
                        layout.nextOffset(region, childWidth, childHeight);
                    }
                }
                this.children = children;
            }
            setPreferredWidth(right - region.getLeft());
            setPreferredHeight(bottom - region.getTop());
        });
    }

    @Override
    protected ResolvedBoard applyAlignAndWrap(ResolvedBoard region) {
        ResolvedBoard ret = super.applyAlignAndWrap(region);
        double offsetX = ret.getLeft() - region.getLeft();
        double offsetY = ret.getTop() - region.getTop();
        getChildren().forEach(child -> child.shift(offsetX,offsetY));
        return ret;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return getChildren().stream()
                .filter(child -> child.isMouseOver(mouseX,mouseY))
                .anyMatch(child -> child.mouseClicked(mouseX,mouseY,button));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return getChildren().stream()
                .filter(child -> child.isMouseOver(mouseX,mouseY))
                .anyMatch(child -> child.mouseReleased(mouseX,mouseY,button));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return getChildren().stream()
                .filter(child -> child.isMouseOver(mouseX,mouseY))
                .anyMatch(child -> child.mouseDragged(mouseX,mouseY,button,deltaX,deltaY));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return getChildren().stream()
                .filter(child -> child.isMouseOver(mouseX,mouseY))
                .anyMatch(child -> child.mouseScrolled(mouseX,mouseY,amount));
    }

    @Override
    public void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        getBounds().ifPresent(bounds -> getChildren().forEach(child -> child.render(context,bounds,mouseX,mouseY,delta)));
    }
}
