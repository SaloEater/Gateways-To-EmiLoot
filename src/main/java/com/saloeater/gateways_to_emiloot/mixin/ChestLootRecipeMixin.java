package com.saloeater.gateways_to_emiloot.mixin;

import com.saloeater.gateways_to_emiloot.ClickableSlotWidget;
import com.saloeater.gateways_to_emiloot.ClickableTextWidget;
import com.saloeater.gateways_to_emiloot.EmiIntegration;
import com.saloeater.gateways_to_emiloot.LootEmiStack;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.EMILootAgnos;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import fzzyhmstrs.emi_loot.emi.ChestLootRecipe;
import fzzyhmstrs.emi_loot.emi.EmiClientPlugin;
import fzzyhmstrs.emi_loot.util.LText;
import fzzyhmstrs.emi_loot.util.TrimmedTitle;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ChestLootRecipe.class, remap = false)
public abstract class ChestLootRecipeMixin {
    @Final
    @Shadow
    private ClientChestLootTable loot;

    @Final
    @Shadow
    private TrimmedTitle title;

    @Inject(
        method = "getInputs",
        at = @At("HEAD"),
            cancellable = true
    )
    public void getInputs(CallbackInfoReturnable<List<EmiIngredient>> cir) {
        List<EmiIngredient> lootTables = new ArrayList<>();
        lootTables.add(new LootEmiStack(loot.getId().toString()));
        cir.setReturnValue(lootTables);
    }

    @Inject(
        method = "addWidgets",
        at = @At("RETURN")
    )
    public void addWidgets(WidgetHolder widgets, CallbackInfo ci) {
        LootEmiStack input = new LootEmiStack(loot.getId().toString());
        var recipes = EmiApi.getRecipeManager().getRecipesByOutput(input);
        if  (recipes.isEmpty()) {
            return;
        }
        ClickableTextWidget textWidget = new ClickableTextWidget(this.title.rawTitle().plainCopy().withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE).getVisualOrderText(), 1, 0, 0x404040, false, (double mouseX, double mouseY, int button) -> {
            if (button == 0) {
                EmiApi.displayRecipes(input);
            }
        });
        widgets.add(textWidget);
    }
}
