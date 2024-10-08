package top.alwaysready.anchorengine.common.service;

import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class ExecutorScheduleService implements ScheduleService {
    private final ScheduledExecutorService scheduler;

    public ExecutorScheduleService(int poolSize){
        scheduler = Executors.newScheduledThreadPool(poolSize);
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    @Override
    public void scheduleAsync(Runnable run) {
        scheduler.submit(run);
    }

    @Override
    public void scheduleAsync(Runnable run, long delay) {
        scheduler.schedule(run,delay,TimeUnit.MILLISECONDS);
    }

    @Override
    public void loopAsync(Supplier<CompletableFuture<Boolean>> run, long interval) {
        new ExecutorTask(scheduler,run).loop(interval);
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
    }

    public static class ExecutorTask{
        private ScheduledFuture<?> future;
        private final ScheduledExecutorService scheduler;
        private final Supplier<CompletableFuture<Boolean>> run;

        public ExecutorTask(ScheduledExecutorService scheduler, Supplier<CompletableFuture<Boolean>> run) {
            this.scheduler = scheduler;
            this.run = run;
        }

        public void loop(long interval){
            future = scheduler.scheduleAtFixedRate(()-> run.get().thenAccept(keepAlive -> {
                if(!keepAlive) future.cancel(true);
            }), interval, interval, TimeUnit.MILLISECONDS);
        }
    }
}
