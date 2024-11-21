package top.alwaysready.anchorengine.spigot.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import top.alwaysready.anchorengine.common.string.StringReplacer;

import java.util.Optional;

public class SpigotPlayerReplacer extends StringReplacer {
    private final Player player;

    public SpigotPlayerReplacer(Player player) {
        this.player = player;
    }

    @Override
    protected Optional<String> getMapped(String from) {
        Optional<String> ret = super.getMapped(from);
        if(ret.isPresent()) return ret;
        return Optional.of(PlaceholderAPI.setPlaceholders(player, "%" + from + "%"));
    }
}
