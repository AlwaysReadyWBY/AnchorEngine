package top.alwaysready.anchorengine.common.action;

import java.util.UUID;

@FunctionalInterface
public interface Action {
    void execute(ActionInfo info, UUID playerId);
}
