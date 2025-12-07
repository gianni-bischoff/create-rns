package com.bmaster.createrns.mining.recipe;

import com.bmaster.createrns.RNSRecipeTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ModernMiningRecipe extends MiningRecipe {
    public ModernMiningRecipe(Block depositBlock, Item yield) {
        super(depositBlock, yield);
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RNSRecipeTypes.MODERN_MINING_TYPE.get();
    }

    public static class Serializer extends MiningRecipe.Serializer<ModernMiningRecipe> {
        public static ModernMiningRecipe.Serializer INSTANCE = new ModernMiningRecipe.Serializer();

        private Serializer() {
            super(ModernMiningRecipe::new);
        }
    }
}
