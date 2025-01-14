package top.alwaysready.anchorengine.spigot.reflection;

import com.google.gson.JsonElement;
import org.bukkit.inventory.ItemStack;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class Serializer {
    private Class<?> cNMSItem;
    private Method asNMSCopy;
    private Method asBukkitCopy;
    private Method codecParse;
    private Method codecEncode;
    private Method result;
    private Object ItemCODEC;
    private Object jsonOps;
    private boolean enabled = false;

    public Serializer(ReflectionUtils ref){
        try {
            Class<?> cCraftItem = ref.getCraftClass("inventory.CraftItemStack");
            asNMSCopy = cCraftItem.getDeclaredMethod("asNMSCopy", ItemStack.class);
            cNMSItem = asNMSCopy.getReturnType();
            asBukkitCopy = cCraftItem.getDeclaredMethod("asBukkitCopy", cNMSItem);
            Class<?> cDynamicOps = Class.forName("com.mojang.serialization.DynamicOps");
            Class<?> cDecoder = Class.forName("com.mojang.serialization.Decoder");
            Class<?> cEncoder = Class.forName("com.mojang.serialization.Encoder");
            Class<?> cJsonOps = Class.forName("com.mojang.serialization.JsonOps");
            Class<?> cDataResult = Class.forName("com.mojang.serialization.DataResult");
            result = cDataResult.getDeclaredMethod("result");
            jsonOps = cJsonOps.getDeclaredField("INSTANCE").get(null);
            codecParse = cDecoder.getDeclaredMethod("parse",cDynamicOps,Object.class);
            codecEncode = cEncoder.getDeclaredMethod("encodeStart", cDynamicOps, Object.class);
            Optional<Field> opt = Arrays.stream(cNMSItem.getDeclaredFields())
                    .filter(field -> cDecoder.isAssignableFrom(field.getType()))
                    .findFirst();
            if(opt.isEmpty()) {
                AnchorUtils.warn("Failed to initialize serializer",new RuntimeException("Failed to find ItemStack CODEC"));
            } else {
                ItemCODEC = opt.get().get(null);

            }
            enabled = true;
        } catch (ReflectiveOperationException e){
            AnchorUtils.warn("Failed to initialize serializer",e);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected Optional<Object> asNMSCopy(ItemStack stack){
        if(!isEnabled()) return Optional.empty();
        if(stack == null) return Optional.empty();
        try {
            return Optional.ofNullable(asNMSCopy.invoke(null, stack));
        }catch (ReflectiveOperationException e){
            AnchorUtils.warn("Failed to convert ItemStack to its nms version",e);
            return Optional.empty();
        }
    }

    protected Optional<ItemStack> asBukkitCopy(Object nmsStack){
        if(!isEnabled()) return Optional.empty();
        if (nmsStack==null) return Optional.empty();
        try {
            return Optional.ofNullable(asBukkitCopy.invoke(null, nmsStack))
                    .filter(ItemStack.class::isInstance)
                    .map(ItemStack.class::cast);
        }catch (ReflectiveOperationException e){
            AnchorUtils.warn("Failed to convert ItemStack from its nms version",e);
            return Optional.empty();
        }
    }

    protected Optional<JsonElement> encodeJson(Object codec,Object obj){
        if(!isEnabled()) return Optional.empty();
        try {
            return ((Optional<?>) result.invoke(codecEncode.invoke(codec, jsonOps, obj)))
                    .filter(JsonElement.class::isInstance)
                    .map(JsonElement.class::cast);
        }catch (Exception e){
            AnchorUtils.warn("Failed to encode object to json ( object="+obj+" )",e);
            return Optional.empty();
        }
    }

    protected <T> Optional<T> parseJson(Object codec, JsonElement elem, Class<T> type){
        if(!isEnabled()) return Optional.empty();
        try {
            return ((Optional<?>) result.invoke(codecParse.invoke(codec, jsonOps, elem)))
                    .filter(type::isInstance)
                    .map(type::cast);
        }catch (Exception e){
            AnchorUtils.warn("Failed to decode object from json ( "+elem+" )",e);
            return Optional.empty();
        }
    }

    public Optional<JsonElement> encodeItem(ItemStack stack) {
        if(!isEnabled()) return Optional.empty();
        if(stack == null) return Optional.empty();
        return asNMSCopy(stack).flatMap(nms -> encodeJson(ItemCODEC,nms));
    }

    public Optional<ItemStack> parseItem(JsonElement elem){
        if(!isEnabled()) return Optional.empty();
        if(elem == null || elem.isJsonNull()) return Optional.empty();
        return parseJson(ItemCODEC,elem,cNMSItem).flatMap(this::asBukkitCopy);
    }
}
