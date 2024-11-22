package top.alwaysready.anchorengine.fabric.client.ui.drawable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CursorMovement;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import top.alwaysready.anchorengine.common.action.ActionInfo;
import top.alwaysready.anchorengine.common.client.ClientVarManager;
import top.alwaysready.anchorengine.common.net.packet.json.JsonPacketUtils;
import top.alwaysready.anchorengine.common.string.StringReplacer;
import top.alwaysready.anchorengine.common.ui.element.AInput;
import top.alwaysready.anchorengine.common.ui.layout.board.RenderBounds;
import top.alwaysready.anchorengine.common.ui.layout.board.ResolvedBoard;
import top.alwaysready.anchorengine.common.util.AnchorUtils;
import top.alwaysready.anchorengine.fabric.mixin.EditBoxAccessor;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AInputDrawable extends AnchorDrawable<AInput> {
    private static final int COLOR_SELECTION = 0x77C0C0C0;

    private EditBox editBox;
    private boolean multiline;
    private double lineHeight = 12;
    private int color = 0xffffffff;
    private int offsetX = 0;
    private int cursorX = 0;
    private int selStartX = 0;
    private double selStartY = 0;
    private int selEndX = 0;
    private double selEndY = 0;
    private String cursorLine = "";
    private String var = null;
    private boolean focusRequired = true;
    private boolean obfuscated;
    private ActionInfo onEnter = null;

    public AInputDrawable(AInput elem) {
        super(elem);
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public boolean isObfuscated() {
        return obfuscated;
    }

    public void setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public void setFocusRequired(boolean focusRequired) {
        this.focusRequired = focusRequired;
    }

    public boolean isFocusRequired() {
        return focusRequired;
    }

    public void setOnEnter(ActionInfo onEnter) {
        this.onEnter = onEnter;
    }

    public ActionInfo getOnEnter() {
        return onEnter;
    }

    public Optional<EditBox> getEditBox() {
        return Optional.ofNullable(editBox);
    }

    @Override
    protected void updateForRegion(ResolvedBoard region) {
        var = getElement().getVar(region.getReplacer());
        String str = editBox==null? getAutofill(var,region.getReplacer()): editBox.getText();
        if(var!=null){
            AnchorUtils.getService(ClientVarManager.class).ifPresent(cvm ->
                    cvm.registerClientVar(var,()->getEditBox().map(EditBox::getText).orElse("")));
        }
        setFocusRequired(getElement().isFocusRequired(region.getReplacer()));
        setMultiline(getElement().isMultiline(region.getReplacer()));
        setObfuscated(getElement().isObfuscated(region.getReplacer()));
        setLineHeight(getElement().getLineHeight(region.getReplacer()));
        setColor(getElement().getColor(region.getReplacer()));
        setOnEnter(getElement().getOnEnter().map(info -> info.getReplaced(region.getReplacer())).orElse(null));
        int width = (int) region.getWidth();
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        if (editBox == null || width!=((EditBoxAccessor)editBox).getWidth()){
            editBox = new EditBox(renderer,width);
            editBox.setText(str);
        }
        breakCursorLine(str,renderer,editBox.getCursor(),width);
        setPreferredWidth(renderer.getWidth(str));
        setPreferredHeight(isMultiline()?editBox.getLineCount()*getLineHeight():getLineHeight());
    }

    private String getAutofill(String var,StringReplacer replacer) {
        return AnchorUtils.getService(ClientVarManager.class)
                .flatMap(cvm -> cvm.getClientVar(var))
                .orElseGet(()->getElement().getAutofill(replacer));
    }

    public void breakCursorLine(String text,TextRenderer renderer,int cursor,int width){
        int cursorLineStart = cursor == 0 ? 0 : text.lastIndexOf('\n', cursor - 1) + 1;
        int cursorLineEnd = text.indexOf('\n',cursorLineStart+1);
        if(cursorLineEnd<0) cursorLineEnd = text.length();
        int lWidth = cursorLineStart>=text.length()? 0:renderer.getWidth(cursor>text.length()?
                text.substring(cursorLineStart):
                text.substring(cursorLineStart, cursor));
        cursorX = lWidth+offsetX;
        if(cursorX<0){
            offsetX -= cursorX;
            cursorX = 0;
        }
        if(cursorX>width-1){
            offsetX -= cursorX-width+1;
            cursorX = width-1;
        }
        cursorLine = text.substring(cursorLineStart,cursorLineEnd);

        if(!editBox.hasSelection()) return;
        if(isMultiline()){
            int selStart = editBox.getSelection().beginIndex();
            int selEnd = editBox.getSelection().endIndex();
            int i = 0;
            for (EditBox.Substring line : editBox.getLines()) {
                if(line.beginIndex()<=selStart && selStart<line.endIndex()){
                    selStartY = (i+1)*getLineHeight();
                }
                if(line.beginIndex()<=selEnd && selEnd<line.endIndex()){
                    selEndY = i*getLineHeight();
                }
                selStartX = Math.min(width,Math.max(0,renderer.getWidth(text.substring(cursorLineStart,selStart))))+offsetX;
                selEndX = Math.min(width,Math.max(0,renderer.getWidth(text.substring(cursorLineStart,selEnd))))+offsetX;
                i++;
            }
        } else {
            int selStart = Math.max(editBox.getSelection().beginIndex(),cursorLineStart);
            int selEnd = Math.min(editBox.getSelection().endIndex(),cursorLineEnd);
            selStartX = Math.min(width,Math.max(0,renderer.getWidth(text.substring(cursorLineStart,selStart))))+offsetX;
            selEndX = Math.min(width,Math.max(0,renderer.getWidth(text.substring(cursorLineStart,selEnd))))+offsetX;
        }
    }

    public void setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;
    }

    public double getLineHeight() {
        return lineHeight;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button!=0) return false;
        if (this.isMouseOver(mouseX, mouseY)) {
            setFocused(true);
            playDownSound();
            this.editBox.setSelecting(Screen.hasShiftDown());
            this.moveCursor(mouseX, mouseY);
            return true;
        }
        setFocused(false);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!isEditable()) return false;
        switch (keyCode){
            case 257,335:
                if(onEnter != null && !Screen.hasShiftDown()){
                    JsonPacketUtils.C2S.sendAction(getOnEnter());
                    return true;
                }
            default:
                return this.editBox.handleSpecialKey(keyCode);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isFocused() || button != 0) return false;
        this.editBox.setSelecting(true);
        this.moveCursor(mouseX, mouseY);
        this.editBox.setSelecting(Screen.hasShiftDown());
        return true;
    }

    private void moveCursor(double mouseX, double mouseY) {
        getRegion().ifPresent(region -> {
            if(editBox == null) return;
            String text = editBox.getText();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            double deltaX = mouseX - cursorX - region.getLeft();
            if(isMultiline()) {
                editBox.moveCursor(mouseX - region.getLeft() - offsetX,mouseY-region.getTop());
            } else {
                if(deltaX>0) {
                    editBox.moveCursor(CursorMovement.RELATIVE,
                            textRenderer.trimToWidth(text.substring(editBox.getCursor()), (int) Math.floor(deltaX)).length());
                } else {
                    editBox.moveCursor(CursorMovement.RELATIVE,
                            -textRenderer.trimToWidth(text.substring(0,editBox.getCursor()), (int) Math.floor(-deltaX),true).length());
                }
            }
        });
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (isEditable() && SharedConstants.isValidChar(chr)) {
            this.editBox.replaceSelection(Character.toString(chr));
            return true;
        } else {
            return false;
        }
    }

    public boolean isEditable(){
        return !isFocusRequired() || isFocused();
    }

    @Override
    public void renderImpl(DrawContext context, RenderBounds parentBounds, int mouseX, int mouseY, float delta) {
        if(editBox==null) return;
        getBounds().ifPresent(bounds -> {
            context.enableScissor((int) bounds.left(), (int) bounds.top(), (int) bounds.right(), (int) bounds.bottom());
            MinecraftClient client = MinecraftClient.getInstance();
            double y = (getRegion().get().getTop() + getAlignOffsetY());
            double minY = bounds.top() - getLineHeight();
            int x = (int) (bounds.left() + getAlignOffsetX());
            if(isMultiline()) {
                int cursorY = (int) (y + editBox.getCurrentLineIndex()*getLineHeight());
                String txt = editBox.getText();
                if(isObfuscated()) txt = "*".repeat(txt.length());
                //Render selection
                if(editBox.hasSelection()) {
                    context.fill(x+offsetX+selStartX, (int)(selStartY-getLineHeight()), (int) bounds.right(), (int) selStartY,COLOR_SELECTION);
                    context.fill(x, (int) selStartY, (int) bounds.right(), (int) selEndY,COLOR_SELECTION);
                    context.fill(x, (int) selEndY,x+offsetX+selEndX, (int) (selEndY+getLineHeight()),COLOR_SELECTION);
                }
                //Render text
                for (EditBox.Substring line : editBox.getLines()) {
                    if (y >= minY) {
                        context.drawTextWithShadow(client.textRenderer,
                                txt.substring(line.beginIndex(),line.endIndex()),
                                x+offsetX,
                                (int) y,
                                getColor());
                    }
                    y += getLineHeight();
                    if (y > bounds.bottom()) break;
                }
                //Render cursor
                if(isEditable() && getTotalDelta() % 10 < 5) {
                    context.fill(x+cursorX, cursorY + 1, x+cursorX + 1, (int) (cursorY + getLineHeight() - 1), getColor());
                }
            } else if(y>=minY) {
                //Render selection
                if(editBox.hasSelection()) {
                    context.fill(x+offsetX+selStartX, (int) y,x+offsetX+selEndX, (int) (y+getLineHeight()),COLOR_SELECTION);
                }
                //Render text
                context.drawTextWithShadow(client.textRenderer,
                        isObfuscated()? "*".repeat(cursorLine.length()):cursorLine,
                        x+offsetX,
                        (int) y,
                        getColor());
                //Render cursor
                if(isEditable() && getTotalDelta() % 10 < 5) {
                    context.fill(x+cursorX, (int) (y + 1), x+cursorX + 1, (int) (y + getLineHeight() - 1), getColor());
                }
            }
            context.disableScissor();
        });
    }

    public void playDownSound(){
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK,1.0f));
    }
}
