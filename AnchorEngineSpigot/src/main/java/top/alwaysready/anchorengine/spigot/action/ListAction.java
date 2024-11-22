package top.alwaysready.anchorengine.spigot.action;

import top.alwaysready.anchorengine.common.action.Action;
import top.alwaysready.anchorengine.common.action.ActionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListAction implements Action {
    private List<Action> actions;

    public List<Action> getActions() {
        if(actions == null) actions = new ArrayList<>();
        return actions;
    }

    @Override
    public void execute(ActionInfo info, UUID playerId) {
        getActions().forEach(action -> action.execute(info,playerId));
    }
}
