package top.alwaysready.anchorengine.spigot.support.betonquest;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.bukkit.entity.Player;
import top.alwaysready.anchorengine.common.net.channel.AChannel;
import top.alwaysready.anchorengine.common.net.channel.AControlChannel;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.server.ServerChannelHandler;
import top.alwaysready.anchorengine.common.server.ServerChannelManager;
import top.alwaysready.anchorengine.common.string.StringParser;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.support.papi.AnchorExpansion;
import top.alwaysready.readycore.ReadyCore;
import top.alwaysready.readycore.config.ReadyConfig;

import java.util.*;

public class AnchorDefaultIO implements ConversationIO {
    private static final Map<Player, AnchorDefaultIO> IO_MAP = new WeakHashMap<>();

    public static Optional<AnchorDefaultIO> get(Player player){
        return Optional.ofNullable(IO_MAP.get(player));
    }

    public static final String ACTION_OPTION = AnchorUtils.toKey("betonquest","option");

    private final List<String> options = new ArrayList<>();
    private final Conversation conv;
    private final Player player;
    private String npcText;
    private String npcName;
    private boolean open = false;
    private boolean closedRemotely = false;

    public AnchorDefaultIO(final Conversation conv, final OnlineProfile profile){
        this.conv = conv;
        player = profile.getPlayer();
        IO_MAP.put(player,this);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void setNpcResponse(String npcName, String response) {
        this.npcName = npcName;
        npcText = response;
    }

    @Override
    public void addPlayerOption(String option) {
        options.add(option);
    }

    @Override
    public void display() {
        if (npcText == null && options.isEmpty()) {
            end();
            return;
        }
        AnchorUtils.getService(AnchorExpansion.class).ifPresent(expansion -> {
            ReadyConfig cfg = ReadyCore.getInstance().getConfig();
            conv.sendMessage(cfg.format("%support.betonquest.npc-text%",npcName,npcText));
            expansion.removeVar(getPlayer(),s -> s.startsWith("bq_option"));
            expansion.putVar(getPlayer(),"bq_npc_name",npcName);
            expansion.putVar(getPlayer(),"bq_npc_text",npcText);
            expansion.putVar(getPlayer(),"bq_option_count",String.valueOf(options.size()));
            int i=0;
            for (String option : options) {
                expansion.putVar(getPlayer(),"bq_option_"+(i++),option);
            }
        });
        if(isOpen()) {
            AnchorUtils.getService(ServerChannelManager.class)
                    .map(cm -> cm.getHandler(getPlayer().getUniqueId()))
                    .map(ServerChannelHandler::getControlChannel)
                    .ifPresent(channel -> {
                        channel.newContext();
                        registerActions(channel);
                    });
        } else {
            JsonPacketUtils.S2C.setScreen(getPlayer().getUniqueId(), "betonquest:conv", this::registerActions);
        }
        setOpen(true);
    }

    public void setPortrait(String url,String slot){
        AnchorUtils.getService(AnchorExpansion.class).ifPresent(expansion -> {
            String key = "bq_portrait_"+slot;
            if(url == null) {
                expansion.removeVar(getPlayer(),key);
            } else {
                expansion.putVar(getPlayer(), key, url);
            }
        });
    }

    private void registerActions(AControlChannel channel) {
        channel.registerAction(ACTION_OPTION, (info,pId) -> {
            info.getParam("index")
                    .flatMap(str -> StringParser.INT.parseString(str,Integer.class))
                    .ifPresent(index->{
                        if(index<0 || index>=options.size()) return;
                        print(getPlayer().getPlayerListName()+": "+options.get(index));
                        conv.passPlayerAnswer(index + 1);
                    });
        });
        channel.registerAction(JsonPacketUtils.C2S.ACTION_CLOSE,(info,pId)->{
            setClosedRemotely(true);
            end();
        });
    }

    @Override
    public void clear() {
        AnchorUtils.getService(AnchorExpansion.class).ifPresent(expansion->{
            expansion.removeVar(getPlayer(),s -> s.startsWith("bq"));
            options.clear();
            npcText = null;
        });
    }

    protected void setOpen(boolean open) {
        if(!open && isOpen()){
            if(!isClosedRemotely()) {
                JsonPacketUtils.S2C.setScreen(getPlayer().getUniqueId(), null);
            } else {
                AnchorUtils.getService(ServerChannelManager.class)
                        .map(cm -> cm.getHandler(getPlayer().getUniqueId()))
                        .map(ServerChannelHandler::getControlChannel)
                        .ifPresent(AChannel::newContext);
            }
        }
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setClosedRemotely(boolean closedRemotely) {
        this.closedRemotely = closedRemotely;
    }

    public boolean isClosedRemotely() {
        return closedRemotely;
    }

    @Override
    public void end() {
        setOpen(false);
        clear();
        conv.endConversation();
    }

    @Override
    public void print(String message) {
        if (message != null && message.length() > 0) {
            conv.sendMessage(message);
        }
    }
}
