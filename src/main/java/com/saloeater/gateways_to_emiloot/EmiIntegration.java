package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.shadowsoffire.gateways.gate.Gateway;
import dev.shadowsoffire.gateways.gate.GatewayRegistry;
import dev.shadowsoffire.gateways.gate.Reward;
import dev.shadowsoffire.gateways.gate.endless.EndlessGateway;
import dev.shadowsoffire.gateways.gate.normal.NormalGateway;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.shadowsoffire.gateways.GatewayObjects.GATE_PEARL;
import static dev.shadowsoffire.gateways.item.GatePearlItem.setGate;

@EmiEntrypoint
public class EmiIntegration implements EmiPlugin {
    public static List<EmiRecipeCategory> categories = new ArrayList<>();

    @Override
    public void register(EmiRegistry emiRegistry) {
        GatewayRegistry.INSTANCE.getKeys().forEach(resourceLocation -> {
            var gate = GatewayRegistry.INSTANCE.getValue(resourceLocation);
            if (gate instanceof NormalGateway normalGateway) {
                var gatePearl = registerGatewayCategory(emiRegistry, resourceLocation, gate);
                int categoryIndex = categories.size() - 1;
                for (int i = 0; i < normalGateway.waves().size(); i++) {
                    var wave = normalGateway.waves().get(i);
                    List<Reward> rewards = wave.rewards();
                    if (isRewardsEmpty(rewards)) continue;
                    emiRegistry.addRecipe(new GatewayDropEmiRecipe(gatePearl, resourceLocation, new GatewayDropRecipe(rewards, wave.entities(), i + 1), categoryIndex));
                }
                if (isRewardsEmpty(normalGateway.rewards())) return;
                emiRegistry.addRecipe(new GatewayDropEmiRecipe(gatePearl, resourceLocation, new GatewayDropRecipe(normalGateway.rewards()), categoryIndex));
            } else if (gate instanceof EndlessGateway endlessGateway) {
                var gatePearl = registerGatewayCategory(emiRegistry, resourceLocation, gate);
                int categoryIndex = categories.size() - 1;
                if (isRewardsEmpty(endlessGateway.baseWave().rewards())) return;
                emiRegistry.addRecipe(new GatewayDropEmiRecipe(gatePearl, resourceLocation, new GatewayDropRecipe(endlessGateway.baseWave().rewards(), endlessGateway.baseWave().entities(), GatewayDropRecipe.ENDLESS), categoryIndex));
            }
        });
    }

    private static @NotNull ItemStack registerGatewayCategory(EmiRegistry emiRegistry, ResourceLocation resourceLocation, Gateway gate) {
        var gatePearl = new ItemStack(GATE_PEARL.get());
        setGate(gatePearl, gate);
        EmiRecipeCategory category = new GatewayDropEmiRecipeCategory(
            ResourceLocation.tryBuild(resourceLocation.getNamespace(), resourceLocation.getPath()),
            EmiStack.of(gatePearl),
            gatePearl
        );
        emiRegistry.addCategory(category);
        categories.add(category);
        return gatePearl;
    }

    private static boolean isRewardsEmpty(List<Reward> rewards) {
        AtomicBoolean empty = new AtomicBoolean(true);
        rewards.forEach(reward -> {
            if (reward instanceof Reward.StackReward || reward instanceof Reward.StackListReward || reward instanceof Reward.EntityLootReward || reward instanceof Reward.LootTableReward || reward instanceof Reward.ChancedReward) {
                empty.set(false);
            }
        });
        return empty.get();
    }
}
