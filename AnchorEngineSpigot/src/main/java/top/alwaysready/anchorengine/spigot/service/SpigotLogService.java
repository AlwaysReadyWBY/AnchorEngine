package top.alwaysready.anchorengine.spigot.service;

import top.alwaysready.anchorengine.common.service.LogService;
import top.alwaysready.readycore.ReadyCore;

import java.util.logging.Level;

public class SpigotLogService implements LogService {
    @Override
    public void info(String text) {
        ReadyCore.getInstance().getConfig().info(text);
    }

    @Override
    public void warn(String text, Throwable thr) {
        ReadyCore.getInstance().getLogger().log(Level.WARNING,text,thr);
    }

    @Override
    public void debug(String text) {
        ReadyCore.getInstance().getLogger().log(Level.FINE,text);
    }
}
