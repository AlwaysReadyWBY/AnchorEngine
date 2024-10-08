package top.alwaysready.anchorengine.spigot;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.server.ServerChannelHandler;
import top.alwaysready.anchorengine.common.server.ServerChannelManager;
import top.alwaysready.anchorengine.common.service.FileService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.readycore.ReadyCore;
import top.alwaysready.readycore.command.ReadyCommandTree;
import top.alwaysready.readycore.command.SimpleCommand;

import java.io.File;

public class CommandRoot extends ReadyCommandTree {
    public CommandRoot() {
        super("");
        addChild(new SimpleCommand("%help.command.push%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.push")) return;
                if(index >= args.length) {
                    getConfig().info(sender,"%info.insufficient-arg%");
                    showHelp(sender);
                    return;
                }
                String path = "push/"+args[index];
                AnchorUtils.getService(FileService.class).ifPresent(fsv->{
                    Player player;
                    if(index+1 >= args.length){
                        if(!checkPlayer(sender)) return;
                        player = (Player) sender;
                    } else {
                        player = ReadyCore.getInstance().getPlugin().getServer().getPlayer(args[index+1]);
                        if(player == null){
                            CommandRoot.this.getConfig().info(sender,"%info.player-not-found%",args[index+1]);
                            return;
                        }
                    }
                    File file = fsv.getFile(path);
                    if(!file.exists()){
                        getConfig().info(sender,"%info.file-not-found%",file.getAbsolutePath());
                        return;
                    }
                    AnchorUtils.getService(ServerChannelManager.class)
                            .map(chMan -> chMan.getHandler(player.getUniqueId()))
                            .map(ServerChannelHandler::getControlChannel)
                            .ifPresent(channel -> channel.sendPush(file));
                });
            }
        },"push");
        addChild(new SimpleCommand("%help.command.show%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.show")) return;
                if(index >= args.length) {
                    getConfig().info(sender,"%info.insufficient-arg%");
                    showHelp(sender);
                    return;
                }
                String key = AnchorUtils.toKey(args[index++]);
                Player player;
                if(index >= args.length) {
                    if(!checkPlayer(sender)) return;
                    player = (Player) sender;
                } else {
                    player = ReadyCore.getInstance().getPlugin().getServer().getPlayer(args[index+1]);
                    if(player == null){
                        CommandRoot.this.getConfig().info(sender,"%info.player-not-found%",args[index+1]);
                        return;
                    }
                }
                JsonPacketUtils.S2C.setScreen(player.getUniqueId(),key);
            }
        },"show");
        addChild(new SimpleCommand("%help.command.close%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.show")) return;
                Player player;
                if(index >= args.length) {
                    if(!checkPlayer(sender)) return;
                    player = (Player) sender;
                } else {
                    player = ReadyCore.getInstance().getPlugin().getServer().getPlayer(args[index+1]);
                    if(player == null){
                        CommandRoot.this.getConfig().info(sender,"%info.player-not-found%",args[index+1]);
                        return;
                    }
                }
                JsonPacketUtils.S2C.setScreen(player.getUniqueId(),null);
            }
        },"close");
        addChild(new SimpleCommand("%help.command.version%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.version")) return;
                AnchorUtils.getService(AnchorEngineSpigot.class)
                        .map(JavaPlugin::getDescription)
                        .ifPresent(desc ->{
                    getConfig().info(sender,"%info.version%",desc.getName(),desc.getVersion(),
                            String.join(",",desc.getAuthors()));
                });
            }
        },"version");
        addChild(new SimpleCommand("%help.command.debug%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.admin"))return;
                if(!checkPlayer(sender)) return;
                JsonPacketUtils.S2C.setScreen(((Player)sender).getUniqueId(),"betonquest:conv");
            }
        },"debug");
        addChild(new SimpleCommand("%help.command.reload%") {
            @Override
            public void execute(CommandSender sender, String[] arg, int index) {
                if(!checkPerm(sender,"anchor.admin")) return;
                AnchorUtils.getService(AnchorEngineSpigot.class).ifPresent(AnchorEngineSpigot::reload);
                ReadyCore.getInstance().getConfig().info(sender,"%info.load%");
            }
        },"reload");
    }
}
