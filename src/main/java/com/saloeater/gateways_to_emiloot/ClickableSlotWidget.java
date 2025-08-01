package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.ButtonWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.forgespi.Environment;

import java.awt.event.KeyEvent;

public class ClickableSlotWidget extends SlotWidget {
    protected final ButtonWidget.ClickAction action;

    public ClickableSlotWidget(EmiIngredient stack, int x, int y, ButtonWidget.ClickAction action) {
        super(stack, x, y);
        this.action = action;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        action.click(mouseX, mouseY, button);
        return false;
        //return super.mouseClicked(mouseX, mouseY, button);
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
