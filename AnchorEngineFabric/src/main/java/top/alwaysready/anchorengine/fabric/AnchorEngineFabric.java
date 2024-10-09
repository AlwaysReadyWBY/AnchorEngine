package top.alwaysready.anchorengine.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.service.LogService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

public class AnchorEngineFabric implements ModInitializer {

    public static final Identifier CONTROL_CHANNEL = Identifier.tryParse(AControlChannel.CONTROL);

    @Override
    public void onInitialize() {
        AnchorUtils.registerService(LogService.class,new FabricLogService());
    }
}
