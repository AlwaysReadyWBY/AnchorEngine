package top.alwaysready.anchorengine.spigot.service;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SpigotScheduleService implements ScheduleService {
    private final JavaPlugin plugin;

    public SpigotScheduleService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeSync(Runnable run) {
        plugin.getServer().getScheduler().runTask(plugin,run);
    }

    @Override
    public void scheduleAsync(Runnable run) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin,run);
    }

    @Override
    public void scheduleAsync(Runnable run, long delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin,run,delay/50);
    }

    @Override
    public void loopAsync(Supplier<CompletableFuture<Boolean>> run, long interval) {
        interval /= 50;
        new BukkitRunnable() {
            @Override
            public void run() {
                run.get().thenAccept(keepALive -> {
                    if(!keepALive) cancel();
                });
            }
        }.runTaskLaterAsynchronously(plugin,interval);
    }
}
