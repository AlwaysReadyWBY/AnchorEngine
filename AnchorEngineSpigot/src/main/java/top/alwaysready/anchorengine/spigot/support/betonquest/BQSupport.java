package top.alwaysready.anchorengine.spigot.support.betonquest;

import org.betonquest.betonquest.BetonQuest;

public interface BQSupport {

    static void init(){
        BetonQuest.getInstance().registerConversationIO("AnchorDefault", AnchorDefaultIO.class);
        BetonQuest.getInstance().getQuestRegistries().getEventTypes().register("aportrait",BQPortraitEvent::parse);
    }
}
