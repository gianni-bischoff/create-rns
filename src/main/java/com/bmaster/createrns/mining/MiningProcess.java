package com.bmaster.createrns.mining;

import com.bmaster.createrns.CreateRNS;
import com.bmaster.createrns.RNSTags;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MiningProcess {
    public final MiningLevel miningLevel;
    public final Set<SingleTypeProcess> innerProcesses = new ObjectOpenHashSet<>();

    public MiningProcess(Level l, MiningLevel ml, Set<BlockPos> depositBlocks, int baseProgress, float minerMultiplier) {
        miningLevel = ml;
        setYields(l, depositBlocks, baseProgress, minerMultiplier);
    }

    public boolean isPossible() {
        return !innerProcesses.isEmpty();
    }

    public void advance(int by) {
        if (!isPossible()) return;
        innerProcesses.forEach(p -> p.advance(by));
    }

    public Set<ItemStack> collect() {
        return innerProcesses.stream()
                .map(SingleTypeProcess::collect)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void setYields(Level l, Set<BlockPos> depositBlocks, int baseProgress, float minerMultiplier) {
        var yieldCounts = depositBlocks.stream()
                .map(bp -> l.getBlockState(bp).getBlock())
                .filter(db -> db.defaultBlockState().is(RNSTags.Block.DEPOSIT_BLOCKS))
                .map(db -> MiningRecipeLookup.getYield(l, miningLevel, db))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        innerProcesses.clear();
        for (var e : yieldCounts.entrySet()) {
            var yieldInfo = e.getKey();
            var depBlockCount = e.getValue().intValue();
            // Apply both deposit multiplier and miner multiplier
            // Lower progress requirement = faster completion = higher yield
            float combinedMultiplier = yieldInfo.multiplier * minerMultiplier;
            int adjustedProgress = (int) (baseProgress / (depBlockCount * combinedMultiplier));
            innerProcesses.add(new SingleTypeProcess(yieldInfo.item, combinedMultiplier, adjustedProgress));
        }
    }

    public CompoundTag getProgressAsNBT() {
        CompoundTag mpTag = new CompoundTag();

        ListTag stpTags = new ListTag();
        innerProcesses.stream()
                .map(SingleTypeProcess::getProgressAsNBT)
                .filter(Objects::nonNull)
                .forEach(stpTags::add);
        mpTag.put("PerYieldProgress", stpTags);

        return mpTag;
    }

    public void setProgressFromNBT(CompoundTag nbt) {
        Object2ObjectOpenHashMap<Item, SingleTypeProcess> yieldToProcess = innerProcesses.stream()
                .collect(Collectors.toMap(p -> p.yield, p -> p,
                        (o, n) -> n, Object2ObjectOpenHashMap::new));

        ListTag yieldProgressTags = nbt.getList("PerYieldProgress", Tag.TAG_COMPOUND);
        for (var stp : yieldProgressTags) {
            var yieldStr = ((CompoundTag) stp).getString("Yield");
            var yieldRL = ResourceLocation.tryParse(yieldStr);
            if (yieldRL == null) {
                CreateRNS.LOGGER.error("Could not parse resource location '{}' when deserializing mining process", yieldStr);
                continue;
            }
            var yield = BuiltInRegistries.ITEM.getOptional(yieldRL).orElse(null);
            if (yield == null) {
                CreateRNS.LOGGER.error("Unknown item '{}' encountered when deserializing mining process", yieldStr);
                continue;
            }
            yieldToProcess.get(yield).setProgressFromNBT((CompoundTag) stp);
        }
    }

    public static class SingleTypeProcess {
        public final Item yield;
        public final float yieldMultiplier;
        public int maxProgress;
        public int progress;

        public SingleTypeProcess(Item yield, float yieldMultiplier, int maxProgress) {
            this.yield = yield;
            this.yieldMultiplier = yieldMultiplier;
            this.maxProgress = maxProgress;
            this.progress = 0;
        }

        public void advance(int by) {
            assert by > 0;
            progress += by;
        }

        public @Nullable ItemStack collect() {
            if (progress < maxProgress) return null;
            progress = progress - maxProgress; // Keep the extra progress
            // Calculate how many items to yield based on multiplier
            int count = (int) Math.floor(yieldMultiplier);
            return new ItemStack(yield, count);
        }

        public @Nullable CompoundTag getProgressAsNBT() {
            var yieldRL = BuiltInRegistries.ITEM.getKeyOrNull(yield);
            if (yieldRL == null) return null;

            CompoundTag stpTag = new CompoundTag();
            stpTag.putString("Yield", yieldRL.toString());
            stpTag.putInt("Progress", progress);
            stpTag.putInt("MaxProgress", maxProgress);
            stpTag.putFloat("YieldMultiplier", yieldMultiplier);

            return stpTag;
        }

        /// Assumes that the yield of the tag matches the yield of this instance
        public void setProgressFromNBT(CompoundTag nbt) {
            this.progress = nbt.getInt("Progress");
            this.maxProgress = nbt.getInt("MaxProgress");
            // YieldMultiplier is not loaded from NBT since it's already set from recipe
        }
    }
}
