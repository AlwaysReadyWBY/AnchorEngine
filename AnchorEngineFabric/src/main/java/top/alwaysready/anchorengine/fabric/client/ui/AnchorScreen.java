package top.alwaysready.anchorengine.fabric.client.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import top.alwaysready.anchorengine.common.client.ClientVarManager;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.ui.element.UIElement;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.client.ui.drawable.AnchorDrawable;
import top.alwaysready.anchorengine.fabric.client.ui.drawable.ADrawableManager;

@Environment(EnvType.CLIENT)
public class AnchorScreen extends Screen {
    private ResolvedBoard region;
    private AnchorDrawable<?> anchorDrawable;
    private Screen then;
    private boolean closedRemotely = false;

    public AnchorScreen(Text title) {
        super(title);
        then = MinecraftClient.getInstance().currentScreen;
    }

    public ResolvedBoard getRegion() {
        return region;
    }

    public void setElement(UIElement elem) {
        AnchorUtils.getService(ADrawableManager.class)
                .flatMap(sv -> sv.newRenderer(elem))
                .ifPresent(this::setAnchorDrawable);
    }

    public void setAnchorDrawable(AnchorDrawable<?> anchorDrawable) {
        this.anchorDrawable = anchorDrawable;
        clearChildren();
        addDrawableChild(anchorDrawable);
    }

    public AnchorDrawable<?> getAnchorDrawable() {
        return anchorDrawable;
    }

    public void update(){
        AnchorUtils.getService(ClientVarManager.class).ifPresent(cvm ->
                region = new ResolvedBoard(width, height, cvm.createChild()));
        anchorDrawable = getAnchorDrawable();
        if (anchorDrawable == null) return;
        anchorDrawable.update(getRegion());
    }

    public void setThen(Screen then) {
        this.then = then;
    }

    @Override
    protected void init() {
        update();
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(then);
    }

    @Override
    public void removed() {
        super.removed();
        sendClose();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(super.keyPressed(keyCode, scanCode, modifiers)) return true;
        AnchorDrawable<?> drawable = getAnchorDrawable();
        return drawable!=null && drawable.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(super.keyReleased(keyCode, scanCode, modifiers)) return true;
        AnchorDrawable<?> drawable = getAnchorDrawable();
        return drawable!=null && drawable.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(super.charTyped(chr,modifiers)) return true;
        AnchorDrawable<?> drawable = getAnchorDrawable();
        return drawable!=null && drawable.charTyped(chr, modifiers);
    }

    public boolean isClosedRemotely() {
        return closedRemotely;
    }

    public void closeRemote() {
        setClosedRemotely();
        close();
    }

    public void sendClose(){
        if(!isClosedRemotely()){
            JsonPacketUtils.C2S.sendClose();
        }
    }

    public void setClosedRemotely() {
        closedRemotely = true;
    }
}
