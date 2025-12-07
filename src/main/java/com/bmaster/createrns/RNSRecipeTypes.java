package com.bmaster.createrns;

import com.bmaster.createrns.mining.MiningLevel;
import com.bmaster.createrns.mining.recipe.AdvancedMiningRecipe;
import com.bmaster.createrns.mining.recipe.BasicMiningRecipe;
import com.bmaster.createrns.mining.recipe.ModernMiningRecipe;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class RNSRecipeTypes {
    // Basic mining recipe
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<BasicMiningRecipe>> BASIC_MINING_SERIALIZER =
            CreateRNS.REGISTRATE.simple(MiningLevel.BASIC.getRecipeID(), Registries.RECIPE_SERIALIZER,
                    () -> BasicMiningRecipe.Serializer.INSTANCE);
    public static final RegistryEntry<RecipeType<?>, RecipeType<BasicMiningRecipe>> BASIC_MINING_TYPE = CreateRNS.REGISTRATE.simple(
            MiningLevel.BASIC.getRecipeID(), Registries.RECIPE_TYPE, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return CreateRNS.MOD_ID + ":" + MiningLevel.BASIC.getRecipeID();
                }
            });

    // Advanced mining recipe
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<AdvancedMiningRecipe>> ADVANCED_MINING_SERIALIZER =
            CreateRNS.REGISTRATE.simple(MiningLevel.ADVANCED.getRecipeID(), Registries.RECIPE_SERIALIZER,
                    () -> AdvancedMiningRecipe.Serializer.INSTANCE);
    public static final RegistryEntry<RecipeType<?>, RecipeType<AdvancedMiningRecipe>> ADVANCED_MINING_TYPE = CreateRNS.REGISTRATE.simple(
            MiningLevel.ADVANCED.getRecipeID(), Registries.RECIPE_TYPE, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return CreateRNS.MOD_ID + ":" + MiningLevel.ADVANCED.getRecipeID();
                }
            });

    // Modern mining recipe
    public static final RegistryEntry<RecipeSerializer<?>, RecipeSerializer<ModernMiningRecipe>> MODERN_MINING_SERIALIZER =
            CreateRNS.REGISTRATE.simple(MiningLevel.MODERN.getRecipeID(), Registries.RECIPE_SERIALIZER,
                    () -> ModernMiningRecipe.Serializer.INSTANCE);
    public static final RegistryEntry<RecipeType<?>, RecipeType<ModernMiningRecipe>> MODERN_MINING_TYPE = CreateRNS.REGISTRATE.simple(
            MiningLevel.MODERN.getRecipeID(), Registries.RECIPE_TYPE, () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return CreateRNS.MOD_ID + ":" + MiningLevel.MODERN.getRecipeID();
                }
            });


    public static void register() {
    }
}
