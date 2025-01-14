package top.alwaysready.anchorengine.spigot.reflection;

import org.bukkit.Server;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

public class ReflectionUtils {
    private final Server server;
    private NMSVersion nmsVersion;
    private Serializer serializer;

    public ReflectionUtils(Server server) {
        this.server = server;
    }

    public NMSVersion getNmsVersion() {
        if(nmsVersion == null) nmsVersion = NMSVersion.auto(server);
        return nmsVersion;
    }

    public Serializer getSerializer() {
        if(serializer == null) serializer = new Serializer(this);
        return serializer;
    }

    public Class<?> getCraftClass(String path){
        try{
            NMSVersion version = getNmsVersion();
            if(version.isLegacy()) {
                try {
                    return Class.forName("org.bukkit.craftbukkit." + version + "." + path);
                }catch (ReflectiveOperationException e){
                    return Class.forName("org.bukkit.craftbukkit."+path);
                }
            } else {
                return Class.forName("org.bukkit.craftbukkit."+path);
            }
        }catch (ReflectiveOperationException e){
            AnchorUtils.warn("Failed to resolve craft bukkit class "+path,e);
        }
        return null;
    }

    public Class<?> getNMSClass(String path,String legacy){
        try{
            NMSVersion version = getNmsVersion();
            if(version.isLegacy()) {
                try {
                    return Class.forName("net.minecraft.server." + version + "." + legacy);
                }catch (ReflectiveOperationException e){
                    return Class.forName("net.minecraft."+path);
                }
            } else {
                return Class.forName("net.minecraft."+path);
            }
        }catch (ReflectiveOperationException e){
            AnchorUtils.warn("Failed to resolve craft bukkit class "+path,e);
        }
        return null;
    }
}
