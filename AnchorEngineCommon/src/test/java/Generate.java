import top.alwaysready.anchorengine.common.AnchorEngine;
import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.ui.element.*;
import top.alwaysready.anchorengine.common.ui.layout.Layout;
import top.alwaysready.anchorengine.common.ui.layout.board.PinPoint;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Generate {

    public static void main(String[] args) {
        try(PrintStream out = new PrintStream(System.out,true, StandardCharsets.UTF_8)) {
            out.println(AnchorEngine.getInstance().getConfigGson().toJson(generateLogin(), UIElement.class));
        }
    }

    public static UIElement generateLogin(){
        AGroup group = new AGroup();
        PinPoint lt = new PinPoint();
        lt.setXGrow("0.25");
        lt.setYGrow("0.25");
        PinPoint rb = new PinPoint();
        rb.setXGrow("0.75");
        rb.setYGrow("0.75");
        group.getLayout().getPinMap().put("boxLT",lt);
        group.getLayout().getPinMap().put("boxRB",rb);

        AGroup box = new AGroup();
        box.getLayout().setPin1("boxLT");
        box.getLayout().setPin2("boxRB");

        PinPoint rbw = new PinPoint();
        rbw.setXGrow("1");
        rbw.setYOffset("12");
        box.getLayout().getPinMap().put("wrapRB",rbw);

        AImage boxBg = new AImage();
        boxBg.setUrlFromResource("minecraft:textures/gui/sprites/popup/background.png");
        boxBg.setFillMode(AImage.Fill.SCALE_9);
        PinPoint p9lt = new PinPoint();
        p9lt.setXOffset("6");
        p9lt.setYOffset("6");
        PinPoint p9rb = new PinPoint();
        p9rb.setXGrow("1");
        p9rb.setYGrow("1");
        p9rb.setXOffset("-6");
        p9rb.setYOffset("-6");
        boxBg.getClip().getPinMap().put("9p_lt",p9lt);
        boxBg.getClip().getPinMap().put("9p_rb",p9rb);
        box.getChildren().add(boxBg);
        box.getLayout().getPinMap().put("content_lt",p9lt);
        box.getLayout().getPinMap().put("content_rb",p9rb);

        AScroll scr = new AScroll();
        scr.getLayout().setPin1("content_lt");
        scr.getLayout().setPin2("content_rb");
        PinPoint itemRB = new PinPoint();
        itemRB.setXGrow("1");
        itemRB.setYOffset("20");
        scr.getLayout().getPinMap().put("item_rb",itemRB);

        AGroup pane = new AGroup();
        PinPoint fixLT = new PinPoint();
        fixLT.setXOffset("6");
        fixLT.setYOffset("2");
        PinPoint fixRB = new PinPoint();
        fixRB.setXGrow("1");
        fixRB.setYOffset("18");
        pane.setLayoutMode(Layout.LINEAR_VERTICAL);
        pane.getLayout().setVWrap("1");
        pane.getLayout().setPin2("item_rb");
        pane.getLayout().getPinMap().put("fix_lt",fixLT);
        pane.getLayout().getPinMap().put("fix_rb",fixRB);
        scr.setChild(pane);

        AText title = new AText();
        title.setReplaceable(true);
        title.setTextRaw("%server_name%");
        title.getLayout().setHAlign("0.5");
        title.getLayout().setVAlign("0.5");
        title.getLayout().setPin1("fix_lt");
        title.getLayout().setPin2("fix_rb");
        pane.getChildren().add(title);

        AGroup inWidget = new AGroup();

        AImage inputBg = new AImage();
        inputBg.setUrlFromResource("minecraft:textures/gui/sprites/widget/text_field.png");
        inputBg.setFillMode(AImage.Fill.SCALE_9);
        PinPoint p9ltIn = new PinPoint();
        p9ltIn.setXOffset("2");
        p9ltIn.setYOffset("2");
        PinPoint p9rbIn = new PinPoint();
        p9rbIn.setXGrow("1");
        p9rbIn.setYGrow("1");
        p9rbIn.setXOffset("-2");
        p9rbIn.setYOffset("-2");
        inputBg.getClip().getPinMap().put("9p_lt",p9ltIn);
        inputBg.getClip().getPinMap().put("9p_rb",p9rbIn);
        inWidget.getChildren().add(inputBg);

        inWidget.getLayout().getPinMap().put("content_lt",p9ltIn);
        inWidget.getLayout().getPinMap().put("content_rb",p9rbIn);

        AInput input = new AInput();
        input.setVar("anchor_passwd");
        ActionInfo login = new ActionInfo("anchor_engine:login");
        login.setParam("passwd","%anchor_passwd%");
        input.setOnEnter(login);
        input.setObfuscated("1");
        input.setFocusRequired("0");
        input.getLayout().setPin1("content_lt");
        input.getLayout().setPin2("content_rb");
        inWidget.getChildren().add(input);

        inWidget.getLayout().setPin1("fix_lt");
        inWidget.getLayout().setPin2("fix_rb");
        pane.getChildren().add(inWidget);

        AButtonWidget loginWidget = new AButtonWidget();
        AText loginTxt = new AText();
        loginTxt.getLayout().setHAlign("0.5");
        loginTxt.getLayout().setVAlign("0.5");
        loginTxt.setTextRaw("登录");
        loginWidget.setText(loginTxt);
        AButton loginBtn = new AButton();
        loginBtn.setOnClick(login);
        loginBtn.setStyle(AButton.Styles.VANILLA);
        loginWidget.setButton(loginBtn);

        loginWidget.getLayout().setPin1("fix_lt");
        loginWidget.getLayout().setPin2("fix_rb");
        pane.getChildren().add(loginWidget);

        AButtonWidget regWidget = new AButtonWidget();
        AText regTxt = new AText();
        regTxt.getLayout().setHAlign("0.5");
        regTxt.getLayout().setVAlign("0.5");
        regTxt.setTextRaw("注册");
        regWidget.setText(regTxt);
        AButton regBtn = new AButton();
        ActionInfo reg = new ActionInfo("anchor_engine:register");
        reg.setParam("passwd","%anchor_passwd%");
        regBtn.setOnClick(reg);
        regBtn.setStyle(AButton.Styles.VANILLA);
        regWidget.setButton(regBtn);

        regWidget.getLayout().setPin1("fix_lt");
        regWidget.getLayout().setPin2("fix_rb");
        pane.getChildren().add(regWidget);

        box.getChildren().add(scr);

        group.getChildren().add(box);
        return group;
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
        ActionInfo onClick = new ActionInfo("betonquest:option");
        onClick.setParam("index","%index_option%");
        btn.setOnClick(onClick);
        widget.setText(btnText);
        widget.setButton(btn);
        pane.getChildren().add(widget);

        return group;
    }
}
