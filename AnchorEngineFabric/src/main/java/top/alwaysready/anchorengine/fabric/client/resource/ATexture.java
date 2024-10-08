package top.alwaysready.anchorengine.fabric.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ATexture {
    private final Identifier id;
    private int width;
    private int height;
    private boolean persistent;

    public ATexture(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isPersistent() {
        return persistent;
    }
}
