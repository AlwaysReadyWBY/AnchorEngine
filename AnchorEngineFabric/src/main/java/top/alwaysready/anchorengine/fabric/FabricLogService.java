package top.alwaysready.anchorengine.fabric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.alwaysready.anchorengine.common.service.LogService;

public class FabricLogService implements LogService {
    private final Logger LOGGER = LoggerFactory.getLogger("AnchorEngine");

    @Override
    public void info(String text) {
        LOGGER.info(text);
    }

    @Override
    public void warn(String text, Throwable thr) {
        LOGGER.warn(text,thr);
    }

    @Override
    public void debug(String text) {
        LOGGER.debug(text);
    }
}
