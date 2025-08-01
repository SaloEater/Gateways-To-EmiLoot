package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MobEmiStack extends EmiStack {
    private final String descriptionId;

    public MobEmiStack(String lootTableId) {
        this.descriptionId = lootTableId;
    }

    @Override
    public EmiStack copy() {
        return new MobEmiStack(descriptionId);
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta, int flags) {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public CompoundTag getNbt() {
        return null;
    }

    @Override
    public Object getKey() {
        return this.descriptionId;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public List<Component> getTooltipText() {
        return List.of();
    }

    @Override
    public Component getName() {
        return null;
    }

    @Override
    public boolean isEqual(EmiStack stack) {
        if (stack instanceof MobEmiStack b) {
            return this.descriptionId.equals(b.descriptionId);
        }
        return super.isEqual(stack);
    }
}
