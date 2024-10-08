package top.alwaysready.anchorengine.fabric;

import net.fabricmc.api.ModInitializer;
import top.alwaysready.anchorengine.common.service.LogService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.net.PacketPayload;

public class AnchorEngineFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        AnchorUtils.registerService(LogService.class,new FabricLogService());
        PacketPayload.register();
    }
}
