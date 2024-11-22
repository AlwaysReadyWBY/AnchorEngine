package top.alwaysready.anchorengine.spigot.config;

import java.util.Optional;

public class LoginConfig {
    private String menu;
    private int delay = 10;

    public Optional<String> getMenu() {
        return Optional.ofNullable(menu);
    }

    public int getDelay() {
        return delay;
    }
}
