package top.alwaysready.anchorengine.fabric.client.ui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AScreenHandler extends ScreenHandler {
    public static final ScreenHandlerType<AScreenHandler> TYPE
            = (ScreenHandlerType<AScreenHandler>) Registry.register(Registries.SCREEN_HANDLER, Identifier.of("anchor_engine","generic"), new ScreenHandlerType((i,inv)->new AScreenHandler(i,inv), FeatureFlags.VANILLA_FEATURES));

    protected AScreenHandler(int syncId, PlayerInventory inv) {
        super(TYPE, syncId);
        disableSyncing();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
