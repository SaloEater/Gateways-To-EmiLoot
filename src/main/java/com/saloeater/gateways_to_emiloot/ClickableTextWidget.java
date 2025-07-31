package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.widget.ButtonWidget;
import dev.emi.emi.api.widget.TextWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;

public class ClickableTextWidget extends TextWidget {
    protected final ButtonWidget.ClickAction action;

    public ClickableTextWidget(MutableComponent text, int x, int y, int color, boolean shadow, ButtonWidget.ClickAction action) {
        super(text.getVisualOrderText(), x, y, color, shadow);
        this.action = action;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        action.click(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        super.render(draw, mouseX, mouseY, delta);
    }
}
