package top.alwaysready.anchorengine.common.net;
import top.alwaysready.anchorengine.common.service.FileService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

public class AResourceLocation {
    private DownloadTask downloadTask;
    private boolean loaded = false;
    private final String path;
    private final LocationType locationType;

    public AResourceLocation(String path, LocationType locationType) {
        this.path = path;
        this.locationType = locationType;
    }

    public DownloadTask getDownloadTask() {
        if(downloadTask == null) {
            AnchorUtils.getService(AResourceManager.class)
                    .ifPresent(rm -> downloadTask = new DownloadTask(getPath(), rm.nextKey()));
        }
        return downloadTask;
    }

    public void reset(){
        if(downloadTask!=null) downloadTask.reset();
        loaded = false;
    }

    public void load(AResourceLoader loader){
        switch (getLocationType()){
            case RESOURCE -> loader.loadResource(AnchorUtils.toKey("minecraft",getPath()));
            case LOCAL -> AnchorUtils.getService(FileService.class).map(sv -> sv.getFile(getPath())).ifPresent(loader::loadFile);
            case NETWORK -> getDownloadTask().start().thenAccept(success -> {
               if(success) {
                   loader.loadFile(getDownloadTask().getFile());
               } else {
                   loader.onFail();
               }
            });
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public String getPath() {
        return path;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public enum LocationType{
        //In resource packs
        RESOURCE,
        //In server/plugins/AnchorEngine/resources/ or client/config/AnchorEngineFabric(Forge)/resources/
        LOCAL,
        NETWORK
    }
}
