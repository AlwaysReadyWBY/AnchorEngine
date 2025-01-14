package top.alwaysready.anchorengine.fabric.client;

import com.google.gson.JsonElement;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import top.alwaysready.anchorengine.common.client.ClientChannelHandler;
import top.alwaysready.anchorengine.common.client.ClientVarManager;
import top.alwaysready.anchorengine.common.client.ui.UIRoot;
import top.alwaysready.anchorengine.common.serialization.SerializableItem;
import top.alwaysready.anchorengine.common.service.ExecutorScheduleService;
import top.alwaysready.anchorengine.common.service.FileService;
import top.alwaysready.anchorengine.common.service.schedule.ScheduleService;
import top.alwaysready.anchorengine.common.ui.element.AGroup;
import top.alwaysready.anchorengine.common.ui.element.AImage;
import top.alwaysready.anchorengine.common.ui.element.AItem;
import top.alwaysready.anchorengine.common.ui.layout.board.PinPoint;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.client.net.FabricClientChannelHandler;
import top.alwaysready.anchorengine.fabric.client.resource.ATextureManager;
import top.alwaysready.anchorengine.fabric.client.ui.AScreenHandler;
import top.alwaysready.anchorengine.fabric.client.ui.AnchorInventory;
import top.alwaysready.anchorengine.fabric.client.ui.drawable.ADrawableManager;
import top.alwaysready.anchorengine.fabric.util.ItemUtils;

import java.io.File;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AnchorEngineFabricClient implements ClientModInitializer {

    private FabricUIRoot uiRoot;

    @Override
    public void onInitializeClient() {
        HandledScreens.register(AScreenHandler.TYPE, AnchorInventory::new);
        AnchorUtils.registerService(ScheduleService.class, new ExecutorScheduleService(2) {
            @Override
            public void executeSync(Runnable run) {
                MinecraftClient.getInstance().execute(run);
            }
        });
        AnchorUtils.registerService(FileService.class, this::getFile);
        AnchorUtils.registerService(ClientVarManager.class, new ClientVarManager());
        AnchorUtils.registerService(ATextureManager.class, new ATextureManager());
        AnchorUtils.registerService(UIRoot.class, getUIRoot());
        AnchorUtils.registerService(ADrawableManager.class, new ADrawableManager());

        new FabricClientChannelHandler().register();

        AnchorUtils.getService(ScheduleService.class).ifPresent(sch -> {
            sch.loopAsync(getUIRoot()::update, 16);
            AnchorUtils.getService(ClientVarManager.class).ifPresent(cvm -> {
                sch.loopAsync(cvm::purge, cvm.getKeepMillis());
                AnchorUtils.getService(ClientChannelHandler.class).ifPresent(handler ->{
                    sch.loopAsync(()->cvm.query(handler), 100);
                });
            });
        });

        registerCommand();
    }

    public void registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("anchorc")
                .then(literal("showImage").then(argument("url", greedyString()).executes(this::showImage)))
                .then(literal("debug").executes(this::debug))
                .then(literal("dumpItem").executes(this::dumpItem))));
    }

    public FabricUIRoot getUIRoot() {
        if (uiRoot == null) uiRoot = new FabricUIRoot();
        return uiRoot;
    }

    public File getFile(String path) {
        File file = new File("config/anchor/");
        if (!file.exists()) file.mkdirs();
        return file.toPath().resolve(path).toFile();
    }

    private int showImage(CommandContext<FabricClientCommandSource> context) {
        AGroup group = new AGroup();
        PinPoint lt = new PinPoint();
        lt.setXGrow("0.25");
        lt.setYGrow("0.25");
        PinPoint rb = new PinPoint();
        rb.setXGrow("0.75");
        rb.setYGrow("0.75");
        group.getLayout().getPinMap().put("picLT", lt);
        group.getLayout().getPinMap().put("picRB", rb);
        AImage img = new AImage();
        img.setFillMode(AImage.Fill.SCALE);
        img.getLayout().setHAlign("0.5");
        img.getLayout().setVAlign("0.5");
        img.setUrl(context.getArgument("url", String.class));
        img.getLayout().setPin1(lt);
        img.getLayout().setPin2(rb);
        group.getChildren().add(img);
        AnchorUtils.getService(UIRoot.class).ifPresent(uiRoot -> {
            AnchorUtils.getService(ScheduleService.class).ifPresent(sch ->
                    sch.scheduleAsync(() -> uiRoot.setScreen(group), 100L));
        });
        return 1;
    }

    private int debug(CommandContext<FabricClientCommandSource> context) {
        AGroup group = new AGroup();
        PinPoint lt = new PinPoint();
        lt.setXGrow("0.25");
        lt.setYGrow("0.25");
        PinPoint rb = new PinPoint();
        rb.setXGrow("0.75");
        rb.setYGrow("0.75");
        group.getLayout().getPinMap().put("inLT", lt);
        group.getLayout().getPinMap().put("inRB", rb);
        AItem item = new AItem();
        SerializableItem si = new SerializableItem();
        ItemUtils.serialize(new ItemStack(Items.APPLE,64))
                .ifPresentOrElse(si::setJson,()->AnchorUtils.debug("Failed to serialize item"));
        item.setItem(si);
        item.getLayout().setPin1(lt);
        item.getLayout().setPin2(rb);
        item.getLayout().setVAlign("0.5");
        item.getLayout().setHAlign("0.5");
        group.getChildren().add(item);
        AnchorUtils.getService(UIRoot.class).ifPresent(uiRoot -> {
            AnchorUtils.getService(ScheduleService.class).ifPresent(sch ->
                    sch.scheduleAsync(() -> uiRoot.setScreen(group), 100L));
        });
        return 1;
    }

    private int dumpItem(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        if(player==null) return 0;
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        return ItemUtils.serialize(stack).map(JsonElement::toString).map(json -> {
            player.sendMessage(Text.translatable("message.anchor_engine.click_to_copy").styled(style -> style
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,Text.literal(json)))
                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,json))));
            return 1;
        }).orElse(0);
    }
}
