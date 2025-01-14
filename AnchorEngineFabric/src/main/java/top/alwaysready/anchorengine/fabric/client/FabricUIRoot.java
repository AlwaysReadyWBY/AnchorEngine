package top.alwaysready.anchorengine.fabric.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import top.alwaysready.anchorengine.common.client.ui.UIRoot;
import top.alwaysready.anchorengine.common.net.packet.json.OverlayInfo;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.ui.element.UIElement;
import top.alwaysready.anchorengine.common.ui.element.UIElementManager;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.client.ui.AnchorScreen;
import top.alwaysready.anchorengine.fabric.client.ui.drawable.ADrawableManager;
import top.alwaysready.anchorengine.fabric.client.ui.drawable.AnchorDrawable;
import top.alwaysready.anchorengine.fabric.client.ui.drawable.OverlayDrawable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class FabricUIRoot implements UIRoot {

    private ResolvedBoard hudRegion;
    private AnchorDrawable<?> hudRoot;
    private final Map<String, OverlayDrawable> overlayMap = new ConcurrentHashMap<>();

    public void setHudRegion(ResolvedBoard hudRegion) {
        this.hudRegion = hudRegion;
    }

    public Optional<ResolvedBoard> getHudRegion() {
        return Optional.ofNullable(hudRegion);
    }

    public CompletableFuture<Boolean> update() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.currentScreen instanceof AnchorScreen screen) {
                screen.update();
            }
            if (hudRoot == null) {
                AnchorUtils.getService(UIElementManager.class)
                        .flatMap(uiMan -> uiMan.getElement(UI_HUD))
                        .ifPresent(this::setHud);
            }
            getHudRegion().ifPresent(hudRegion -> {
                if (hudRoot != null) {
                    hudRoot.update(hudRegion);
                }
                getOverlays().forEach(overlay -> overlay.update(hudRegion));
            });
        } catch (Exception e) {
            AnchorUtils.warn("Failed to update ui.", e);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void setScreen(UIElement elem) {
        AnchorUtils.getService(ScheduleService.class).ifPresent(sch -> {
            AnchorScreen screen = new AnchorScreen(Text.of("Custom Screen"));
            screen.setElement(elem);
            sch.executeSync(() -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.currentScreen instanceof AnchorScreen prev) {
                    prev.setClosedRemotely();
                }
                client.setScreenAndRender(screen);
            });
        });
    }

    @Override
    public void closeScreen() {
        AnchorUtils.getService(ScheduleService.class).ifPresent(sch -> {
            sch.executeSync(() -> {
                Screen screen = MinecraftClient.getInstance().currentScreen;
                if (screen instanceof AnchorScreen aScreen) {
                    aScreen.closeRemote();
                }
            });
        });
    }

    @Override
    public void setHud(UIElement elem) {
        if (elem == null) {
            hudRoot = null;
            return;
        }
        hudRoot = AnchorUtils.getService(ADrawableManager.class)
                .flatMap(dMan -> dMan.newRenderer(elem))
                .orElse(null);
    }

    public Optional<AnchorDrawable<?>> getHudRoot() {
        return Optional.ofNullable(hudRoot);
    }

    @Override
    public void addOverlay(OverlayInfo info, UIElement elem) {
        if (info == null || info.getId()==null || elem == null) return;
        AnchorUtils.getService(ADrawableManager.class)
                .flatMap(dMan -> dMan.newRenderer(elem))
                .map(drawable -> new OverlayDrawable(drawable, info))
                .ifPresent(drawable -> {
                    synchronized (overlayMap) {
                        overlayMap.put(AnchorUtils.toKey(info.getId()), drawable);
                    }
                });
    }

    public Stream<OverlayDrawable> getOverlays() {
        synchronized (overlayMap) {
            Collection<OverlayDrawable> overlays = overlayMap.values();
            long time = System.currentTimeMillis();
            overlays.removeIf(drawable -> time >= drawable.getEndMillis());
            return overlays.stream();
        }
    }
}
