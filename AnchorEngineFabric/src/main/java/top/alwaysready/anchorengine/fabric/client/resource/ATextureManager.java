package top.alwaysready.anchorengine.fabric.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import top.alwaysready.anchorengine.common.net.AResourceLocation;
import top.alwaysready.anchorengine.common.net.AResourceManager;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public class ATextureManager {

    private final Map<String, ATexture> textureMap = new Hashtable<>();
    private final Map<String, CompletableFuture<ATexture>> futureMap = new ConcurrentHashMap<>();
    private ATexture loading;
    private ATexture missing;

    public ATextureManager(){
        AnchorUtils.getService(AResourceManager.class).ifPresent(rm -> {
            getOrLoad(rm.getOrCreate(AResourceLocation.LocationType.RESOURCE,AnchorUtils.toKey("anchor_engine:textures/gui/loading.png")))
                    .thenAccept(texture -> loading = texture);
            getOrLoad(rm.getOrCreate(AResourceLocation.LocationType.RESOURCE,AnchorUtils.toKey("anchor_engine:textures/gui/missing.png")))
                    .thenAccept(texture -> missing = texture);
        });
    }

    public ATexture getLoadingTexture() {
        return loading;
    }

    public ATexture getMissingTexture() {
        return missing;
    }

    public CompletableFuture<ATexture> getOrLoad(AResourceLocation loc){
        String path = loc.getPath();
        if(path == null) {
            AnchorUtils.info("Ignoring a texture with null path.");
            return CompletableFuture.completedFuture(null);
        }
        ATexture texture = textureMap.get(path);
        if(texture != null) return CompletableFuture.completedFuture(texture);
        return futureMap.computeIfAbsent(path, any -> {
            ATextureLoader loader = new ATextureLoader();
            loc.load(loader);
            return loader.getFuture().whenComplete((res,thr)->{
                //Avoid concurrent modification.
                AnchorUtils.getService(ScheduleService.class).ifPresent(sv ->
                        sv.runAsync(()-> futureMap.remove(path)));
                if(thr!= null) {
                    AnchorUtils.warn("Failed to load texture "+loc.getLocationType()+"("+path+")",thr);
                    return;
                }
                if(res == null) return;
                if(loc.getLocationType() == AResourceLocation.LocationType.NETWORK) {
                    loc.getDownloadTask().getFile().deleteOnExit();
                }
                textureMap.put(path,res);
            });
        });
    }

    public void unload(String path){
        ATexture texture = textureMap.remove(path);
        if(texture == null || texture.isPersistent()) return;
        MinecraftClient.getInstance().getTextureManager().destroyTexture(texture.getId());
    }
}
