package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import top.alwaysready.anchorengine.common.ui.element.ASlot;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;

@Environment(EnvType.CLIENT)
public class ASlotDrawable extends AnchorDrawable<ASlot> {
    public ASlotDrawable(ASlot elem) {
        super(elem);
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
    }

    @Override
    protected void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {

    }
}
