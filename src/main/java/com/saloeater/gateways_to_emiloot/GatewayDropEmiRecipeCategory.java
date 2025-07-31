package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GatewayDropEmiRecipeCategory extends EmiRecipeCategory {
    Component name;
    public GatewayDropEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, ItemStack gatePearl) {
        super(id, icon);
        name = gatePearl.getHoverName();
    }

    @Override
    public Component getName() {
        return name;
    }
}
