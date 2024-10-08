package top.alwaysready.anchorengine.fabric.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import top.alwaysready.anchorengine.common.net.AResourceLoader;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class ATextureLoader implements AResourceLoader {
    private final CompletableFuture<ATexture> future;

    public ATextureLoader() {
        future = new CompletableFuture<>();
    }

    public CompletableFuture<ATexture> getFuture() {
        return future;
    }

    @Override
    public void loadResource(String key) {
        Identifier id = Identifier.tryParse(key);
        if(id == null){
            AnchorUtils.info("Invalid id "+key);
            onFail();
            return;
        }
        AnchorUtils.getService(ScheduleService.class).ifPresentOrElse(sv -> {
            sv.runAsync(()-> MinecraftClient.getInstance().getResourceManager().getResource(id).ifPresentOrElse(res ->{
                try(InputStream in = res.getInputStream();
                    ImageInputStream imgIn = ImageIO.createImageInputStream(in)){
                    Iterator<ImageReader> readers = ImageIO.getImageReaders(imgIn);
                    if(readers.hasNext()){
                        ImageReader reader = readers.next();
                        try{
                            reader.setInput(imgIn);
                            ATexture texture = new ATexture(id);
                            texture.setWidth(reader.getWidth(0));
                            texture.setHeight(reader.getHeight(0));
                            future.complete(texture);
                        } catch (Exception e){
                            AnchorUtils.warn("Failed to parse resource: "+ key,e);
                            onFail();
                        } finally {
                            reader.dispose();
                        }
                    }
                } catch (IOException e) {
                    AnchorUtils.warn("Failed to parse resource: "+ key,e);
                    onFail();
                }
            },this::onFail));
        },this::onFail);
    }

    @Override
    public void loadFile(File file) {
        if(!file.exists()){
            onFail();
            return;
        }
        try (FileInputStream input = new FileInputStream(file)){
            NativeImage img = NativeImage.read(input);
            Identifier id = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("anchor_engine", new NativeImageBackedTexture(img));
            ATexture texture = new ATexture(id);
            texture.setWidth(img.getWidth());
            texture.setHeight(img.getHeight());
            future.complete(texture);
        } catch (IOException e) {
            AnchorUtils.warn("Failed to load local image.",e);
            onFail();
        }
    }

    @Override
    public void onFail() {
        future.cancel(true);
    }
}
