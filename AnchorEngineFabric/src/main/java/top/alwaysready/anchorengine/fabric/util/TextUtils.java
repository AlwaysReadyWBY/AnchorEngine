package top.alwaysready.anchorengine.fabric.util;

import com.google.gson.JsonElement;
import net.minecraft.text.Text;
import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.serialization.StringReplacer;
import top.alwaysready.anchorengine.common.serialization.SerializableText;

public interface TextUtils {

    static Text deserialize(SerializableText text, StringReplacer replacer){
        try {
            return Text.Serializer.fromJson(AnchorEngine.getInstance().getCompactGson()
                    .fromJson(text.getJson(replacer), JsonElement.class));
        } catch (IllegalStateException e) {
            return Text.empty();
        }
    }
}
