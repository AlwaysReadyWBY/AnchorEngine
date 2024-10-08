import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.ui.element.*;
import top.alwaysready.anchorengine.common.ui.layout.Layout;
import top.alwaysready.anchorengine.common.ui.layout.board.PinPoint;
import top.alwaysready.anchorengine.common.util.AnchorUtils;

public class Generate {

    public static void main(String[] args) {
        System.out.println(AnchorEngine.getInstance().getConfigGson().toJson(generateElement(), UIElement.class));
    }

    public static UIElement generateElement(){
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
        img.setUrl("net:https://img.picui.cn/free/2024/08/16/66bf68f219700.png");
        img.getLayout().setPin1("picLT");
        img.getLayout().setPin2("picRB");
        group.getChildren().add(img);
        return group;
    }

    public static UIElement generateBetonQuest(){
        AGroup group = new AGroup();
        PinPoint boxLT = new PinPoint();
        boxLT.setYGrow("0.6");
        boxLT.setYOffset("-8");
        PinPoint boxBgLT = new PinPoint();
        boxBgLT.setYGrow("0.6");
        boxBgLT.setYOffset("-32");
        PinPoint portraitLeftRB = new PinPoint();
        portraitLeftRB.setXGrow("0.25");
        portraitLeftRB.setYGrow("0.6");
        PinPoint portraitRightLB = new PinPoint();
        portraitRightLB.setXGrow("0.75");
        portraitRightLB.setYGrow("0.6");
        PinPoint nameRT = new PinPoint();
        nameRT.setXGrow("0.25");
        nameRT.setYGrow("0.6");
        nameRT.setYOffset("-32");
        PinPoint scrollLT = new PinPoint();
        scrollLT.setXOffset("8");
        scrollLT.setYGrow("0.6");
        PinPoint scrollRB = new PinPoint();
        scrollRB.setXGrow("1");
        scrollRB.setXOffset("-8");
        scrollRB.setYGrow("1");
        scrollRB.setYOffset("-8");
        group.getLayout().getPinMap().put("box_lt",boxLT);
        group.getLayout().getPinMap().put("box_bg_lt",boxBgLT);
        group.getLayout().getPinMap().put("scroll_lt",scrollLT);
        group.getLayout().getPinMap().put("scroll_rb",scrollRB);
        group.getLayout().getPinMap().put("name_rt",nameRT);
        group.getLayout().getPinMap().put("portrait_left_rb",portraitLeftRB);
        group.getLayout().getPinMap().put("portrait_right_lb",portraitRightLB);

        AImage portraitLeft = new AImage();
        portraitLeft.setUrl("%anchor_bq_portrait_left%");
        portraitLeft.getLayout().setPin2("portrait_left_rb");
        portraitLeft.getLayout().setHAlign("0");
        portraitLeft.getLayout().setVAlign("1");
        portraitLeft.setFillMode(AImage.Fill.SCALE);
        group.getChildren().add(portraitLeft);

        AImage portraitRight = new AImage();
        portraitRight.setUrl("%anchor_bq_portrait_right%");
        portraitRight.getLayout().setPin1("portrait_right_lb");
        portraitRight.getLayout().setPin2("right_top");
        portraitRight.getLayout().setHAlign("0");
        portraitRight.getLayout().setVAlign("1");
        portraitRight.setFillMode(AImage.Fill.SCALE);
        group.getChildren().add(portraitRight);

        AImage box = new AImage();
        box.setUrl("res:anchor_engine:textures/gui/conv_bg.png");
        box.setFillMode(AImage.Fill.SCALE_9);
        PinPoint box9p_lt = new PinPoint();
        box9p_lt.setXOffset("8");
        box9p_lt.setYOffset("32");
        PinPoint box9p_rb = new PinPoint();
        box9p_rb.setXGrow("1");
        box9p_rb.setYGrow("1");
        box9p_rb.setXOffset("-8");
        box9p_rb.setYOffset("-10");
        box.getClip().getPinMap().put("9p_lt",box9p_lt);
        box.getClip().getPinMap().put("9p_rb",box9p_rb);
        box.getLayout().getPinMap().put("9p_lt",box9p_lt);
        box.getLayout().getPinMap().put("9p_rb",box9p_rb);
        box.getLayout().setPin1("box_bg_lt");
        box.getLayout().setPin2("right_bottom");
        group.getChildren().add(box);

        AText npcName = new AText();
        npcName.getLayout().setPin1("box_lt");
        npcName.getLayout().setPin2("name_rt");
        npcName.getLayout().setHAlign("0.5");
        npcName.getLayout().setVAlign("0.5");
        npcName.setReplaceable(true);
        npcName.setTextRaw("%anchor_bq_npc_name%");
        group.getChildren().add(npcName);

        AScroll scroll = new AScroll();
        scroll.getLayout().setPin1("scroll_lt");
        scroll.getLayout().setPin2("scroll_rb");
        group.getChildren().add(scroll);


        PinPoint btnRB = new PinPoint();
        btnRB.setXGrow("1");
        btnRB.setYOffset("16");
        AGroup pane = new AGroup();
        pane.setLayoutMode(Layout.LINEAR_VERTICAL);
        pane.getLayout().setVWrap("1");
        pane.getLayout().setPin2("right_top");
        pane.getLayout().getPinMap().put("btn_rb",btnRB);
        scroll.setChild(pane);

        AText text = new AText();
        text.getLayout().setPin2("right_top");
        text.getLayout().setVWrap("1");
        text.setReplaceable(true);
        text.setTextRaw("%anchor_bq_npc_text%");
        pane.getChildren().add(text);

        AButtonWidget widget = new AButtonWidget();
        widget.setId("option");
        widget.setCount("%anchor_bq_option_count%");
//        widget.setCount("4");
        widget.getLayout().setPin2("btn_rb");
        AText btnText = new AText();
        btnText.getLayout().setHAlign("0.5");
        btnText.getLayout().setVAlign("0.5");
        btnText.setReplaceable(true);
        btnText.setTextRaw("%anchor_bq_option_{index_option}%");
        AButton btn = new AButton();
        btn.setOnClick("betonquest:option%index_option%");
        widget.setText(btnText);
        widget.setButton(btn);
        pane.getChildren().add(widget);

        return group;
    }
}
