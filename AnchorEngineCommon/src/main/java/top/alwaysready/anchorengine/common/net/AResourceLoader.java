package top.alwaysready.anchorengine.common.net;

import java.io.File;

public interface AResourceLoader {

    void loadResource(String key);
    void loadFile(File file);
    void onFail();
}
