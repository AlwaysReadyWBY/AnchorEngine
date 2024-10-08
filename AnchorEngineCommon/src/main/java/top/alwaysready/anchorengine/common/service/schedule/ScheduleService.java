package top.alwaysready.anchorengine.common.service.schedule;

import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public interface ScheduleService {
    void executeSync(Runnable run);
    default void schedule(Runnable run){
        scheduleAsync(()-> executeSync(run));
    }
    default void schedule(Runnable run,long delay){
        scheduleAsync(()-> executeSync(run),delay);
    }
    default void loop(BooleanSupplier run,long interval){
        loopAsync(()-> {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            executeSync(()-> future.complete(run.getAsBoolean()));
            return future;
        },interval);
    }
    void scheduleAsync(Runnable run);
    void scheduleAsync(Runnable run,long delay);
    void loopAsync(Supplier<CompletableFuture<Boolean>> run, long interval);
    default void shutdown(){}

    default <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier){
        CompletableFuture<T> future = new CompletableFuture<>();
        scheduleAsync(() -> future.complete(supplier.get()));
        return future;
    }

    default CompletableFuture<Void> runAsync(Runnable run){
        return supplyAsync(()-> {
            run.run();
            return null;
        });
    }
}
