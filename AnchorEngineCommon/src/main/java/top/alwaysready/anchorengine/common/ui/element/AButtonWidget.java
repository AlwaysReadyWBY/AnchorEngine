package top.alwaysready.anchorengine.common.ui.element;

import java.util.List;
import java.util.Optional;

public class AButtonWidget extends AGroup{
    private AText text;
    private AButton button;
    private AImage image;

    public void setText(AText text) {
        this.text = text;
    }

    public Optional<AText> getText() {
        return Optional.ofNullable(text);
    }

    public void setButton(AButton button) {
        this.button = button;
    }

    public Optional<AButton> getButton() {
        return Optional.ofNullable(button);
    }

    public void setImage(AImage image) {
        this.image = image;
    }

    public Optional<AImage> getImage() {
        return Optional.ofNullable(image);
    }

    @Override
    public List<UIElement> getChildren() {
        List<UIElement> children = super.getChildren();
        if(children.isEmpty()) {
            getImage().ifPresent(children::add);
            getButton().ifPresent(children::add);
            getText().ifPresent(children::add);
        }
        return children;
    }
}
