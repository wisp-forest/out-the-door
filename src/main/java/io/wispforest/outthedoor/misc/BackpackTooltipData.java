package io.wispforest.outthedoor.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.util.collection.DefaultedList;

public record BackpackTooltipData(DefaultedList<ItemStack> items) implements TooltipData {}
