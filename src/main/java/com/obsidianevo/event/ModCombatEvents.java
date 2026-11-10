package com.obsidianevo.event;

import com.obsidianevo.ObsidianEvoMod;
import com.obsidianevo.item.custom.EvolvingArmorItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ObsidianEvoMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCombatEvents {

    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            if (boots.getItem() instanceof EvolvingArmorItem) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && !player.level.isClientSide()) {
            float incomingDamage = event.getAmount();
            if (incomingDamage <= 0) return;

            float totalMitigationBonus = 0.0f;
            int equippedPieces = 0;

            EquipmentSlot[] armorSlots = new EquipmentSlot[]{
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
            };

            for (EquipmentSlot slot : armorSlots) {
                ItemStack stack = player.getItemBySlot(slot);
                if (stack.getItem() instanceof EvolvingArmorItem) {
                    equippedPieces++;
                    EvolvingArmorItem.addDamageAbsorbed(stack, incomingDamage);
                    totalMitigationBonus += EvolvingArmorItem.getPieceMitigationBonus(stack);
                }
            }

            if (equippedPieces > 0) {
                float finalReduction = Math.min(0.95f, totalMitigationBonus / 100.0f);
                float reducedDamage = incomingDamage * (1.0f - finalReduction);
                event.setAmount(reducedDamage);

                Entity attacker = event.getSource().getEntity();
                if (attacker instanceof LivingEntity livingAttacker && attacker != player) {
                    float reflectPercent = totalMitigationBonus * 0.005f;
                    float reflectedDamage = incomingDamage * reflectPercent;
                    if (reflectedDamage > 0.5f) {
                        livingAttacker.hurt(DamageSource.thorns(player), reflectedDamage);
                    }
                }
            }
        }
    }
}
