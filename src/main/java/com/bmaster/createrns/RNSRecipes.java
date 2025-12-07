package com.bmaster.createrns;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.api.data.recipe.WashingRecipeGen;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public class RNSRecipes {
    static {
        CreateRNS.REGISTRATE.addDataGenerator(ProviderType.RECIPE, prov -> {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.REDSTONE)
                    .requires(RNSContent.REDSTONE_SMALL_DUST.get(), 9)
                    .unlockedBy("has_item", RegistrateRecipeProvider.has(RNSContent.REDSTONE_SMALL_DUST))
                    .save(prov, ResourceLocation.fromNamespaceAndPath(CreateRNS.MOD_ID, "redstone_from_small_dust"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.COAL)
                    .requires(RNSContent.COAL_CHUNK.get(), 9)
                    .unlockedBy("has_item", RegistrateRecipeProvider.has(RNSContent.COAL_CHUNK))
                    .save(prov, ResourceLocation.fromNamespaceAndPath(CreateRNS.MOD_ID, "coal_from_chunk"));
        });
    }

    public static class Washing extends WashingRecipeGen {
        GeneratedRecipe IMPURE_IRON_ORE = fromImpure("impure_iron_ore", RNSContent.IMPURE_IRON_ORE, Items.IRON_NUGGET);
        GeneratedRecipe IMPURE_COPPER_ORE = fromImpure("impure_copper_ore", RNSContent.IMPURE_COPPER_ORE, AllItems.COPPER_NUGGET);
        GeneratedRecipe IMPURE_ZINC_ORE = fromImpure("impure_zinc_ore", RNSContent.IMPURE_ZINC_ORE, AllItems.ZINC_NUGGET);
        GeneratedRecipe IMPURE_GOLD_ORE = fromImpure("impure_gold_ore", RNSContent.IMPURE_GOLD_ORE, Items.GOLD_NUGGET);
        GeneratedRecipe IMPURE_REDSTONE_DUST = fromImpure("impure_redstone_dust", RNSContent.IMPURE_REDSTONE_DUST, RNSContent.REDSTONE_SMALL_DUST);
        GeneratedRecipe IMPURE_COAL_ORE = fromImpure("impure_coal_ore", RNSContent.IMPURE_COAL_ORE, RNSContent.COAL_CHUNK);

        public Washing(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
            super(output, provider, CreateRNS.MOD_ID);
        }

        private GeneratedRecipe fromImpure(String name, ItemLike in, ItemLike out) {
            return create(name, b -> b.require(in)
                    .output(Items.COBBLESTONE)
                    .output(0.5f, out));
        }
    }

    public static void register() {
    }
}
