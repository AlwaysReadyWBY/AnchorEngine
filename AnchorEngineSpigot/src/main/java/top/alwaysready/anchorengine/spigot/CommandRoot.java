package top.alwaysready.anchorengine.spigot;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.server.ServerChannelHandler;
import top.alwaysready.anchorengine.common.server.ServerChannelManager;
import top.alwaysready.anchorengine.common.service.FileService;
import top.alwaysready.anchorengine.common.serialization.StringParser;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.api.AnchorAPI;
import top.alwaysready.anchorengine.spigot.reflection.ReflectionUtils;
import top.alwaysready.readycore.ReadyCore;
import top.alwaysready.readycore.command.ReadyCommandTree;
import top.alwaysready.readycore.command.SimpleCommand;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

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
        addChild(new SimpleCommand("%help.command.menu%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.menu.base")) return;
                if(index >= args.length) {
                    getConfig().info(sender,"%info.insufficient-arg%");
                    showHelp(sender);
                    return;
                }
                String key = args[index++];
                Player player;
                if(index >= args.length) {
                    if(!checkPlayer(sender)) return;
                    player = (Player) sender;
                } else {
                    if(!checkPerm(sender,"anchor.menu.others")) return;
                    player = ReadyCore.getInstance().getPlugin().getServer().getPlayer(args[index+1]);
                    if(player == null){
                        getConfig().info(sender,"%info.player-not-found%",args[index+1]);
                        return;
                    }
                }
                AnchorUtils.getService(AnchorAPI.class).ifPresent(api ->
                        api.sendMenu(player,key,ch->{}));
            }
        },"menu");
        addChild(new SimpleCommand("%help.command.overlay%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.overlay.base")) return;
                if(index >= args.length) {
                    getConfig().info(sender,"%info.insufficient-arg%");
                    showHelp(sender);
                    return;
                }
                String key = args[index++];
                Player player;
                if(index >= args.length) {
                    if(!checkPlayer(sender)) return;
                    player = (Player) sender;
                } else {
                    if(!checkPerm(sender,"anchor.overlay.others")) return;
                    player = ReadyCore.getInstance().getPlugin().getServer().getPlayer(args[index+1]);
                    if(player == null){
                        getConfig().info(sender,"%info.player-not-found%",args[index+1]);
                        return;
                    }
                }
                AnchorUtils.getService(AnchorAPI.class).ifPresent(api ->
                        api.sendOverlay(Collections.singleton(player),null,key, map->{}));
            }
        },"overlay");
        addChild(new SimpleCommand("%help.command.danmaku%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.danmaku")) return;
                if(index >= args.length) {
                    getConfig().info(sender,"%info.insufficient-arg%");
                    showHelp(sender);
                    return;
                }
                long millis = StringParser.LONG.parseString(args[index++], Long.class).orElse(10000L);
                String text = ((sender instanceof Player player)? player.getDisplayName()+": ":"")
                        +(index>=args.length? "":String.join(" ", Arrays.copyOfRange(args,index,args.length)));
                double y = Math.random()*0.6;
                AnchorUtils.getService(AnchorAPI.class).ifPresent(api ->
                        api.sendDanmaku(Bukkit.getOnlinePlayers(),text,y,millis));
            }
        },"danmaku");
        addChild(new SimpleCommand("%help.command.show%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.show.base")) return;
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
                    if(!checkPerm(sender,"anchor.show.others")) return;
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
                if(!checkPerm(sender,"anchor.close.base")) return;
                Player player;
                if(index >= args.length) {
                    if(!checkPlayer(sender)) return;
                    player = (Player) sender;
                } else {
                    if(!checkPerm(sender,"anchor.close.others")) return;
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
        addChild(new SimpleCommand("%help.command.item%") {
            @Override
            public void execute(CommandSender sender, String[] args, int index) {
                if(!checkPerm(sender,"anchor.admin")) return;
                if(!checkPlayer(sender)) return;
                AnchorUtils.getService(ReflectionUtils.class)
                        .map(ReflectionUtils::getSerializer)
                        .flatMap(serializer -> serializer.encodeItem(((Player)sender).getInventory().getItemInMainHand()))
                        .map(AnchorEngine.getInstance().getConfigGson()::toJson)
                        .ifPresent(json -> getConfig().sendActionText((Player)sender,
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT,TextComponent.fromLegacyText(json)),
                                new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,json),
                                "%info.click-to-copy%"));
            }
        },"item");
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
