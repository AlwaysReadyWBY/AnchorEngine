package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import top.alwaysready.anchorengine.common.ui.element.AItem;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;

import java.util.Optional;

public class AItemDrawable extends AnchorDrawable<AItem> {
    private ItemStack stack;
    private boolean iconOnly = false;
    private float scale = 16f;

    protected AItemDrawable(AItem elem) {
        super(elem);
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public Optional<ItemStack> getStack() {
        return Optional.ofNullable(stack);
    }

    public boolean isIconOnly() {
        return iconOnly;
    }

    public void setIconOnly(boolean iconOnly) {
        this.iconOnly = iconOnly;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
        setStack(getElement().getItem()
                .map(item -> item.getJson(region.getReplacer()))
                .map(json -> ItemStack.CODEC.parse(JsonOps.INSTANCE,json))
                .flatMap(DataResult::result)
                .orElse(null));
        setIconOnly(region.getReplacer().getAsInt(getElement().getIconOnly()).orElse(0)!=0);
        setScale(isHWrap() || isVWrap()?
                16
                :Math.max(8,(float) Math.min(region.getWidth(),region.getHeight())));
        setPreferredWidth(getScale());
        setPreferredHeight(getScale());
    }

    @Override
    protected void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        getBounds().ifPresent(bounds -> {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.enableScissor((int) bounds.left(), (int) bounds.top(), (int) bounds.right(), (int) bounds.bottom());
            getStack().ifPresent(stack -> {
                int startX = (int)(bounds.left()+getAlignOffsetX());
                int startY = (int)(bounds.top()+getAlignOffsetY());
                float scale = getScale();
                drawItem(context,startX,startY,scale);
            });
            context.disableScissor();
            context.enableScissor(0,0,context.getScaledWindowWidth(),context.getScaledWindowHeight());
            if(isMouseOver(mouseX,mouseY)){
                context.drawItemTooltip(textRenderer,stack,mouseX,mouseY);
            }
            context.disableScissor();
        });
    }

    public void drawItem(DrawContext context,float x,float y,float scale){
        if (!stack.isEmpty()) {
            MinecraftClient client = MinecraftClient.getInstance();
            BakedModel bakedModel = client.getItemRenderer().getModel(stack, client.world, null, 0);
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.translate(x + scale*0.5, y + scale*0.5, getZ());

            try {
                matrices.multiplyPositionMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
                matrices.scale(scale,scale,scale);
                boolean bl = !bakedModel.isSideLit();
                if (bl) {
                    DiffuseLighting.disableGuiDepthLighting();
                }
                client.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, matrices, context.getVertexConsumers(), 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
                context.draw();
                if (bl) {
                    DiffuseLighting.enableGuiDepthLighting();
                }
            } catch (Throwable var12) {
                CrashReport crashReport = CrashReport.create(var12, "Rendering item");
                CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
                crashReportSection.add("Item Damage", () -> String.valueOf(stack.getDamage()));
                crashReportSection.add("Item NBT", () -> String.valueOf(stack.getNbt()));
                crashReportSection.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
                throw new CrashException(crashReport);
            }
            matrices.pop();
            TextRenderer textRenderer = client.textRenderer;
            if(!isIconOnly()){
                matrices.push();
                if (stack.getCount() != 1) {
                    String string = String.valueOf(stack.getCount());
                    matrices.translate(0.0F, 0.0F, getZ()+1);
                    context.drawText(textRenderer, string, (int) (x + scale + 1 - textRenderer.getWidth(string)), (int) (y + scale -7), 16777215, true);
                }

                int left;
                int top;
                if (stack.isItemBarVisible()) {
                    int barStep = stack.getItemBarStep();
                    int barColor = stack.getItemBarColor();
                    left = (int) (x + 2);
                    top = (int) (y + scale - 3);
                    context.fill(RenderLayer.getGuiOverlay(), left, top, (int) (left + scale -3), top + 2, getZ()-1,-16777216);
                    context.fill(RenderLayer.getGuiOverlay(), left, top, left + barStep, top + 1, getZ()-1,barColor | -16777216);
                }
                int bottom;
                ClientPlayerEntity clientPlayerEntity = client.player;
                float cdProgress = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), client.getTickDelta());
                if (cdProgress > 0.0F) {
                    top = (int) (y + MathHelper.floor(scale * (1.0F - cdProgress)));
                    bottom = top + MathHelper.ceil(scale * cdProgress);
                    context.fill(RenderLayer.getGuiOverlay(), (int) x, top, (int) (x + scale), bottom, getZ()-1,Integer.MAX_VALUE);
                }

                matrices.pop();
            }
        }
    }
}
