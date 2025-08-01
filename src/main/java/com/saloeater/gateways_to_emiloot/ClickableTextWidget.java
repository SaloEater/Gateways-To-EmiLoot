package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.widget.ButtonWidget;
import dev.emi.emi.api.widget.TextWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;

import java.awt.event.KeyEvent;

public class ClickableTextWidget extends TextWidget {
    protected final ButtonWidget.ClickAction action;

    public ClickableTextWidget(FormattedCharSequence text, int x, int y, int color, boolean shadow, ButtonWidget.ClickAction action) {
        super(text, x, y, color, shadow);
        this.action = action;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        action.click(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        action.click(0, 0, keyCodeToButton(keyCode));
        return false;
    }

    private int keyCodeToButton(int keyCode) {
        return keyCode == KeyEvent.VK_U ? 1 : (keyCode == KeyEvent.VK_R ? 0 : -1);
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        super.render(draw, mouseX, mouseY, delta);
    }
}
