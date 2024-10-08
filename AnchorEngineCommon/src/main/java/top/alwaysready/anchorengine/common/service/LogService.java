package top.alwaysready.anchorengine.common.service;

public interface LogService {
    void info(String text);
    void warn(String text,Throwable thr);

    void debug(String text);
}
