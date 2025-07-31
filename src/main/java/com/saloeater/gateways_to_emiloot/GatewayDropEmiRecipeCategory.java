package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.shadowsoffire.gateways.gate.Gateway;
import dev.shadowsoffire.gateways.item.GatePearlItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static dev.shadowsoffire.gateways.item.GatePearlItem.getGate;

public class GatewayDropEmiRecipeCategory extends EmiRecipeCategory {
    Component name;
    public GatewayDropEmiRecipeCategory(ResourceLocation id, EmiRenderable icon, ItemStack gatePearl) {
        super(id, icon);
        if (gatePearl.getItem() instanceof GatePearlItem) {
            var gate = getGate(gatePearl);
            if (gate.isBound()) {
                name = Component.translatable(gate.getId().toString().replace(':', '.')).withStyle(Style.EMPTY.withColor(gate.get().color()));
                return;
            }
        }
        name = gatePearl.getHoverName();
    }

    @Override
    public Component getName() {
        return name;
    }
}
