package com.obsidianevo.item;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraft.tags.BlockTags;

public class ModToolTiers {
    public static final Tier OBSIDIAN_EVO = new ForgeTier(
            4,
            100000,
            9.0f,
            4.0f,
            15,
            BlockTags.NEEDS_DIAMOND_TOOL,
            () -> Ingredient.of(Items.OBSIDIAN)
    );
}
