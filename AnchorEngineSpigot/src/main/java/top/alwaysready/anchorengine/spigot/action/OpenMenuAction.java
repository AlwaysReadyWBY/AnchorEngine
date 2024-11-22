package top.alwaysready.anchorengine.spigot.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import top.alwaysready.anchorengine.common.action.Action;
import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.config.AnchorEngineConfig;
import top.alwaysready.anchorengine.spigot.AnchorEngineSpigot;

import java.util.UUID;

public class OpenMenuAction implements Action {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public void execute(ActionInfo info, UUID playerId) {
        Player p = Bukkit.getPlayer(playerId);
        if(p==null) return;
        AnchorUtils.getService(AnchorEngineSpigot.class).ifPresent(plugin -> {
            String menuId = info.getReplacer(plugin.getPlayerReplacer(p)).apply(id);
            AnchorUtils.getService(AnchorEngineConfig.class).flatMap(cfg -> cfg.getMenu(menuId))
                    .ifPresent(menu -> menu.open(playerId));
        });
    }
}
