package top.alwaysready.anchorengine.fabric.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import top.alwaysready.anchorengine.common.service.ExecutorScheduleService;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

public class AnchorEngineFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        AnchorUtils.registerService(ScheduleService.class, new ExecutorScheduleService(2) {
            @Override
            public void executeSync(Runnable run) {
                run.run();
            }
        });
    }
}
