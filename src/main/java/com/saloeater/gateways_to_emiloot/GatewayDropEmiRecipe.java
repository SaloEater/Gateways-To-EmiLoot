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
import fzzyhmstrs.emi_loot.client.ClientResourceData;
import fzzyhmstrs.emi_loot.emi.EmiClientPlugin;
import fzzyhmstrs.emi_loot.util.BlockStateEmiStack;
import fzzyhmstrs.emi_loot.util.EntityEmiStack;
import fzzyhmstrs.emi_loot.util.LText;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
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
    List<EmiStack> stacks;
    List<EmiStack> outputs;
    List<Float> chances;
    List<GatewayDropRecipe.EntityReward> entityIds;
    List<GatewayDropRecipe.LootTableReward> lootTables;
    List<GatewayDropRecipe.EntityWithCount> spawns;
    Component name;

    int titleHeight = 11;
    int slotSize = 18;
    int itemColumns = 5;
    int slotsStartY = 12;
    int entityColumns = 2;
    int entitiesX = 95;
    int lootTableColumns = 2;
    int spawnColumns = 8;
    int height;

    public GatewayDropEmiRecipe(ItemStack gatePearl, ResourceLocation resourceLocation, GatewayDropRecipe recipe, int categoryIndex) {
        this.categoryIndex = categoryIndex;
        this.path = resourceLocation.getPath();
        this.waveIndex = recipe.waveIndex;
        this.gatePearl = gatePearl.copy();
        stacks = new ArrayList<>();
        chances = new ArrayList<>();
        recipe.stacks.forEach(stack -> {
            stacks.add(EmiStack.of(stack.stack()));
            chances.add(stack.chance());
        });
        outputs = new ArrayList<>(stacks);
        this.name = gatePearl.getHoverName();
        this.entityIds = recipe.entityIds;
        this.entityIds.forEach(entityId -> {
            outputs.add(new MobEmiStack(toEmiMobLootFormat(ForgeRegistries.ENTITY_TYPES.getKey(entityId.type()))));
        });
        this.lootTables = recipe.lootTableRewards;
        this.lootTables.forEach(lootTable -> {
            outputs.add(new LootEmiStack(lootTable.lootTableId().toString()));
        });
        spawns = recipe.entityWithCounts;
        spawns.forEach(entityWithCount -> {
            outputs.add(new MobEmiStack(toEmiMobLootFormat(ForgeRegistries.ENTITY_TYPES.getKey(entityWithCount.type()))));
        });

        height = titleHeight + Math.max(stacks.size() / itemColumns, 2 * titleHeight + lootTables.size() / lootTableColumns + entityIds.size() / entityColumns) * slotSize;
    }

    private String toEmiMobLootFormat(@Nullable ResourceLocation key) {
        if (key == null) {
            return "";
        }
        return ResourceLocation.fromNamespaceAndPath(key.getNamespace(), "entities/" + key.getPath()).toString();
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
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return height;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var waveText = getWaveText();
        widgets.addText(waveText, centeredTextX(waveText), 0, 0x404040, false);

        int row = 0;
        int column = 0;
        for (EmiStack stack : stacks) {
            if (column == itemColumns) {
                row++;
                column = 0;
            }
            int x = column * slotSize;
            int y = slotsStartY + row * slotSize;
            var slot = new SlotWidget(stack, x, y);
            widgets.add(slot.appendTooltip(LText.translatable("emi_loot.percentage", trimFloatString(chances.get(column)))));
            column++;
        }

        int y = slotsStartY;
        if (!entityIds.isEmpty()) {
            widgets.addText(getFromMobText(), entitiesX, slotsStartY, 0x404040, false);
            y +=  titleHeight;
            row = 0;
            column = 0;
            for (var entityRewards : entityIds) {
                if (column == entityColumns) {
                    row++;
                    column = 0;
                    y += slotSize;
                }
                int x = entitiesX + column * slotSize;
                var entityType = entityRewards.type();
                EmiIngredient entityStack = createEntitySlot(entityType);
                ClickableSlotWidget slot = new ClickableSlotWidget(entityStack, x, y, (double mouseX, double mouseY, int button) -> {
                    if (button == 1) {
                        showMobLoot(entityType);
                    }
                });
                widgets.add(slot);
                column++;
            }
            y += slotSize;
        }

        if (!lootTables.isEmpty()) {
            widgets.addText(getFromLootText(), entitiesX, y, 0x404040, false);
            y += titleHeight;
            row = 0;
            column = 0;

            for (var lootTable : lootTables) {
                if (column == lootTableColumns) {
                    row++;
                    column = 0;
                    y += slotSize;
                }
                int x = entitiesX+ column * slotSize;
                var chestSlot = new ItemStack(Items.CHEST);
                chestSlot.setHoverName(getLootTableName(lootTable));
                EmiIngredient slot = BlockStateEmiStack.of(chestSlot);
                ClickableSlotWidget slotWidget = new ClickableSlotWidget(slot, x, y, (double mouseX, double mouseY, int button) -> {
                    if (button == 1) {
                        showChestLoot(lootTable.lootTableId());
                    }
                });
                widgets.add(slotWidget);
                column++;
            }
            y += slotSize;
        }

        if (!spawns.isEmpty()) {
            y = (int) Math.max(y, slotsStartY + Math.ceil((double) stacks.size() / itemColumns));
            widgets.addText(Component.translatable("gateways_to_emiloot.spawns"), 0, y, 0x404040, false);
            y += titleHeight;
            row = 0;
            column = 0;

            for (var entityWithCount : spawns) {
                if (column == spawnColumns) {
                    row++;
                    column = 0;
                    y += slotSize;
                }
                int x = column * slotSize;
                EmiIngredient entityStack = createEntitySlot(entityWithCount.type());
                ClickableSlotWidget slot = new ClickableSlotWidget(entityStack, x, y, (double mouseX, double mouseY, int button) -> {
                    if (button == 1) {
                        showMobLoot(entityWithCount.type());
                    }
                });
                widgets.add(slot);
                widgets.addText(Component.literal(String.valueOf(entityWithCount.count())), x + 1, y + 1, 0xBD2A0A, false);
                column++;
            }
        }
    }

    private static void showChestLoot(ResourceLocation id) {
        var emiRecipe = EmiApi.getRecipeManager().getRecipe(ResourceLocation.fromNamespaceAndPath(EMILoot.MOD_ID, "/" + EmiClientPlugin.CHEST_CATEGORY.getId().getPath() + "/" + id.getNamespace() + "/" + id.getPath()));
        if (emiRecipe == null) return;
        EmiApi.displayRecipe(emiRecipe);
    }

    private static void showMobLoot(EntityType<?> type) {
        var id = ForgeRegistries.ENTITY_TYPES.getKey(type);
        var emiRecipe = EmiApi.getRecipeManager().getRecipe(ResourceLocation.fromNamespaceAndPath(EMILoot.MOD_ID, "/" + EmiClientPlugin.MOB_CATEGORY.getId().getPath() + "/" + id.getNamespace() + "/entities/" + id.getPath()));
        if (emiRecipe == null) return;
        EmiApi.displayRecipe(emiRecipe);
    }

    private EmiIngredient createEntitySlot(EntityType<?> type) {
        Minecraft client = Minecraft.getInstance();
        Entity entity = type.create(client.level);
        EmiStack inputStack;
        if (entity != null) {
            var box = entity.getBoundingBox();
            double len = box.getSize();
            if (len > 1.05) {
                len = (len + Math.sqrt(len))/2.0;
            }
            if (entity instanceof Slime) {
                ((Slime)entity).setSize(5, false);
            }
            double scale = 1.05 / len * 8;
            if (ClientResourceData.MOB_SCALES.containsKey(type)) {
                scale *= ClientResourceData.MOB_SCALES.getOrDefault(type, 1.0f);
            }
            inputStack = EntityEmiStack.ofScaled(entity, scale);
        } else {
            inputStack = EmiStack.EMPTY;
        }
        return inputStack;
    }

    private MutableComponent getLootTableName(GatewayDropRecipe.LootTableReward lootTable) {
        ResourceLocation id = lootTable.lootTableId();
        String key = "emi_loot.chest." + id.toString();
        MutableComponent rawTitle;
        if (!I18n.exists(key)) {
            StringBuilder chestName = new StringBuilder();
            String[] chestPathTokens = id.getPath().split("[/_]");

            for(String str : chestPathTokens) {
                if (!LText.tablePrefixes.contains(str)) {
                    if (!chestName.isEmpty()) {
                        chestName.append(" ");
                    }

                    if (str.length() <= 1) {
                        chestName.append(str);
                    } else {
                        chestName.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
                    }
                }
            }

            if (EMILootAgnos.isModLoaded(id.getNamespace())) {
                rawTitle = LText.translatable("emi_loot.chest.unknown_chest", chestName.toString());
            } else {
                Component unknown = LText.translatable("emi_loot.chest.unknown");
                rawTitle = LText.translatable("emi_loot.chest.unknown_chest", LText.literal(chestName.toString()).append(" ").append(unknown));
            }
        } else {
            rawTitle = LText.translatable(key);
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

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
