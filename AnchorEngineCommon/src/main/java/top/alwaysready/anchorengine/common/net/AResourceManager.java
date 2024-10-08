package top.alwaysready.anchorengine.common.net;

import top.alwaysready.anchorengine.common.util.Registry;

public class AResourceManager {
    private final Registry<AResourceLocation> LOCAL = new Registry<>();
    private final Registry<AResourceLocation> NETWORK = new Registry<>();
    private final Registry<AResourceLocation> RESOURCE = new Registry<>();

    private long nextKey = 0;

    public AResourceLocation getOrCreate(String res){
        if(res == null) return null;
        int index = res.indexOf(':');
        return index<0?
                getOrCreate(AResourceLocation.LocationType.NETWORK,res):
                switch (res.substring(0,index).toLowerCase()){
                    default -> getOrCreate(AResourceLocation.LocationType.NETWORK,res);
                    case "net","network" -> getOrCreate(AResourceLocation.LocationType.NETWORK,res.substring(index+1));
                    case "local" -> getOrCreate(AResourceLocation.LocationType.LOCAL,res.substring(index+1));
                    case "res","resource" -> getOrCreate(AResourceLocation.LocationType.RESOURCE,res.substring(index+1));
                };
    }

    public AResourceLocation getOrCreate(AResourceLocation.LocationType type,String res) {
        Registry<AResourceLocation> reg = switch (type){
            case RESOURCE -> RESOURCE;
            case LOCAL -> LOCAL;
            case NETWORK -> NETWORK;
        };
        return reg.getOrCreate(res,()->new AResourceLocation(res,type));
    }

    public String nextKey() {
        return "temp/"+ (nextKey++);
    }
}
