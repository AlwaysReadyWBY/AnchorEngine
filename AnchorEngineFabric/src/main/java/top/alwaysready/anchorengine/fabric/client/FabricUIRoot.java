package top.alwaysready.anchorengine.fabric.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import top.alwaysready.anchorengine.common.client.ui.UIRoot;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.ui.element.UIElement;
import top.alwaysready.anchorengine.common.ui.element.UIElementManager;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.client.ui.AnchorScreen;
import top.alwaysready.anchorengine.fabric.client.ui.drawable.ADrawableManager;
import top.alwaysready.anchorengine.fabric.client.ui.drawable.AnchorDrawable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class FabricUIRoot implements UIRoot {

    private ResolvedBoard hudRegion;
    private AnchorDrawable<?> hudRoot;

    public void setHudRegion(ResolvedBoard hudRegion) {
        this.hudRegion = hudRegion;
    }

    public Optional<ResolvedBoard> getHudRegion() {
        return Optional.ofNullable(hudRegion);
    }

    public CompletableFuture<Boolean> update(){
        try {
            if (MinecraftClient.getInstance().currentScreen instanceof AnchorScreen screen) {
                screen.update();
            }
            if(hudRoot==null){
                AnchorUtils.getService(UIElementManager.class)
                        .flatMap(uiMan -> uiMan.getElement(UI_HUD))
                        .ifPresent(this::setHud);
            }
            if(hudRoot!=null){
                getHudRegion().ifPresent(hudRegion -> hudRoot.update(hudRegion));
            }
        }catch (Exception e){
            AnchorUtils.warn("Failed to update ui.",e);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void setScreen(UIElement elem) {
        AnchorUtils.getService(ScheduleService.class).ifPresent(sch -> {
            AnchorScreen screen = new AnchorScreen(Text.of("Custom Screen"));
            screen.setElement(elem);
            sch.executeSync(()->{
                MinecraftClient.getInstance().setScreenAndRender(screen);
            });
        });
    }

    @Override
    public void closeScreen() {
        AnchorUtils.getService(ScheduleService.class).ifPresent(sch -> {
            sch.executeSync(()-> {
                Screen screen = MinecraftClient.getInstance().currentScreen;
                if(screen instanceof AnchorScreen aScreen){
                    aScreen.closeRemote();
                } else if (screen != null){
                    screen.close();
                }
            });
        });
    }

    @Override
    public void setHud(UIElement elem) {
        hudRoot = AnchorUtils.getService(ADrawableManager.class)
                .flatMap(dMan -> dMan.newRenderer(elem))
                .orElse(null);
    }

    public Optional<AnchorDrawable<?>> getHudRoot() {
        return Optional.ofNullable(hudRoot);
    }
}
