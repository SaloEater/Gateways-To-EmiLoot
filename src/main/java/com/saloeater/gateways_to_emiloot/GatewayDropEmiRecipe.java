package com.saloeater.gateways_to_emiloot;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import fzzyhmstrs.emi_loot.emi.EmiClientPlugin;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.resources.language.I18n;

import java.util.ArrayList;
import java.util.List;

import static fzzyhmstrs.emi_loot.util.FloatTrimmer.trimFloatString;

public class GatewayDropEmiRecipe implements EmiRecipe {
    int categoryIndex;
    String path;
    int waveIndex;
    ItemStack gatePearl;
    List<EmiStack> outputStacks;
    List<Float> chances;
    List<GatewayDropRecipe.EntityReward> entityIds;
    List<GatewayDropRecipe.LootTableReward> lootTables;
    Component name;

    int titleHeight = 11;
    int slotSize = 18;
    int maxRows = 8;
    int maxColumns = 5;
    int slotsStartY = 12;
    int maxEntities = 5;
    int entitiesX = 95;
    int maxLootTables = 5;

    public GatewayDropEmiRecipe(ItemStack gatePearl, ResourceLocation resourceLocation, GatewayDropRecipe recipe, int categoryIndex) {
        this.categoryIndex = categoryIndex;
        this.path = resourceLocation.getPath();
        this.waveIndex = recipe.waveIndex;
        this.gatePearl = gatePearl.copy();
        outputStacks = new ArrayList<>();
        chances = new ArrayList<>();
        recipe.stacks.forEach(stack -> {
            outputStacks.add(EmiStack.of(stack.stack()));
            chances.add(stack.chance());
        });
        this.name = gatePearl.getHoverName();
        this.entityIds = recipe.entityIds;
        this.lootTables = recipe.lootTableRewards;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiIntegration.categories.get(categoryIndex);
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return ResourceLocation.tryBuild(GatewaysToEmiLoot.MODID, "/gateway_drop/" + path);
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(EmiIngredient.of(Ingredient.of(gatePearl)));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputStacks;
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return titleHeight + slotSize * maxRows;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var waveText = getWaveText();
        widgets.addText(waveText, centeredTextX(waveText), 0, 0x404040, false);
        var maxSlots = Math.min(outputStacks.size(), maxRows * maxColumns);
        for (int i = 0; i < maxSlots; i++) {
            int x = (i % maxColumns) * slotSize;
            int y = slotsStartY + (i / maxColumns) * slotSize;
            String fTrim = trimFloatString(chances.get(i));
            var slot = new SlotWidget(outputStacks.get(i), x, y);
            widgets.add(slot.appendTooltip(LText.translatable("emi_loot.percentage", fTrim)));
        }

        int y = slotsStartY;
        var maxEntities = Math.min(entityIds.size(), this.maxEntities);
        if (!entityIds.isEmpty()) {
            widgets.addText(getFromMobText(), entitiesX, slotsStartY, 0x404040, false);
            y +=  titleHeight;
            for (int i = 0; i < maxEntities; i++) {
                int x = entitiesX;
                var entity = entityIds.get(i);
                ClickableTextWidget textWidget = new ClickableTextWidget(Component.translatable(entity.type().getDescriptionId()).withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE), x, y, 0x404040, false, (double mouseX, double mouseY, int button) -> {
                    var id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type());
                    var emiRecipe = EmiApi.getRecipeManager().getRecipe(ResourceLocation.fromNamespaceAndPath(EMILoot.MOD_ID, "/" + EmiClientPlugin.MOB_CATEGORY.getId().getPath() + "/" + id.getNamespace() + "/entities/" + id.getPath()));
                    if (emiRecipe == null) return;
                    EmiApi.displayRecipe(emiRecipe);
                });
                widgets.add(textWidget);
                y += titleHeight;
            }
        }

        var maxLoot = Math.min(lootTables.size(), this.maxLootTables);
        if (!lootTables.isEmpty()) {
            widgets.addText(getFromLootText(), entitiesX, y, 0x404040, false);
            y += titleHeight;
            for (int i = 0; i < maxLoot; i++) {
                int x = entitiesX;
                var lootTable = lootTables.get(i);
                ClickableTextWidget textWidget = new ClickableTextWidget(getLootTableName(lootTable).withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE), x, y, 0x404040, false, (double mouseX, double mouseY, int button) -> {
                    var id = lootTable.lootTableId();
                    var emiRecipe = EmiApi.getRecipeManager().getRecipe(ResourceLocation.fromNamespaceAndPath(EMILoot.MOD_ID, "/" + EmiClientPlugin.LOOT_CATEGORY.getId().getPath() + "/" + id.getNamespace() + "/" + id.getPath()));
                    if (emiRecipe == null) return;
                    EmiApi.displayRecipe(emiRecipe);
                });
                widgets.add(textWidget);
                y += titleHeight;
            }
        }
    }

    private MutableComponent getLootTableName(GatewayDropRecipe.LootTableReward lootTable) {
        ResourceLocation id = lootTable.lootTableId();
        String key = "emi_loot.chest." + id.toString();
        MutableComponent text = LText.translatable(key);
        MutableComponent rawTitle;
        if (!I18n.exists(key)) {
            if(EMILootAgnos.isModLoaded(id.getNamespace())) {
                String modName = EMILootAgnos.getModName(id.getNamespace());
                rawTitle = LText.translatable("emi_loot.chest.unknown_chest", modName);
            } else {
                var unknown = LText.translatable("emi_loot.chest.unknown");
                rawTitle = LText.translatable("emi_loot.chest.unknown_chest", unknown.getString());
            }
        } else {
            rawTitle = text;
        }

        return rawTitle;
    }

    private Component getFromMobText() {
        return Component.translatable("gateways_to_emiloot.from_mob");
    }

    private Component getFromLootText() {
        return Component.translatable("gateways_to_emiloot.from_loot");
    }

    private @NotNull MutableComponent getWaveText() {
        if (waveIndex == GatewayDropRecipe.FINAL) {
            return Component.translatable("gateways_to_emiloot.final");
        }
        return Component.translatable("gateways_to_emiloot.wave", waveIndex);
    }

    private int centeredTextX(Component text, int startX, int endX) {
        var textWidth = Minecraft.getInstance().font.width(text);
        return startX + (endX - startX - textWidth) / 2;
    }

    private int centeredTextX(Component text, int startX) {
        return centeredTextX(text, startX, getDisplayWidth());
    }

    private int centeredTextX(Component text) {
        return centeredTextX(text,  0, getDisplayWidth());
    }
}
