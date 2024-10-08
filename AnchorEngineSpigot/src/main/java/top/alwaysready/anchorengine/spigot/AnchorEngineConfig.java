package top.alwaysready.anchorengine.spigot;

import top.alwaysready.readycore.config.ReadyConfig;

import java.util.ArrayList;
import java.util.List;

public class AnchorEngineConfig extends ReadyConfig {
    private List<String> autoPush = new ArrayList<>();

    public AnchorEngineConfig(AnchorEngineSpigot plugin) {
        super(plugin);
    }

    @Override
    public void saveResources(boolean replace) {
        super.saveResources(replace);
        getPlugin().saveResource("push/default.json",replace);
    }

    public List<String> getAutoPush() {
        return autoPush;
    }
}
