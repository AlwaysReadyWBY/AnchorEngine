package top.alwaysready.anchorengine.fabric.client.net;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.alwaysready.anchorengine.common.client.ClientVarManager;
import top.alwaysready.anchorengine.common.client.ui.UIRoot;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacket;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketTypes;
import top.alwaysready.anchorengine.common.net.packet.json.Push;
import top.alwaysready.anchorengine.common.ui.element.UIElementManager;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

@Environment(EnvType.CLIENT)
public class ClientControlChannel extends AControlChannel {
    public ClientControlChannel() {
        super(false);
        registerListener(JsonPacketTypes.S2C.PUSH,this::handlePush);
        registerListener(JsonPacketTypes.S2C.DEBUG,this::handleDebug);
        registerListener(JsonPacketTypes.S2C.SET_SCREEN,this::handleSetScreen);
        registerListener(JsonPacketTypes.S2C.CLOSE_SCREEN,this::handleCloseScreen);
    }

    private boolean handleCloseScreen(JsonPacket packet) {
        AnchorUtils.getService(UIRoot.class).ifPresent(UIRoot::closeScreen);
        return true;
    }

    private boolean handleSetScreen(JsonPacket packet) {
        AnchorUtils.getService(UIRoot.class).ifPresent(uiRoot -> {
            packet.read(String.class).ifPresent(uiRoot::setScreen);
        });
        return true;
    }

    private boolean handleDebug(JsonPacket jsonPacket) {
        AnchorUtils.info("Received debug message!");
        return true;
    }

    private boolean handlePush(JsonPacket packet) {
        packet.read(Push.class).ifPresent(push -> {
            AnchorUtils.getService(UIElementManager.class).ifPresent(uiMan ->
                    push.getUIMap().forEach(uiMan::registerElement));
            AnchorUtils.getService(ClientVarManager.class).ifPresent(cvm ->
                    push.getVarMap().forEach(cvm::map));
        });
        return true;
    }
}
