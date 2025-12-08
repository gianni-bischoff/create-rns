package com.bmaster.createrns.mining.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class MiningRecipe implements Recipe<SingleRecipeInput> {
    private final Block depositBlock;
    private final Item yield;
    private final float yieldMultiplier;

    public MiningRecipe(Block depositBlock, Item yield, float yieldMultiplier) {
        this.depositBlock = depositBlock;
        this.yield = yield;
        this.yieldMultiplier = yieldMultiplier;
    }

    public Block getDepositBlock() {
        return depositBlock;
    }

    public Item getYield() {
        return yield;
    }

    public float getYieldMultiplier() {
        return yieldMultiplier;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return new ItemStack(yield);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(depositBlock));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(SingleRecipeInput singleRecipeInput, Level level) {
        return singleRecipeInput.item().is(getDepositBlock().asItem());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput singleRecipeInput, HolderLookup.Provider provider) {
        return new ItemStack(getYield());
    }

    @FunctionalInterface
    public interface Factory<R extends MiningRecipe> {
        R create(Block depositBlock, Item yield, float yieldMultiplier);
    }

    public static abstract class Serializer<R extends MiningRecipe> implements RecipeSerializer<R> {
        public final MapCodec<R> CODEC;
        public final StreamCodec<RegistryFriendlyByteBuf, R> STREAM_CODEC;
        private final Factory<R> recipeFactory;

        public Serializer(Factory<R> recipeFactory) {
            this.recipeFactory = recipeFactory;
            CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("deposit_block").forGetter(MiningRecipe::getDepositBlock),
                            BuiltInRegistries.ITEM.byNameCodec().fieldOf("yield").forGetter(MiningRecipe::getYield),
                            Codec.FLOAT.optionalFieldOf("yield_multiplier", 1.0f).forGetter(MiningRecipe::getYieldMultiplier))
                    .apply(instance, recipeFactory::create));
            STREAM_CODEC = StreamCodec.of(this::toNetwork, this::fromNetwork);
        }

        public void toNetwork(RegistryFriendlyByteBuf buffer, R recipe) {
            ByteBufCodecs.registry(Registries.BLOCK).encode(buffer, recipe.getDepositBlock());
            ByteBufCodecs.registry(Registries.ITEM).encode(buffer, recipe.getYield());
            ByteBufCodecs.FLOAT.encode(buffer, recipe.getYieldMultiplier());
        }

        public R fromNetwork(RegistryFriendlyByteBuf buffer) {
            return recipeFactory.create(
                    ByteBufCodecs.registry(Registries.BLOCK).decode(buffer),
                    ByteBufCodecs.registry(Registries.ITEM).decode(buffer),
                    ByteBufCodecs.FLOAT.decode(buffer)
            );
        }

        @Override
        public MapCodec<R> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
