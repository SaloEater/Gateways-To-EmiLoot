package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class LootEmiStack extends EmiStack {
    private final String lootTableId;

    public LootEmiStack(String lootTableId) {
        this.lootTableId = lootTableId;
    }

    @Override
    public EmiStack copy() {
        return new LootEmiStack(lootTableId);
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
        return this.lootTableId;
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
        if (stack instanceof LootEmiStack b) {
            return this.lootTableId.equals(b.lootTableId);
        }
        return super.isEqual(stack);
    }
}
