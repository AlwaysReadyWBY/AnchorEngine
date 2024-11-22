package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.ui.element.AButton;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AButtonDrawable extends AnchorDrawable<AButton> {
    private String style = AButton.Styles.MODERN;
    private ActionInfo onClick;
    private ActionInfo onRelease;
    private boolean active = true;
    private boolean lastHover = false;

    public AButtonDrawable(AButton elem) {
        super(elem);
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setOnClick(ActionInfo onClick) {
        this.onClick = onClick;
    }

    public Optional<ActionInfo> getOnClick() {
        return Optional.ofNullable(onClick);
    }

    public void setOnRelease(ActionInfo onRelease) {
        this.onRelease = onRelease;
    }

    public Optional<ActionInfo> getOnRelease() {
        return Optional.ofNullable(onRelease);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!isMouseOver(mouseX, mouseY)) {
            setFocused(false);
            return false;
        }
        playDownSound();
        getOnClick().ifPresent(JsonPacketUtils.C2S::sendAction);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(!isMouseOver(mouseX, mouseY)) return false;
        getOnRelease().ifPresent(JsonPacketUtils.C2S::sendAction);
        return true;
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
        setPreferredWidth(region.getWidth());
        setPreferredHeight(region.getHeight());
        setStyle(region.getReplacer().apply(getElement().getStyle()));
        setOnClick(getElement().getOnClick().map(info->info.getReplaced(region.getReplacer())).orElse(null));
        setOnRelease(getElement().getOnRelease().map(info->info.getReplaced(region.getReplacer())).orElse(null));
    }

    @Override
    public void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        getBounds().ifPresent(bounds -> {
            boolean hovered = isMouseOver(mouseX,mouseY);
            switch (getStyle()){
                case "anchor_engine:vanilla"->{
                    context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                    int textureY = 46 + 20*(isActive()? hovered? 2:1 :0);
                    context.drawNineSlicedTexture(ClickableWidget.WIDGETS_TEXTURE,
                            (int) bounds.left(),
                            (int) bounds.top(),
                            (int) bounds.width(),
                            (int) bounds.height(),
                            20,
                            4,
                            200,
                            20,
                            0,
                            textureY);
                    RenderSystem.disableBlend();
                    RenderSystem.disableDepthTest();
                }
                case "anchor_engine:invisible"->{}
                //Modern style by default
                default-> {
                    context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.enableBlend();
                    context.fill((int) bounds.left(),
                            (int) bounds.top(),
                            (int) bounds.right(),
                            (int) bounds.bottom(),
                            hovered ? 0x77ffffff : 0x77000000);
                    RenderSystem.disableBlend();
                }
            }
            if(hovered && !lastHover){
                playHoverSound();
            }
            lastHover = hovered;
        });
    }

    public void playHoverSound(){
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_TOAST_OUT,2.0f));
    }

    public void playDownSound(){
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK,1.0f));
    }
}
