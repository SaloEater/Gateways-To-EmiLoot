package com.saloeater.gateways_to_emiloot;

import dev.shadowsoffire.gateways.gate.Reward;
import dev.shadowsoffire.gateways.gate.WaveEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GatewayDropRecipe {
    public static final int FINAL = -1;
    public int waveIndex;
    public List<ItemStackReward> stacks = new ArrayList<>();
    public List<EntityReward> entityIds = new ArrayList<>();
    public List<LootTableReward> lootTableRewards = new ArrayList<>();
    public List<EntityWithCount> entityWithCounts = new ArrayList<>();

    public GatewayDropRecipe(List<Reward> rewards) {
        this(rewards, new ArrayList<>(), FINAL);
    }

    public GatewayDropRecipe(List<Reward> rewards, List<WaveEntity> entities, int i) {
        this.waveIndex = i;
        rewards.forEach(reward -> {
            if (reward instanceof Reward.ChancedReward chancedReward) {
                addReward(chancedReward.reward(), chancedReward.chance());
            } else {
                addReward(reward, 1);
            }
        });
        entities.forEach(entity -> {
            if (entity instanceof WaveEntity.StandardWaveEntity standardEntity && entity instanceof TypeAccessor typeAccessor) {
                this.entityWithCounts.add(new EntityWithCount(typeAccessor.getType(), standardEntity.getCount()));
            }
        });
    }

    private void addReward(Reward reward, float chance) {
        if (reward instanceof Reward.StackReward stackReward) {
            this.addStackReward(stackReward, chance);
        } else if (reward instanceof Reward.StackListReward stackListReward) {
            this.addStackListReward(stackListReward, chance);
        } else if (reward instanceof Reward.EntityLootReward entityReward) {
            this.addEntityReward(entityReward, chance);
        } else if (reward instanceof Reward.LootTableReward lootTableReward) {
            this.addLootTableReward(lootTableReward, chance);
        }
    }

    private void addLootTableReward(Reward.LootTableReward lootTableReward, float chance) {
        lootTableRewards.add(new LootTableReward(lootTableReward.table(), lootTableReward.rolls(), chance));
    }

    private void addStackListReward(Reward.StackListReward stackListReward, float chance) {
        stacks.addAll(stackListReward.stacks().stream().map(i -> new ItemStackReward(i, chance)).toList());
    }

    private void addStackReward(Reward.StackReward reward, float chance) {
        stacks.add(new ItemStackReward(reward.stack(), chance));
    }

    private void addEntityReward(Reward.EntityLootReward entityReward, float chance) {
        entityIds.add(new EntityReward(entityReward.type(), entityReward.rolls(), chance));
    }

    public record EntityReward(EntityType<?> type, int rolls, float chance) { }

    public record LootTableReward(ResourceLocation lootTableId, int rolls, float chance) { }

    public record ItemStackReward(ItemStack stack, float chance) { }

    public record EntityWithCount(EntityType<?> type, int count) { }
}
