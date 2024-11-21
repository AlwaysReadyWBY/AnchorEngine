package top.alwaysready.anchorengine.spigot.support.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import top.alwaysready.anchorengine.common.string.StringReplacer;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class AnchorExpansion extends PlaceholderExpansion {
    private final Map<Player, Map<String,String>> valueMap = new WeakHashMap<>();

    @Override
    public String getIdentifier() {
        return "anchor";
    }

    @Override
    public String getAuthor() {
        return "AlwaysReadyWby";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    public Map<String,String> getValueMap(Player p){
        return valueMap.computeIfAbsent(p,any -> new ConcurrentHashMap<>());
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        return getValueMap(player).getOrDefault(params,"...");
    }

    public void removeVar(Player player, Predicate<String> removeIf) {
        getValueMap(player).keySet().removeIf(removeIf);
    }

    public void removeVar(Player player,String key){
        getValueMap(player).remove(key);
    }

    public void putVar(Player player, String key, String value) {
        getValueMap(player).put(key,value);
    }
}
