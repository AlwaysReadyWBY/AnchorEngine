package top.alwaysready.anchorengine.fabric.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public interface ItemUtils {

    static Optional<JsonElement> serialize(ItemStack stack){
        return ItemStack.CODEC.encodeStart(JsonOps.INSTANCE,stack).result();
    }

    static Optional<ItemStack> deserialize(JsonElement elem){
        return ItemStack.CODEC.parse(JsonOps.INSTANCE,elem).result();
    }
}
