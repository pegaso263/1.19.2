package com.obsidianevo.item;

import com.obsidianevo.ObsidianEvoMod;
import com.obsidianevo.item.custom.EvolvingArmorItem;
import com.obsidianevo.item.custom.EvolvingSwordItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ObsidianEvoMod.MOD_ID);

    public static final RegistryObject<Item> OBSIDIAN_SWORD = ITEMS.register("obsidian_sword",
            () -> new EvolvingSwordItem(ModToolTiers.OBSIDIAN_EVO, 1, -1.0f,
                    new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).fireResistant()));

    public static final RegistryObject<Item> OBSIDIAN_HELMET = ITEMS.register("obsidian_helmet",
            () -> new EvolvingArmorItem(ModArmorMaterials.OBSIDIAN_EVO_ARMOR, EquipmentSlot.HEAD,
                    new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).fireResistant()));

    public static final RegistryObject<Item> OBSIDIAN_CHESTPLATE = ITEMS.register("obsidian_chestplate",
            () -> new EvolvingArmorItem(ModArmorMaterials.OBSIDIAN_EVO_ARMOR, EquipmentSlot.CHEST,
                    new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).fireResistant()));

    public static final RegistryObject<Item> OBSIDIAN_LEGGINGS = ITEMS.register("obsidian_leggings",
            () -> new EvolvingArmorItem(ModArmorMaterials.OBSIDIAN_EVO_ARMOR, EquipmentSlot.LEGS,
                    new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).fireResistant()));

    public static final RegistryObject<Item> OBSIDIAN_BOOTS = ITEMS.register("obsidian_boots",
            () -> new EvolvingArmorItem(ModArmorMaterials.OBSIDIAN_EVO_ARMOR, EquipmentSlot.FEET,
                    new Item.Properties().tab(CreativeModeTab.TAB_COMBAT).fireResistant()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
