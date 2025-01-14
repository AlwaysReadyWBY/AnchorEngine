package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import top.alwaysready.anchorengine.common.ui.element.AText;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.fabric.util.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class ATextDrawable extends AnchorDrawable<AText> {
    private List<TextLine> lines = Collections.emptyList();
    private double lineHeight = 12;
    private int color = 0xffffff;

    public ATextDrawable(AText elem) {
        super(elem);
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
        Text text = TextUtils.deserialize(getElement().getText(),region.getReplacer());
        region.getReplacer().getAsDouble(getElement().getLineHeight()).ifPresent(this::setLineHeight);
        region.getReplacer().getAsHexInt(getElement().getColor()).ifPresent(this::setColor);
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        setLines(renderer.wrapLines(text, (int) region.getWidth()).stream()
                .map(line -> new TextLine(line, renderer.getWidth(line)))
                .collect(Collectors.toList()));
        setPreferredWidth(getLines().stream().map(TextLine::width).max(Integer::compareTo).orElse(0));
        setPreferredHeight(getLineHeight() * getLines().size());
    }

    public void setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;
    }

    public double getLineHeight() {
        return lineHeight;
    }

    protected void setLines(List<TextLine> lines) {
        this.lines = lines;
    }

    public List<TextLine> getLines() {
        return lines;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        getBounds().ifPresent(bounds -> {
            context.enableScissor((int) bounds.left(), (int) bounds.top(), (int) bounds.right(), (int) bounds.bottom());
            context.getMatrices().push();
            context.getMatrices().translate(0,0,getZ());
            MinecraftClient client = MinecraftClient.getInstance();
            double y = (getRegion().get().getTop() + getAlignOffsetY());
            double minY = bounds.top() - getLineHeight();
            int x = (int) (getRegion().get().getLeft() + getAlignOffsetX());
            for (TextLine line : getLines()) {
                if (y >= minY) {
                    context.drawTextWithShadow(client.textRenderer, line.text(), x, (int) y, getColor());
                }
                y += getLineHeight();
                if (y > bounds.bottom()) break;
            }
            context.getMatrices().pop();
            context.disableScissor();
        });
    }
}
