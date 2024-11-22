package top.alwaysready.anchorengine.spigot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.action.ActionManager;
import top.alwaysready.anchorengine.common.server.ServerChannelManager;
import top.alwaysready.anchorengine.common.service.FileService;
import top.alwaysready.anchorengine.common.service.LogService;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.action.*;
import top.alwaysready.anchorengine.spigot.listener.PlayerListener;
import top.alwaysready.anchorengine.spigot.net.SpigotChannelManager;
import top.alwaysready.anchorengine.spigot.service.SpigotLogService;
import top.alwaysready.anchorengine.spigot.service.SpigotScheduleService;
import top.alwaysready.anchorengine.spigot.support.betonquest.BQSupport;
import top.alwaysready.anchorengine.spigot.support.papi.AnchorExpansion;
import top.alwaysready.anchorengine.spigot.util.SpigotPlayerReplacer;
import top.alwaysready.readycore.ReadyCore;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class AnchorEngineSpigot extends JavaPlugin {

    public static final int CONFIG_VERSION = 1;

    private CommandRoot cmdRoot;
    private Map<Player, SpigotPlayerReplacer> playerReplacerMap;

    @Override
    public void onEnable() {
        ReadyCore.init(this,new AnchorEngineConfig(this),CONFIG_VERSION);
        AnchorUtils.registerService(AnchorEngineSpigot.class,this);
        AnchorUtils.registerService(LogService.class,new SpigotLogService());
        AnchorUtils.registerService(ScheduleService.class,new SpigotScheduleService(this));
        Path dataFolder = getDataFolder().toPath();
        AnchorUtils.registerService(FileService.class, path -> dataFolder.resolve(path).toFile());
        AnchorUtils.registerService(AnchorEngineConfig.class,ReadyCore.getInstance().getConfig());
        AnchorExpansion expansion = new AnchorExpansion();
        expansion.register();
        AnchorUtils.registerService(AnchorExpansion.class,expansion);
        cmdRoot = new CommandRoot();
        AnchorUtils.registerService(ServerChannelManager.class,new SpigotChannelManager(this));

        if(getServer().getPluginManager().isPluginEnabled("BetonQuest")){
            BQSupport.init();
        }

        registerActions();
        registerListeners();

        AnchorUtils.getService(AnchorEngineConfig.class).ifPresent(AnchorEngineConfig::loadMenu);
    }

    private void registerActions(){
        AnchorUtils.getService(ActionManager.class).ifPresent(actMan -> {
            actMan.registerType(AnchorUtils.toKey("open"), OpenMenuAction.class);
            actMan.registerType(AnchorUtils.toKey("close"), CloseMenuAction.class);
            actMan.registerType(AnchorUtils.toKey("command"), CommandAction.class);
            actMan.registerType(AnchorUtils.toKey("console"), ConsoleAction.class);
            actMan.registerType(AnchorUtils.toKey("list"), ListAction.class);
        });
    }

    private void registerListeners(){
        PluginManager plugMan = getServer().getPluginManager();
        registerListener(PlayerListener.class,new PlayerListener(),plugMan);
    }

    private <T extends Listener> void registerListener(Class<T> type,T listener,PluginManager plugMan){
        plugMan.registerEvents(listener,this);
        AnchorUtils.registerService(type,listener);
    }

    public void reload() {
        ReadyCore.getInstance().getConfig().load(CONFIG_VERSION);
        AnchorUtils.getService(AnchorEngineConfig.class).ifPresent(AnchorEngineConfig::loadMenu);
        ReadyCore.getInstance().getConfig().info("%info.load%");
    }

    public Map<Player, SpigotPlayerReplacer> getPlayerReplacerMap() {
        if(playerReplacerMap==null) playerReplacerMap = new WeakHashMap<>();
        return playerReplacerMap;
    }

    public SpigotPlayerReplacer getPlayerReplacer(Player player){
        return getPlayerReplacerMap().computeIfAbsent(player,SpigotPlayerReplacer::new);
    }

    @Override
    public void onDisable() {
        ReadyCore.unload();
        AnchorEngine.unload();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(cmdRoot == null) return false;
        cmdRoot.execute(sender,args,0);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(cmdRoot == null) return Collections.emptyList();
        return cmdRoot.tabComplete(sender,args,0);
    }
}
