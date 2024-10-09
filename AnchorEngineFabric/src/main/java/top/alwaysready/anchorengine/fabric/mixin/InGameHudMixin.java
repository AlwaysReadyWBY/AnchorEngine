package top.alwaysready.anchorengine.fabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.alwaysready.anchorengine.common.client.ClientVarManager;
import top.alwaysready.anchorengine.common.client.ui.UIRoot;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.client.FabricUIRoot;

@Mixin(InGameHud.class)
@Environment(EnvType.CLIENT)
public class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render",at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;)V"))
    private void renderCustomHud(DrawContext context,float tickDelta, CallbackInfo ci){
        AnchorUtils.getService(UIRoot.class)
                .filter(FabricUIRoot.class::isInstance)
                .map(FabricUIRoot.class::cast)
                .ifPresent(ui -> {
                    AnchorUtils.getService(ClientVarManager.class).ifPresent(cvm -> {
                        ui.setHudRegion(new ResolvedBoard(context.getScaledWindowWidth(),context.getScaledWindowHeight(),cvm.createChild()));
                        if(!client.options.hudHidden) {
                            ui.getHudRoot().ifPresent(hudRoot -> hudRoot.render(context, 0, 0, tickDelta));
                        }
                    });
                });
    }
}
