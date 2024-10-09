package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.OrderedText;

@Environment(EnvType.CLIENT)
public record TextLine(OrderedText text,int width) {
}
