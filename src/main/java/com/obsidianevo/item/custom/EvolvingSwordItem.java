package com.obsidianevo.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EvolvingSwordItem extends SwordItem {
    public static final String KILLS_KEY = "KillsCount";
    public static final String DAMAGE_DEALT_KEY = "DamageDealt";

    public EvolvingSwordItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties.durability(100000));
    }

    public static int getKills(ItemStack stack) {
        if (!stack.hasTag()) return 0;
        return stack.getTag().getInt(KILLS_KEY);
    }

    public static void addKill(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(KILLS_KEY, tag.getInt(KILLS_KEY) + 1);
    }

    public static float getDamageDealt(ItemStack stack) {
        if (!stack.hasTag()) return 0.0f;
        return stack.getTag().getFloat(DAMAGE_DEALT_KEY);
    }

    public static void addDamageDealt(ItemStack stack, float amount) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat(DAMAGE_DEALT_KEY, tag.getFloat(DAMAGE_DEALT_KEY) + amount);
    }

    public static float getBonusDamage(ItemStack stack) {
        return (getKills(stack) * 0.5f) + (getDamageDealt(stack) * 0.02f);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        if (!pAttacker.level.isClientSide()) {
            float bonus = getBonusDamage(pStack);
            if (bonus > 0) {
                pTarget.hurt(net.minecraft.world.damagesource.DamageSource.mobAttack(pAttacker), bonus);
            }
            addDamageDealt(pStack, 8.0f + bonus);
            if (pTarget.isDeadOrDying()) {
                addKill(pStack);
            }
        }
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        int kills = getKills(pStack);
        float dealt = getDamageDealt(pStack);
        float bonus = getBonusDamage(pStack);

        pTooltipComponents.add(Component.literal("Almas Cosechadas: " + kills).withStyle(ChatFormatting.DARK_PURPLE));
        pTooltipComponents.add(Component.literal("Daño Acumulado: " + String.format("%.1f", dealt)).withStyle(ChatFormatting.AQUA));
        pTooltipComponents.add(Component.literal("Daño Evolutivo Extra: +" + String.format("%.2f", bonus)).withStyle(ChatFormatting.RED));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
