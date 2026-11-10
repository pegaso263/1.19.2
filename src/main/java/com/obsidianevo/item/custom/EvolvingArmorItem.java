package com.obsidianevo.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EvolvingArmorItem extends ArmorItem {
    public static final String DAMAGE_ABSORBED_KEY = "DamageAbsorbed";
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("d8b9e6a2-4c1f-4b9a-9e8d-1a2b3c4d5e6f");

    public EvolvingArmorItem(ArmorMaterial pMaterial, EquipmentSlot pSlot, Properties pProperties) {
        super(pMaterial, pSlot, pProperties.durability(100000));
    }

    public static float getDamageAbsorbed(ItemStack stack) {
        if (!stack.hasTag()) return 0.0f;
        return stack.getTag().getFloat(DAMAGE_ABSORBED_KEY);
    }

    public static void addDamageAbsorbed(ItemStack stack, float amount) {
        CompoundTag tag = stack.getOrCreateTag();
        float current = tag.getFloat(DAMAGE_ABSORBED_KEY);
        tag.putFloat(DAMAGE_ABSORBED_KEY, current + amount);
    }

    public static float getPieceMitigationBonus(ItemStack stack) {
        return getDamageAbsorbed(stack) * 0.01f;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide() && entity instanceof Player player) {
            handleArmorFeatures(player);
        }
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);
        if (!level.isClientSide()) {
            handleArmorFeatures(player);
        }
    }

    private void handleArmorFeatures(Player player) {
        if (this.getSlot() == EquipmentSlot.HEAD) {
            if (hasFullSet(player)) {
                applySetEffects(player);
                applyHealthAndFlight(player);
            } else {
                removeHealthAndFlight(player);
            }
        }
    }

    private boolean hasFullSet(Player player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);

        return head.getItem() instanceof EvolvingArmorItem &&
               chest.getItem() instanceof EvolvingArmorItem &&
               legs.getItem() instanceof EvolvingArmorItem &&
               feet.getItem() instanceof EvolvingArmorItem;
    }

    private void applySetEffects(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        float mitigation = getPieceMitigationBonus(chest);

        int amplifier = 0;
        if (mitigation >= 80.0f) {
            amplifier = 3;
        } else if (mitigation >= 40.0f) {
            amplifier = 2;
        } else if (mitigation >= 11.0f) {
            amplifier = 1;
        }

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 220, Math.min(amplifier, 1), false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 220, amplifier, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 220, 0, false, false, true));

        MobEffectInstance regen = player.getEffect(MobEffects.REGENERATION);
        if (regen == null || regen.getDuration() <= 20 || regen.getAmplifier() < amplifier) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, amplifier, false, false, true));
        }
    }

    private void applyHealthAndFlight(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        float absorbed = getDamageAbsorbed(chest);
        float mitigation = getPieceMitigationBonus(chest);

        int extraHearts = (int) (absorbed / 20.0f);
        double bonusHealth = Math.min(40.0, extraHearts * 2.0);

        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            AttributeModifier existing = healthAttr.getModifier(HEALTH_MODIFIER_UUID);
            if (existing == null || existing.getAmount() != bonusHealth) {
                healthAttr.removeModifier(HEALTH_MODIFIER_UUID);
                if (bonusHealth > 0) {
                    healthAttr.addTransientModifier(new AttributeModifier(
                            HEALTH_MODIFIER_UUID,
                            "Extra Health Bonus",
                            bonusHealth,
                            AttributeModifier.Operation.ADDITION
                    ));
                }
            }
        }

        if (mitigation >= 50.0f) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        } else if (!player.isCreative() && !player.isSpectator()) {
            if (player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    private void removeHealthAndFlight(Player player) {
        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null && healthAttr.getModifier(HEALTH_MODIFIER_UUID) != null) {
            healthAttr.removeModifier(HEALTH_MODIFIER_UUID);
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }

        if (!player.isCreative() && !player.isSpectator() && player.getAbilities().mayfly) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        float absorbed = getDamageAbsorbed(pStack);
        float mitigation = getPieceMitigationBonus(pStack);

        pTooltipComponents.add(Component.literal("Daño Absorbido: " + String.format("%.1f", absorbed)).withStyle(ChatFormatting.AQUA));
        pTooltipComponents.add(Component.literal("Mitigación de Pieza: +" + String.format("%.2f", mitigation) + "%").withStyle(ChatFormatting.GREEN));

        int extraHearts = Math.min(20, (int) (absorbed / 20.0f));
        pTooltipComponents.add(Component.literal("Vitalidad Extra: +" + extraHearts + " Corazones").withStyle(ChatFormatting.RED));

        if (mitigation >= 50.0f) {
            pTooltipComponents.add(Component.literal("Habilidad Desbloqueada: Vuelo Activo").withStyle(ChatFormatting.GOLD));
        }

        if (this.getSlot() == EquipmentSlot.FEET) {
            pTooltipComponents.add(Component.literal("Pasiva: Inmunidad al daño de caída.").withStyle(ChatFormatting.LIGHT_PURPLE));
        }

        int tier = 1;
        if (mitigation >= 80.0f) tier = 4;
        else if (mitigation >= 40.0f) tier = 3;
        else if (mitigation >= 11.0f) tier = 2;

        pTooltipComponents.add(Component.literal("Bonus de Set (Nivel " + tier + "): Resistencia, Velocidad y Regeneración").withStyle(ChatFormatting.YELLOW));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
