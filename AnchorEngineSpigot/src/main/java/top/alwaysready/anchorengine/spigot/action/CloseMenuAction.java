package top.alwaysready.anchorengine.spigot.action;

import top.alwaysready.anchorengine.common.action.Action;
import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;

import java.util.UUID;

public class CloseMenuAction implements Action {
    @Override
    public void execute(ActionInfo info, UUID playerId) {
        JsonPacketUtils.S2C.setScreen(playerId,null);
    }
}
