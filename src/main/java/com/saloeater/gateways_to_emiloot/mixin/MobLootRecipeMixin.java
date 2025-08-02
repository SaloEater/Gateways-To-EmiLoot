package com.saloeater.gateways_to_emiloot.mixin;

import com.saloeater.gateways_to_emiloot.ClickableTextWidget;
import com.saloeater.gateways_to_emiloot.EmiIntegration;
import com.saloeater.gateways_to_emiloot.LootEmiStack;
import com.saloeater.gateways_to_emiloot.MobEmiStack;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import fzzyhmstrs.emi_loot.EMILoot;
import fzzyhmstrs.emi_loot.client.ClientChestLootTable;
import fzzyhmstrs.emi_loot.emi.ChestLootRecipe;
import fzzyhmstrs.emi_loot.emi.MobLootRecipe;
import fzzyhmstrs.emi_loot.util.TrimmedTitle;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = MobLootRecipe.class, remap = false)
public abstract class MobLootRecipeMixin {
    @Final
    @Shadow
    private ResourceLocation lootId;

    @Final
    @Shadow
    private TrimmedTitle name;

    @Final
    @Shadow
    private @Nullable EmiStack egg;

    @Inject(
        method = "getInputs",
        at = @At("RETURN"),
            cancellable = true
    )
    public void getInputs(CallbackInfoReturnable<List<EmiIngredient>> cir) {
        var oldReturn = cir.getReturnValue();
        List<EmiIngredient> newReturn = new ArrayList<>(oldReturn);
        newReturn.add(new MobEmiStack(lootId.toString()));
        cir.setReturnValue(newReturn);
    }

    @Inject(
        method = "addWidgets",
        at = @At("RETURN")
    )
    public void addWidgets(WidgetHolder widgets, CallbackInfo ci) {
        int x = 49;
        if (this.egg == null) {
            x = 30;
        }

        MobEmiStack input = new MobEmiStack(lootId.toString());
        var recipes = EmiApi.getRecipeManager().getRecipesByOutput(input);
        if (recipes.isEmpty()) {
            return;
        }
        ClickableTextWidget textWidget = new ClickableTextWidget(this.name.rawTitle().plainCopy().withStyle(ChatFormatting.BLUE, ChatFormatting.UNDERLINE).getVisualOrderText(), x, 0, 4210752, false, (double mouseX, double mouseY, int button) -> {
            if (button == 0) {
                EmiApi.displayRecipes(input);
            }
        });
        widgets.add(textWidget);
    }
}
