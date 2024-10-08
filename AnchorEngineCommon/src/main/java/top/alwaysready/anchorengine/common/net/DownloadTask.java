package top.alwaysready.anchorengine.common.net;

import top.alwaysready.anchorengine.common.service.FileService;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

public class DownloadTask {
    private final String localPath;
    private final String path;
    private CompletableFuture<Boolean> future;
    private boolean successful = false;

    public DownloadTask(String path,String localPath) {
        int index = path.lastIndexOf('.');
        String ext = index>=0? path.substring(index):"";
        this.localPath = localPath + ext;
        this.path = path;
        AnchorUtils.info("Downloading "+path+" as "+getLocalPath());
    }

    public String getLocalPath() {
        return localPath;
    }

    public File getFile(){
        return AnchorUtils.getService(FileService.class).map(sv -> sv.getFile(getLocalPath())).orElse(null);
    }

    public CompletableFuture<Boolean> start(){
        if(future != null) return future;
        future = AnchorUtils.getService(ScheduleService.class).map(sv->sv.supplyAsync(()->{
            int byteread;
            File file = getFile();
            if(file == null) return false;
            File parent = file.getParentFile();
            if(parent.exists() && !parent.isDirectory()) {
                AnchorUtils.info("Invalid local path: %0%", file);
                return false;
            }
            if(!parent.exists() && !parent.mkdirs()){
                AnchorUtils.info("Failed to create folder: %0%", parent);
                return false;
            }
            URL url;
            try {
                url = URI.create(path).toURL();
            } catch (MalformedURLException e) {
                AnchorUtils.warn("Invalid resource path: "+ path,e);
                return false;
            }
            URLConnection conn;
            try {
                conn = url.openConnection();
            } catch (IOException e) {
                AnchorUtils.info("Failed to connect resource: "+ path,e);
                return false;
            }
            try(
                    InputStream in = conn.getInputStream();
                    FileOutputStream fs = new FileOutputStream(file)
            ) {
                byte[] buffer = new byte[32768];
                while ((byteread = in.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                successful = true;
                return true;
            } catch (IOException e) {
                AnchorUtils.info("Failed to download resource: "+path,e);
                return false;
            }
        })).orElse(CompletableFuture.completedFuture(false));
        return future;
    }

    public void reset(){
        if(future != null) future.cancel(true);
        future = null;
    }

    public boolean isDone() {
        return future!=null && future.isDone();
    }

    public boolean isSuccessful() {
        return successful;
    }
}
