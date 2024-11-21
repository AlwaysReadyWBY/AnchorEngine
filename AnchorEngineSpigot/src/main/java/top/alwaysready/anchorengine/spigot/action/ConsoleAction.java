package top.alwaysready.anchorengine.spigot.action;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import top.alwaysready.anchorengine.common.action.Action;
import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.string.StringReplacer;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.AnchorEngineSpigot;
import top.alwaysready.readycore.ReadyCore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConsoleAction implements Action {
    private List<String> commands;

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getCommands() {
        if(commands == null) commands = new ArrayList<>();
        return commands;
    }
    @Override
    public void execute(ActionInfo info, UUID playerId) {
        List<String> cmd = getCommands();
        if(cmd.isEmpty()) return;
        Player p = Bukkit.getPlayer(playerId);
        if(p==null) return;
        AnchorUtils.getService(AnchorEngineSpigot.class).ifPresent(plugin -> {
            StringReplacer replacer = info.getReplacer(plugin.getPlayerReplacer(p));
            List<String> commands = replacer.applyList(cmd);
            AnchorUtils.getService(ScheduleService.class).ifPresent(sch-> sch.schedule(()->{
                ConsoleCommandSender sender = ReadyCore.getInstance().getSilentConsoleSender();
                commands.forEach(line -> Bukkit.dispatchCommand(sender,line));
            }));
        });
    }
}
