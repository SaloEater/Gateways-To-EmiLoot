package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.ButtonWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.config.EmiConfig;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.forgespi.Environment;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ClickableSlotWidget extends SlotWidget {
    protected final ButtonWidget.ClickAction action;

    public ClickableSlotWidget(EmiIngredient stack, int x, int y, ButtonWidget.ClickAction action) {
        super(stack, x, y);
        this.action = action;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        action.click(mouseX, mouseY, buttonToButton(button));
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        action.click(0, 0, keyCodeToButton(keyCode, scanCode));
        return false;
    }

    private int keyCodeToButton(int keyCode, int scanCode) {
        if (EmiConfig.viewRecipes.matchesKey(keyCode, scanCode)) {
            return 0;
        }

        if (EmiConfig.viewUses.matchesKey(keyCode, scanCode)) {
            return 1;
        }
        return -1;
    }

    private int buttonToButton(int button) {
        if (EmiConfig.viewRecipes.matchesMouse(button)) {
            return 0;
        }

        if (EmiConfig.viewUses.matchesMouse(button)) {
            return 1;
        }
        return -1;
    }

    @Override
    public void render(GuiGraphics draw, int mouseX, int mouseY, float delta) {
        super.render(draw, mouseX, mouseY, delta);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        var tooltipOld = super.getTooltip(mouseX, mouseY);
        List<ClientTooltipComponent> tooltip = new ArrayList<>();
        tooltip.add(tooltipOld.get(0));
        tooltip.add(tooltipOld.get(2));
        return tooltip;
    }

    @Override
    public ClickableSlotWidget appendTooltip(Component text) {
        super.appendTooltip(text);
        return this;
    }
}
