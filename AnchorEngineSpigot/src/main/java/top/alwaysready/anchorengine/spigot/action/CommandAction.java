package top.alwaysready.anchorengine.spigot.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import top.alwaysready.anchorengine.common.action.Action;
import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.string.StringReplacer;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.AnchorEngineSpigot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandAction implements Action {
    private List<String> commands;
    private List<String> perms;

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getCommands() {
        if(commands == null) commands = new ArrayList<>();
        return commands;
    }

    public void setPerms(List<String> perms) {
        this.perms = perms;
    }

    public List<String> getPerms() {
        if(perms == null) perms = new ArrayList<>();
        return perms;
    }

    @Override
    public void execute(ActionInfo info, UUID playerId) {
        List<String> cmd = getCommands();
        if(cmd.isEmpty()) return;
        Player p = Bukkit.getPlayer(playerId);
        if(p==null) return;
        AnchorUtils.getService(AnchorEngineSpigot.class).ifPresent(plugin -> {
            StringReplacer replacer = info.getReplacer(plugin.getPlayerReplacer(p));
            List<String> perms = replacer.applyList(getPerms());
            List<String> commands = replacer.applyList(cmd);
            AnchorUtils.getService(ScheduleService.class).ifPresent(sch->sch.schedule(()->{
                PermissionAttachment attachment = p.addAttachment(plugin);
                perms.forEach(perm -> attachment.setPermission(perm,true));
                commands.forEach(line -> Bukkit.dispatchCommand(p,line));
                p.removeAttachment(attachment);
            }));
        });
    }
}
