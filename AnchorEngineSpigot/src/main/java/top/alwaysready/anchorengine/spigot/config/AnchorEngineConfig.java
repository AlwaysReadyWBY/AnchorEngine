package top.alwaysready.anchorengine.spigot.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.server.menu.Menu;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.spigot.AnchorEngineSpigot;
import top.alwaysready.readycore.config.ReadyConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnchorEngineConfig extends ReadyConfig {
    private List<String> autoPush = new ArrayList<>();
    private LoginConfig login;
    private transient final Map<String, Menu> menuMap = new ConcurrentHashMap<>();

    public AnchorEngineConfig(AnchorEngineSpigot plugin) {
        super(plugin);
    }

    @Override
    public void saveResources(boolean replace) {
        super.saveResources(replace);
        getPlugin().saveResource("push/default.json",replace);
    }

    public void loadMenu(){
        File folder = getFile("menu");
        if(!folder.exists()){
            folder.mkdirs();
            getPlugin().saveResource("menu/default.json",true);
        }
        menuMap.clear();
        for (File file : Objects.requireNonNull(folder.listFiles((file, name) -> name.endsWith(".json")))) {
            try(FileInputStream is = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(is)){
                Gson gson = AnchorEngine.getInstance().getCompactGson();
                JsonArray json = gson.fromJson(reader, JsonArray.class);
                json.forEach(elem -> {
                    try {
                        Menu menu = gson.fromJson(elem, Menu.class);
                        if(menu!=null) menuMap.put(menu.getId(),menu);
                    } catch (JsonParseException e){
                        AnchorUtils.warn("Failed to load a menu from "+file.getName(),e);
                    }
                });
            }catch (IOException | JsonParseException e){
                AnchorUtils.warn("Failed to load menu from "+file.getName(),e);
            }
        }
    }

    public Optional<Menu> getMenu(String id){
        return Optional.ofNullable(menuMap.get(id));
    }

    public List<String> getAutoPush() {
        return autoPush;
    }

    public LoginConfig getLoginConfig() {
        if(login == null) login = new LoginConfig();
        return login;
    }
}
