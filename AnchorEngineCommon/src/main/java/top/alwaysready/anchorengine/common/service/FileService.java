package top.alwaysready.anchorengine.common.service;

import java.io.File;

@FunctionalInterface
public interface FileService {
    File getFile(String path);
}
